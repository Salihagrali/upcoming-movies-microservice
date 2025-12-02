package com.movies.movieserver.movie.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieEventProducer {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    @Value("${spring.kafka.topics.movie-favorite-added}")
    private String movieFavoriteAddedTopic;

    @Value("${spring.kafka.topics.movie-favorite-removed}")
    private String movieFavoriteRemovedTopic;

    public void publishMovieFavoriteAdded(MovieFavoriteAddedEvent event){
        //set metadata
        event.setEventTimestamp(LocalDateTime.now());
        event.setEventId(UUID.randomUUID().toString());

        //partition key
        String key = event.getUserId();

        log.info("Publishing MovieFavoriteAddedEvent: userId={}, movieId={}, eventId={}",
                event.getUserId(), event.getMovieId(), event.getEventId());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(movieFavoriteAddedTopic, key, event);

        future.whenComplete((result,ex) -> {
            if(ex == null){
                log.info("Successfully published event to Kafka: topic={}, partition={}, offset={}",
                        movieFavoriteAddedTopic,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }else{
                log.error("Failed to publish event to Kafka: {}", ex.getMessage(), ex);
            }
        });
    }

    public void publishMovieFavoriteRemoved(MovieFavoriteRemovedEvent event){
        event.setEventTimestamp(LocalDateTime.now());
        event.setEventId(UUID.randomUUID().toString());

        String key = event.getUserId();

        log.info("Publishing MovieFavoriteRemovedEvent: userId={}, movieId={}, eventId={}",
                event.getUserId(), event.getMovieId(), event.getEventId());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(movieFavoriteRemovedTopic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully published event to Kafka: topic={}, offset={}",
                        movieFavoriteRemovedTopic, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish event to Kafka: {}", ex.getMessage(), ex);
            }
        });
    }
}
