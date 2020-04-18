package com.haw.lebensmittelladen;

import com.haw.lebensmittelladen.article.domain.datatypes.Email;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootApplication
@EnableSwagger2
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /*// Remove to use real mail gateway (review config in application.yml)
    @Bean
    public BookingComponentMailGateway bookingComponentMailGateway() {
        BookingComponentMailGateway bookingComponentMailGateway = Mockito.mock(BookingComponentMailGateway.class);
        when(bookingComponentMailGateway.sendMail(anyString(), anyString(), anyString())).thenReturn(true);
        return bookingComponentMailGateway;
    }

    // use mock messaging for local start of application
    @Bean
    @Profile("default")
    public BookingComponentMessagingGateway bookingComponentMessagingGateway() {
        return Mockito.mock(BookingComponentMessagingGateway.class);
    }

    @Override
    public Health health() {
        return Health.up().build();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.haw"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Hurtig-Routen REST API",
                "REST API for Architecture-Course @ HAW-Hamburg",
                "API v1",
                "Terms of service",
                new Contact("Stefan Sarstedt", "http://www.haw-hamburg.de/beschaeftigte/detailansicht/name/Stefan-Sarstedt.html",
                        "stefan.sarstedt@haw-hamburg.de"),
                "MIT License", "https://opensource.org/licenses/MIT", Collections.emptyList());
    }
}

@Component
@Profile("default")
class PopulateTestDataRunner implements CommandLineRunner {

    private final CustomerRepository customerRepository;

    @Autowired
    public PopulateTestDataRunner(CustomerRepository customerRepository) {
        super();
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(String... args) {
        Arrays.asList(
                "Miller,Doe,Smith".split(","))
                .forEach(
                        name -> customerRepository.save(new Customer("Jane", name, Gender.FEMALE, new Email(name + "@dummy.org"), null))
                );

        Customer customer = new Customer("Stefan", "Sarstedt", Gender.MALE, new Email("stefan.sarstedt@haw-hamburg.de"), null);
        Booking booking = new Booking("scandlines1");
        customer.getBookings().add(booking);
        booking = new Booking("scandlines2");
        booking.updateBookingStatus(BookingStatus.CONFIRMED);
        customer.getBookings().add(booking);

        customerRepository.save(customer);
    }*/
}
