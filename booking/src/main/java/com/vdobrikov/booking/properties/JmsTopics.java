package com.vdobrikov.booking.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "jms.topic")
@ConstructorBinding
public record JmsTopics(String booking, String status) {
}
