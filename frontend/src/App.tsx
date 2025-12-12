import { useState, useEffect, useCallback } from 'react';
import { Play, Info } from 'lucide-react';
import type { MovieData } from './types/movie';
import { Navbar } from './components/common/Navbar';
import { MovieSlider } from './components/movies/MovieSlider';
import { MovieDetailsModal } from './components/movies/MovieDetailsModal';
import { useAuth } from 'react-oidc-context';

const API_BASE_URL = 'http://localhost:9000';

export default function App() {
  const auth = useAuth();

  const [upcomingMovies, setUpcomingMovies] = useState<MovieData[]>([]);
  const [nowPlayingMovies, setNowPlayingMovies] = useState<MovieData[]>([]);
  const [topPicks, setTopPicks] = useState<MovieData[]>([]);
  const [selectedMovie, setSelectedMovie] = useState<MovieData | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isLoadingData, setIsLoadingData] = useState(false); // New loading state for data

  // NEW: State for the Movie to feature in the Hero section
    const [heroMovie, setHeroMovie] = useState<MovieData | null>(null);

  // 1. Data Fetching Function (Reusable)
    const fetchMovieData = useCallback(async (endpoint: string, setter: React.Dispatch<React.SetStateAction<MovieData[]>>) => {
        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`);
            if (!response.ok) {
                throw new Error(`Failed to fetch data from ${endpoint}. Status: ${response.status}`);
            }
            const data: MovieData[] = await response.json();
            setter(data);
            return data; // Return data for setting hero movie
        } catch (error) {
            console.error("API Fetch Error:", error);
            setter([]); 
            return [];
        }
    }, []);

  // 2. Effect to fetch movies and set the hero movie
    useEffect(() => {
        setIsLoadingData(true);
        const fetchAllData = async () => {
            // Fetch upcoming movies and capture the result
            const upcoming = await fetchMovieData('/movies/upcomingMovies', setUpcomingMovies);
            
            // Fetch now playing movies concurrently
            await fetchMovieData('/movies/nowPlaying', setNowPlayingMovies);

            // Set the Hero movie using the first movie from upcoming
            if (upcoming.length > 0) {
                setHeroMovie(upcoming[0]);
            }

            setIsLoadingData(false);
        };
        
        fetchAllData();
    }, [fetchMovieData]);

    // Combined Loading State
    if (auth.isLoading || isLoadingData || !heroMovie) {
        return (
            <div className="min-h-screen bg-[#141414] flex items-center justify-center">
                <div className="text-red-600 font-bold text-2xl animate-pulse">Loading Movie Data...</div>
            </div>
        );
    }

    const handleMovieClick = (movie: MovieData) => {
        setSelectedMovie(movie);
        setIsModalOpen(true);
    };

  // 3. UPDATED API Logic for Favorites (Toggles POST/DELETE)
    const toggleFavorite = async (movie: MovieData, isFavorite: boolean) => {
        if (!auth.isAuthenticated || !auth.user?.access_token) {
            alert("Please log in to add to favorites!");
            return;
        }

        const method = isFavorite ? 'DELETE' : 'POST';
        const action = isFavorite ? 'Removing' : 'Adding';

        // Payload is only necessary for POST (Adding), but included for consistency
        const payload = {
            id: movie.id, 
            title: movie.title,
            // React (camelCase) -> Java Backend (snake_case)
            original_title: movie.original_title, 
            overview: movie.overview,
            release_date: movie.release_date,
            poster_path: movie.poster_path,
            backdrop_path: movie.backdrop_path,
            popularity: movie.popularity,
            vote_average: movie.vote_average,
            vote_count: movie.vote_count,
            genre_ids: movie.genre_ids,
            original_language: movie.original_language,
            adult: movie.adult,
            video: movie.video
        };

        try {
            console.log(`${action} favorite request for:`, movie.title);
            
            const response = await fetch(`${API_BASE_URL}/movies/${movie.id}/favorite`, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${auth.user.access_token}`
                },
                // Only send body for POST request
                body: method === 'POST' ? JSON.stringify(payload) : undefined
            });

            if (!response.ok) throw new Error(`Failed to ${action.toLowerCase()} favorite. Status: ${response.status}`);
            console.log("Favorite updated successfully!");
            // You might add logic here to refresh the favorite list or update local state
        } catch (error) {
            console.error("API Error:", error);
            alert(`Failed to ${action.toLowerCase()} favorites. Check console.`);
        }
    };

  return (
    <div className="min-h-screen bg-[#141414] text-white font-sans overflow-x-hidden">
      <Navbar 
        isLoggedIn={auth.isAuthenticated}
        userName={auth.user?.profile.given_name || auth.user?.profile.preferred_username}
        onLoginClick={() => auth.signinRedirect()}
        onRegisterClick={() => auth.signinRedirect({ extraQueryParams: { prompt: "create" } })} // Forces Keycloak registration page
        onLogoutClick={() => auth.signoutRedirect()}
      />

      {/* UPDATED HERO SECTION to use dynamic heroMovie data */}
            <div className="relative h-[85vh] w-full mb-4">
                <div className="absolute inset-0">
                    <img 
                        src={
                            heroMovie.backdrop_path 
                                ? `https://image.tmdb.org/t/p/original${heroMovie.backdrop_path}` 
                                : "https://placehold.co/1920x1080/1a1a1a/ffffff?text=Featured+Blockbuster"
                        } 
                        className="w-full h-full object-cover"
                        alt={heroMovie.title}
                    />
                    <div className="absolute inset-0 bg-linear-to-r from-[#141414] via-[#141414]/30 to-transparent" />
                    <div className="absolute inset-0 bg-linear-to-t from-[#141414] via-transparent to-transparent" />
                </div>
                
                <div className="absolute top-[45%] left-0 transform -translate-y-1/2 p-8 md:p-16 space-y-6 max-w-2xl z-10">
                    {/* Conditional Personal Greeting */}
                    {auth.isAuthenticated && (
                        <div className="flex items-center gap-2 text-gray-300 font-medium tracking-wide uppercase text-sm animate-in fade-in slide-in-from-left-4 duration-500">
                            <span className="w-1 h-4 bg-red-600 block"></span>
                            Welcome back, {auth.user?.profile.preferred_username || "User"}!
                        </div>
                    )}

                    {/* DYNAMIC TITLE */}
                    <h1 className="text-5xl md:text-7xl font-black drop-shadow-2xl leading-tight">
                        {heroMovie.title.toUpperCase()}
                        <br/> 
                        {heroMovie.original_title && heroMovie.original_title !== heroMovie.title && (
                            <span className="text-3xl md:text-4xl font-light text-gray-200">{heroMovie.original_title}</span>
                        )}
                    </h1>
                    
                    {/* DYNAMIC OVERVIEW */}
                    <p className="text-lg text-gray-200 drop-shadow-md line-clamp-3">
                        {heroMovie.overview}
                    </p>
                    
                    <div className="flex gap-4 pt-2">
                        <button 
                            className="bg-gray-500/40 backdrop-blur-md text-white px-8 py-3 rounded font-bold hover:bg-gray-500/60 transition flex items-center gap-2 text-lg"
                            onClick={() => handleMovieClick(heroMovie)}
                        >
                            <Info size={24} /> More Info
                        </button>
                    </div>
                </div>
            </div>

      {/* Content Rows */}
      <div className="relative z-20 space-y-2 md:-mt-24 pl-0">
        {auth.isAuthenticated && (
            <MovieSlider 
                title="Top Picks for You" 
                movies={topPicks} 
                onMovieClick={handleMovieClick}
                onFavoriteClick={toggleFavorite}
            />
        )}
        
        <MovieSlider 
          title="Upcoming Movies" 
          movies={upcomingMovies} 
          onMovieClick={handleMovieClick}
          onFavoriteClick={toggleFavorite}
        />
        
        <MovieSlider 
          title="Now Playing in Theaters" 
          movies={nowPlayingMovies} 
          onMovieClick={handleMovieClick}
          onFavoriteClick={toggleFavorite}
        />
      </div>

      <MovieDetailsModal 
        movie={selectedMovie} 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)}
        onFavoriteToggle={toggleFavorite}
      />
    </div>
  );
}