package com.haw.lebensmittelladen.article.domain.datatypes;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor
@Embeddable
public class Email {

    /**
     * @see <a href="https://www.oreilly.com/library/view/regular-expressions-cookbook/9781449327453/ch04s01.html" />
     */
    private static final String EMAIL_PATTERN =
            "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,6}$";

    private String email;

    public Email(String email) {
        if (!isValid(email)) {
            throw new IllegalArgumentException("Invalid email: " + email);
        }

        this.email = email;
    }

    public static boolean isValid(String email) {
        if (email == null)
            return false;
        else
            return email.matches(EMAIL_PATTERN);
    }
}

