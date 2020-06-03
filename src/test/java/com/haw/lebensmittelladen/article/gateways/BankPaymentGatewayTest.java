package com.haw.lebensmittelladen.article.gateways;

import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;


@ExtendWith(SpringExtension.class)
@ActiveProfiles(profiles = "testing")
@RestClientTest(BankPaymentGateway.class)
@ContextConfiguration(classes = { BankPaymentGateway.class, RestTemplate.class })
public class BankPaymentGatewayTest {

    double amount = 5;
    String theirIban = "DE48794319732728991296";

    String validResponse = "\"transactionCode\": {\n" +
            "        \"code\": \"P6MDP2\"\n" +
            "    },\n" +
            "    \"createdOn\": \"2020-05-31T17:26:47.256+0000\",\n" +
            "    \"ibanTo\": \"" + theirIban + "\",\n" +
            "    \"ibanFrom\": \"OurIban\",\n" +
            "    \"transactionAmount\": " + -amount + "";
    @Autowired
    private BankPaymentGateway bankPaymentGateway;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer server;

    @BeforeEach
    public void setUp() {
        server= MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void createAccountSuccess() throws PaymentProviderException {
        server
                .expect(requestTo(bankPaymentGateway.HOST + BankPaymentGateway.TRANSACTION_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .body(validResponse)
                        .contentType(MediaType.APPLICATION_JSON));
        bankPaymentGateway.pay(amount, theirIban);
    }

    @Test
    void createAccountFail() {
        server
                .expect(requestTo(bankPaymentGateway.HOST + BankPaymentGateway.TRANSACTION_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        assertThrows(PaymentProviderException.class, () -> bankPaymentGateway.pay(amount, theirIban));
    }

}
