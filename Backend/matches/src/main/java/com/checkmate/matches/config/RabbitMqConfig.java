package com.checkmate.matches.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class RabbitMqConfig {

    public static final String USER_EVENTS_EXCHANGE = "user_events_exchange";

    @Bean
    public FanoutExchange userEventsExchange() {
        return new FanoutExchange(USER_EVENTS_EXCHANGE);
    }

    public static final String MATCHES_MATCH_EVENT_QUEUE = "matches_match_event_queue";
    public static final String MATCH_EVENT_EXCHANGE = "match_event_exchange";

    @Bean
    Queue matchesMatchEventQueue() {
        return new Queue(MATCHES_MATCH_EVENT_QUEUE, false, false, true);
    }

    @Bean
    FanoutExchange matchEventExchange() {
        return new FanoutExchange(MATCH_EVENT_EXCHANGE);
    }

    @Bean
    Binding bindingMatchEvent(Queue matchesMatchEventQueue, FanoutExchange matchEventExchange) {
        return BindingBuilder.bind(matchesMatchEventQueue).to(matchEventExchange);
    }

    public static final String MATCH_MICROSERVICE_QUEUE = "match_microservice_queue";
    public static final String MATCH_MICROSERVICE_EXCHANGE = "match_microservice_exchange";

    @Bean
    Queue matchMicroserviceQueue() {
        return new Queue(MATCH_MICROSERVICE_QUEUE, true);
    }
    @Bean
    FanoutExchange matchMicroserviceExchange() {
        return new FanoutExchange(MATCH_MICROSERVICE_EXCHANGE);
    }
    @Bean
    Binding bindingMatchMicroserviceExchangeQueue(Queue matchMicroserviceQueue, FanoutExchange matchMicroserviceExchange) {
        return BindingBuilder.bind(matchMicroserviceQueue).to(matchMicroserviceExchange);
    }

    public static final String MATCH_MICROSERVICE_DIRECT_QUEUE = "match_microservice_direct_queue";
    public static final String MATCH_MICROSERVICE_DIRECT_EXCHANGE = "match_microservice_direct_exchange";

    @Bean
    Queue matchMicroserviceDirectQueue() {
        return new Queue(MATCH_MICROSERVICE_DIRECT_QUEUE, false, false, true);
    }
    @Bean
    DirectExchange matchMicroserviceDirectExchange() {
        return new DirectExchange(MATCH_MICROSERVICE_DIRECT_EXCHANGE);
    }
    @Bean
    Binding bindingMatchMicroservice(Queue matchMicroserviceDirectQueue, DirectExchange matchMicroserviceDirectExchange) {
        return BindingBuilder.bind(matchMicroserviceDirectQueue).to(matchMicroserviceDirectExchange).withQueueName();
    }

    public static final String MATCHMAKING_MICROSERVICE_DIRECT_EXCHANGE = "matchmaking_microservice_direct_exchange";

    @Bean
    DirectExchange matchmakingMicroserviceDirectExchange() {
        return new DirectExchange(MATCHMAKING_MICROSERVICE_DIRECT_EXCHANGE);
    }

    // JSON Message Converter
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
