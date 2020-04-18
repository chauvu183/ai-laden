package com.haw.lebensmittelladen.article;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("testing")
@Configuration
public class TestConfiguration {

    @Bean
    public BookingComponentMailGateway bookingComponentMailGateway() {
        return Mockito.mock(BookingComponentMailGateway.class);
    }

    @Bean
    public BookingComponentMessagingGateway bookingComponentMessagingGateway() {
        return Mockito.mock(BookingComponentMessagingGateway.class);
    }
}
