package com.haw.lebensmittelladen.article.gateways;

import com.haw.lebensmittelladen.article.domain.datatypes.Credentials;
import org.springframework.stereotype.Service;

@Service
public class BankPaymentGateway implements PaymentGateway {
    @Override
    public boolean pay(double amount, Credentials cred) {
        //TODO
        return false;
    }
}
