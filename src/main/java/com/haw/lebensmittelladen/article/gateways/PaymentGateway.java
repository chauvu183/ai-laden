package com.haw.lebensmittelladen.article.gateways;

import com.haw.lebensmittelladen.article.domain.datatypes.Credentials;

public interface PaymentGateway {
    public boolean pay(double amount, Credentials cred);
}
