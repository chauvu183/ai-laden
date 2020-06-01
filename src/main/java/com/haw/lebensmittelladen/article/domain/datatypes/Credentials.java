package com.haw.lebensmittelladen.article.domain.datatypes;

import lombok.Getter;


@Getter
public class Credentials {
    String password;
    String account;

    public Credentials(String pass, String login) {
        password = pass;
        account = login;
    }
}
