package com.movies.movieserver.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topics.movie-favorite-added}")
    private String movieFavoriteAddedTopic;

    @Value("${spring.kafka.topics.movie-favorite-removed}")
    private String movieFavoriteRemovedTopic;

    @Bean
    public NewTopic movieFavoriteAddedTopic(){
        return TopicBuilder.name(movieFavoriteAddedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic movieFavoriteRemovedTopic(){
        return TopicBuilder.name(movieFavoriteRemovedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
