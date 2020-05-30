package com.haw.lebensmittelladen.article.gateways;

import com.haw.lebensmittelladen.article.domain.datatypes.Credentials;
import com.haw.lebensmittelladen.article.exceptions.PaymentProviderException;

public interface PaymentGateway {
    public void pay(double amount, String iban) throws PaymentProviderException;
}
