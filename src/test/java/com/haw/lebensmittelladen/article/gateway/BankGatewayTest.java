package com.haw.lebensmittelladen.article.gateway;

import com.haw.lebensmittelladen.Application;
import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import com.haw.lebensmittelladen.article.gateways.BankPaymentGateway;
import org.hibernate.validator.internal.IgnoreForbiddenApisErrors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureJsonTesters
@ActiveProfiles(profiles = "testing")
public class BankGatewayTest {

    @Autowired
    BankPaymentGateway bankPaymentGateway;

    //this is just for triggering the gateway
    @Test
    void paymentTest() throws PaymentProviderException {
        bankPaymentGateway.pay(12.50,"DE74512108001245126198");
    }
}
