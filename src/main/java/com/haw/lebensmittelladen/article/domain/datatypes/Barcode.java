package com.haw.lebensmittelladen.article.domain.datatypes;

import lombok.AccessLevel;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.util.Random;

@Getter
@Embeddable
public class Barcode {

    @Getter(AccessLevel.NONE)
    private static final int CODE_LENGTH = 13;

    private static final String rexexp = "^[0-9]{" + CODE_LENGTH + "}$";

    private final String code;

    public Barcode() {
        StringBuilder code = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append("0123456789".charAt(r.nextInt(10)));
        }
        this.code = code.toString();
    }

    public Barcode(String barcode) {
        if (!isValid(barcode)) {
            throw new IllegalArgumentException("Invalid barcode format: " + barcode);
        }
        this.code = barcode;
    }

    public static boolean isValid(String bookingCode) {
        if (bookingCode == null)
            return false;
        else
            return bookingCode.matches(rexexp);
    }


    @Override
    public String toString() {
        return "Barcode{" +
                "code='" + code + '\'' +
                '}';
    }
}
