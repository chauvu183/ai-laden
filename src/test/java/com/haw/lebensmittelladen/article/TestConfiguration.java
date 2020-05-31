package com.haw.lebensmittelladen.article;

import com.haw.lebensmittelladen.article.gateways.BankPaymentGateway;
import com.haw.lebensmittelladen.article.gateways.PaymentGateway;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/*@Profile("testing")
@Configuration
public class TestConfiguration {

    @Bean
    public BankPaymentGateway bankPaymentGateway() {
        return Mockito.mock(BankPaymentGateway.class);
    }

}*/
