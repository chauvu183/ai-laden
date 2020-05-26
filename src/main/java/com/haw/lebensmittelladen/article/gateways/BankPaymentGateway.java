package com.haw.lebensmittelladen.article.gateways;

import com.haw.lebensmittelladen.article.domain.datatypes.Credentials;
import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;
import org.springframework.stereotype.Service;

@Service
public class BankPaymentGateway implements PaymentGateway {

    @Override
    public void pay(double amount, String iban) throws PaymentProviderException {
        throw new PaymentProviderException("not implemented");
        //TODO
        //return;
    }
}
