package com.haw.lebensmittelladen.article.gateways;

import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class BankPaymentGateway implements PaymentGateway {
    public static final String HOST = "http://AIBANK";
    public static final String TRANSACTION_URL = "/accounts/customer/transactions";
    //public static final String CUSTOMER_URL = "/accounts/customer";
    //public static final String ACCOUNT_CREATE_URL = "/accounts/create";

    private String myIban = "DE48794319732728991292";
    private static final String PASSWORD = "test";

    @LoadBalanced
    RestTemplate restTemplate;

    public BankPaymentGateway(RestTemplateBuilder templateBuilder) {
        this.restTemplate = templateBuilder.build();
    }

    @Override
    public void pay(double amount, String iban) throws PaymentProviderException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(myIban, PASSWORD);
        headers.setContentType(MediaType.APPLICATION_JSON);
        BankPayDTO transferBody = new BankPayDTO(iban, myIban, -amount);
        HttpEntity<BankPayDTO> body = new HttpEntity<>(transferBody, headers);
        try {
            restTemplate.exchange(HOST + TRANSACTION_URL, HttpMethod.POST, body, String.class);
        } catch (HttpClientErrorException httpClientErrorException) {
            throw new PaymentProviderException(httpClientErrorException.getStatusCode().value() + " " +
                    httpClientErrorException.getStatusCode().getReasonPhrase());
        }
    }

    /*protected boolean testMyAccount() throws PaymentProviderException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(myIban, PASSWORD);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> entity = restTemplate.exchange(HOST + CUSTOMER_URL, HttpMethod.GET, request, String.class);
        if (entity.getStatusCode().is2xxSuccessful()) {
            return true;
        } else if (entity.getStatusCode().isError()) {
            throw new PaymentProviderException(entity.getStatusCode().value() + " " + entity.getStatusCode().getReasonPhrase());
        }
        return false;
    }

    protected BankCreateResponseDTO createMyAccount() throws PaymentProviderException {
        HttpHeaders headers = new HttpHeaders();
        BankCreateDTO createBody = new BankCreateDTO(PASSWORD);
        HttpEntity<BankCreateDTO> body = new HttpEntity<>(createBody, headers);
        ResponseEntity<String> entity = restTemplate.postForEntity(HOST + ACCOUNT_CREATE_URL, body, String.class);
        if (entity.getStatusCode().isError()) {
            throw new PaymentProviderException(entity.getStatusCode().value() + " " + entity.getStatusCode().getReasonPhrase());
        }
        BankCreateResponseDTO resp = new Gson().fromJson(entity.getBody(), BankCreateResponseDTO.class);
        return resp;
    }*/

    static class BankPayDTO {
        private String ibanTo;
        private String ibanFrom;
        private Double transactionAmount;

        public BankPayDTO(String ibanTo, String ibanFrom, Double transactionAmount) {
            this.ibanTo = ibanTo;
            this.ibanFrom = ibanFrom;
            this.transactionAmount = transactionAmount;
        }

        public String getIbanTo() {
            return ibanTo;
        }

        public String getIbanFrom() {
            return ibanFrom;
        }

        public Double getTransactionAmount() {
            return transactionAmount;
        }
    }

    /*static class BankCreateDTO {
        private String password;

        public BankCreateDTO(String password) {
            this.password = password;
        }

        public String getPassword() {
            return password;
        }
    }

    static class BankCreateResponseDTO {
        private String iban;
        private String password;

        public BankCreateResponseDTO(String iban, String password) {
            this.iban = iban;
            this.password = password;
        }

        public String getIban() {
            return iban;
        }

        public String getPassword() {
            return password;
        }
    }*/
}
