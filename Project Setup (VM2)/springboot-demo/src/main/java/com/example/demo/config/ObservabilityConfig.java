package com.example.demo.config;

import feign.micrometer.MicrometerObservationCapability;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the aspect that turns {@code @Observed} annotations into spans/metrics.
 * Without this bean, {@code @Observed} on service methods would be ignored.
 */
@Configuration
public class ObservabilityConfig {

    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

    /**
     * Ensures Feign client calls create a client span and inject the W3C traceparent
     * header so App 2 continues the same trace as App 1.
     */
    @Bean
    MicrometerObservationCapability micrometerObservationCapability(ObservationRegistry observationRegistry) {
        return new MicrometerObservationCapability(observationRegistry);
    }
}
