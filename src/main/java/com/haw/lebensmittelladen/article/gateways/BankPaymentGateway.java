package com.haw.lebensmittelladen.article.gateways;

import com.google.gson.Gson;
import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class BankPaymentGateway implements PaymentGateway {
    private static final String PASSWORD = "test";
    private static final String HOST = "http://AIBANK";
    private String myIban = "DE48794319732728991292";

    @Autowired
    RestTemplate restTemplate;

    @Override
    public void pay(double amount, String iban) throws PaymentProviderException {
        //todo: get a fixed shop iban/password
        if (!testMyAccount()) {
            BankCreateResponseDTO myCreds = createMyAccount();
            myIban = myCreds.iban;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(myIban, PASSWORD);
        headers.setContentType(MediaType.APPLICATION_JSON);
        BankPayDTO transferBody = new BankPayDTO(myIban, iban, amount);
        System.out.println(new Gson().toJson(transferBody));
        HttpEntity<String> body = new HttpEntity<>(new Gson().toJson(transferBody), headers);
        //ResponseEntity<String> entity = restTemplate.postForEntity(HOST+"/accounts/customer/transactions", body, String.class);
        //headers.set();
        System.out.println(headers.getContentType());
        System.out.println(body);
        try {
            ResponseEntity<String> entity = restTemplate.exchange(HOST + "/accounts/customer/transactions", HttpMethod.POST, body, String.class);
            if (!entity.getStatusCode().is2xxSuccessful()) {
                throw new PaymentProviderException(entity.getStatusCode().value() + " " + entity.getStatusCode().getReasonPhrase());
            }
        } catch (Exception exception){
            System.out.println(exception);
            throw exception;
        }
    }

    private boolean testMyAccount() throws PaymentProviderException {
        final String ressource = "/accounts/customer";
        HttpHeaders headers = new HttpHeaders();
        //headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(myIban, PASSWORD);
        HttpEntity<String> request = new HttpEntity<>(headers);
        System.out.println(HOST + ressource);
        ResponseEntity<String> entity = restTemplate.exchange(HOST + ressource, HttpMethod.GET, request, String.class);
        //restTemplate.getForEntity(HOST+ressource, String.class);
        if (entity.getStatusCode().is2xxSuccessful()) {
            return true;
        } else if (entity.getStatusCode().isError()) {
            throw new PaymentProviderException(entity.getStatusCode().value() + " " + entity.getStatusCode().getReasonPhrase());
        }
        return false;
    }

    private BankCreateResponseDTO createMyAccount() throws PaymentProviderException {
        final String ressource = "/accounts/create";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        BankCreateDTO createBody = new BankCreateDTO(PASSWORD);
        HttpEntity<BankCreateDTO> body = new HttpEntity<>(createBody, headers);
        ResponseEntity<String> entity = restTemplate.postForEntity(HOST + ressource, body, String.class);
        if (entity.getStatusCode().isError()) {
            throw new PaymentProviderException(entity.getStatusCode().value() + " " + entity.getStatusCode().getReasonPhrase());
        }
        BankCreateResponseDTO resp = new Gson().fromJson(entity.getBody(), BankCreateResponseDTO.class);
        return resp;
    }

    class BankPayDTO {
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

    class BankCreateDTO {
        private String password;

        public BankCreateDTO(String password) {
            this.password = password;
        }

        public String getPassword() {
            return password;
        }
    }

    class BankCreateResponseDTO {
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
    }
}
