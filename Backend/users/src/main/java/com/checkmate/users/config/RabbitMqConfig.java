package com.checkmate.users.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String USERS_USER_EVENTS_QUEUE = "users_user_events_queue";
    public static final String USER_EVENTS_EXCHANGE = "user_events_exchange";

    @Bean
    Queue queue() {
        return new Queue(USERS_USER_EVENTS_QUEUE, true);
    }

    @Bean
    FanoutExchange exchange() {
        return new FanoutExchange(USER_EVENTS_EXCHANGE);
    }

    @Bean
    Binding binding(Queue queue, FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
