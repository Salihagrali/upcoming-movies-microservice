package com.movies.favoriteservice.favorite.event;

import com.movies.favoriteservice.favorite.FavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovieFavoriteEventConsumer {
    private final FavoriteService favoriteService;

    @KafkaListener(
            topics = "${spring.kafka.topics.movie-favorite-added}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeMovieFavoriteAdded(
            @Payload MovieFavoriteAddedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received MovieFavoriteAddedEvent: eventId={}, userId={}, movieId={}, partition={}, offset={}",
                event.getEventId(), event.getUserId(), event.getMovieId(), partition, offset);

        try {
            favoriteService.addFavorite(event);
            log.info("Successfully processed MovieFavoriteAddedEvent: eventId={}", event.getEventId());

        } catch (Exception e) {
            log.error("Error processing MovieFavoriteAddedEvent: eventId={}, error={}",
                    event.getEventId(), e.getMessage(), e);
            // In production:
            // - Retry logic
            // - Dead letter queue
            // - Alert monitoring system
        }
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.movie-favorite-removed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeMovieFavoriteRemoved(
            @Payload MovieFavoriteRemovedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received MovieFavoriteRemovedEvent: eventId={}, userId={}, movieId={}, partition={}, offset={}",
                event.getEventId(), event.getUserId(), event.getMovieId(), partition, offset);

        try {
            favoriteService.removeFavorite(event);
            log.info("Successfully processed MovieFavoriteRemovedEvent: eventId={}", event.getEventId());

        } catch (Exception e) {
            log.error("Error processing MovieFavoriteRemovedEvent: eventId={}, error={}",
                    event.getEventId(), e.getMessage(), e);
        }
    }
}
