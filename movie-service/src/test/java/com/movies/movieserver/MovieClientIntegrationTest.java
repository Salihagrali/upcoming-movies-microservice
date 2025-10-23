package com.movies.movieserver;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.movies.movieserver.movie.Movie;
import com.movies.movieserver.movie.MovieService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieClientIntegrationTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private RedisTemplate<String, Movie> redisTemplate;

    private static WireMockServer wireMockServer;

    @Container
//  @ServiceConnection --autoconfigures with spring boot--
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379) // ← Expose Redis's internal port 6379
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Configure Redis connection from Testcontainer
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);

        // Override Feign client URL to point to WireMock
        registry.add("movie.api.url", () -> "http://localhost:" + wireMockServer.port());
    }

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();

        System.out.println("WireMock started on port: " + wireMockServer.port());
        System.out.println("Redis started on port: " + redisContainer.getFirstMappedPort());
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void setUp() {
        // Clear Redis cache before each test
        redisTemplate.getConnectionFactory().getConnection().flushAll();

        // Reset WireMock
        wireMockServer.resetAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should fetch movies from API and cache them in Redis")
    void shouldFetchFromApiAndCache() {
        // Given: WireMock stub for the movie API
        wireMockServer.stubFor(get(urlPathEqualTo("/discover/movie"))
                .withQueryParam("language", equalTo("en-US"))
                .withQueryParam("page", equalTo("1"))
                .withQueryParam("region", equalTo("US"))
                .withQueryParam("with_release_type", equalTo("2|3"))
                .withQueryParam("primary_release_date.gte", matching("\\d{4}-\\d{2}-\\d{2}"))
                .withHeader("Authorization", containing("Bearer"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "page": 1,
                                    "results": [
                                        {
                                            "id": 101,
                                            "title": "Inception",
                                            "release_date": "2025-11-01",
                                            "overview": "A mind-bending thriller"
                                        },
                                        {
                                            "id": 102,
                                            "title": "The Matrix Resurrections",
                                            "release_date": "2025-11-15",
                                            "overview": "Return to the Matrix"
                                        }
                                    ],
                                    "total_pages": 10,
                                    "total_results": 200
                                }
                                """)));

        // When: Fetch movies from the service
        List<Movie> movies = movieService.fetchUpcomingMovies(1);

        // Then: Verify the response
        assertNotNull(movies, "Movies list should not be null");
        assertEquals(2, movies.size(), "Should return 2 movies");

        // Verify first movie
        assertEquals(101, movies.get(0).id());
        assertEquals("Inception", movies.get(0).title());
        assertEquals("2025-11-01", movies.get(0).release_date());

        // Verify second movie
        assertEquals(102, movies.get(1).id());
        assertEquals("The Matrix Resurrections", movies.get(1).title());

        // Verify the API was called
        wireMockServer.verify(1, getRequestedFor(urlPathEqualTo("/discover/movie"))
                .withQueryParam("page", equalTo("1"))
                .withHeader("Authorization", matching("Bearer test-api-key-12345")));

        // Verify data is cached in Redis
        List<Movie> cachedMovies = redisTemplate.opsForList()
                .range("upcomingMovies:page_1", 0, -1);

        assertNotNull(cachedMovies, "Cached movies should not be null");
        assertEquals(2, cachedMovies.size(), "Should have 2 cached movies");
        assertEquals("Inception", cachedMovies.get(0).title());
    }

    @Test
    @Order(2)
    @DisplayName("Should return cached data on second call without hitting API")
    void shouldReturnCachedData() {
        // Given: Populate cache first
        wireMockServer.stubFor(get(urlPathEqualTo("/discover/movie"))
                .withQueryParam("page", equalTo("1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "page": 1,
                                    "results": [
                                        {
                                            "id": 201,
                                            "title": "Dune Part 3",
                                            "release_date": "2025-12-01"
                                        }
                                    ],
                                    "total_pages": 5,
                                    "total_results": 50
                                }
                                """)));

        // First call - should hit API
        List<Movie> firstCall = movieService.fetchUpcomingMovies(1);
        assertEquals(1, firstCall.size());
        assertEquals("Dune Part 3", firstCall.get(0).title());

        // Verify API was called once
        wireMockServer.verify(1, getRequestedFor(urlPathEqualTo("/discover/movie")));

        // When: Make second call (should use cache)
        List<Movie> secondCall = movieService.fetchUpcomingMovies(1);

        // Then: Should return same data from cache
        assertEquals(1, secondCall.size());
        assertEquals("Dune Part 3", secondCall.get(0).title());
        assertEquals(201, secondCall.get(0).id());

        // API should still have been called only once (not twice)
        wireMockServer.verify(1, getRequestedFor(urlPathEqualTo("/discover/movie")));
    }

    @Test
    @Order(3)
    @DisplayName("Should fetch different pages independently")
    void shouldFetchDifferentPages() {
        // Given: Stub for page 1
        wireMockServer.stubFor(get(urlPathEqualTo("/discover/movie"))
                .withQueryParam("page", equalTo("1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "page": 1,
                                    "results": [
                                        {"id": 301, "title": "Movie Page 1", "release_date": "2025-11-01"}
                                    ]
                                }
                                """)));

        // Given: Stub for page 2
        wireMockServer.stubFor(get(urlPathEqualTo("/discover/movie"))
                .withQueryParam("page", equalTo("2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "page": 2,
                                    "results": [
                                        {"id": 302, "title": "Movie Page 2", "release_date": "2025-11-05"}
                                    ]
                                }
                                """)));

        // When: Fetch both pages
        List<Movie> page1 = movieService.fetchUpcomingMovies(1);
        List<Movie> page2 = movieService.fetchUpcomingMovies(2);

        // Then: Verify different results
        assertEquals("Movie Page 1", page1.get(0).title());
        assertEquals("Movie Page 2", page2.get(0).title());

        // Verify both API calls were made
        wireMockServer.verify(1, getRequestedFor(urlPathEqualTo("/discover/movie"))
                .withQueryParam("page", equalTo("1")));
        wireMockServer.verify(1, getRequestedFor(urlPathEqualTo("/discover/movie"))
                .withQueryParam("page", equalTo("2")));

        // Verify both pages are cached separately
        List<Movie> cachedPage1 = redisTemplate.opsForList()
                .range("upcomingMovies:page_1", 0, -1);
        List<Movie> cachedPage2 = redisTemplate.opsForList()
                .range("upcomingMovies:page_2", 0, -1);

        assertNotNull(cachedPage1);
        assertNotNull(cachedPage2);
        assertEquals(1, cachedPage1.size());
        assertEquals(1, cachedPage2.size());
    }

    @Test
    @Order(4)
    @DisplayName("Should handle API errors gracefully")
    void shouldHandleApiErrors() {
        // Given: WireMock returns error
        wireMockServer.stubFor(get(urlPathEqualTo("/discover/movie"))
                .withQueryParam("page", equalTo("999"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "status_message": "Internal Server Error",
                                    "status_code": 500
                                }
                                """)));

        // When/Then: Should throw exception
        assertThrows(Exception.class, () -> {
            movieService.fetchUpcomingMovies(999);
        });

        // Verify API was called
        wireMockServer.verify(1, getRequestedFor(urlPathEqualTo("/discover/movie"))
                .withQueryParam("page", equalTo("999")));
    }

//    @Test
//    @Order(5)
//    @DisplayName("Should verify request headers are sent correctly")
//    void shouldSendCorrectHeaders() {
//        // Given
//        wireMockServer.stubFor(get(urlPathEqualTo("/discover/movie"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody("""
//                                {
//                                    "page": 1,
//                                    "results": []
//                                }
//                                """)));
//
//        // When
//        movieService.fetchUpcomingMovies(1);
//
//        // Then: Verify all required headers and params were sent
//        wireMockServer.verify(getRequestedFor(urlPathEqualTo("/discover/movie"))
//                .withHeader("Authorization", equalTo("Bearer test-api-key-12345"))
//                .withQueryParam("language", equalTo("en-US"))
//                .withQueryParam("region", equalTo("US"))
//                .withQueryParam("with_release_type", equalTo("2|3"))
//                .withQueryParam("primary_release_date.gte", matching("\\d{4}-\\d{2}-\\d{2}")));
//    }
}