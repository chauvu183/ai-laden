package com.haw.lebensmittelladen.article.domain.datatypes;

import com.haw.lebensmittelladen.article.domain.repositories.ArticleRepository;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

@Getter
@Embeddable
public class Barcode implements Serializable {

    @Getter(AccessLevel.NONE)
    private static final int CODE_LENGTH = 13;
    private static ArticleRepository articleRepository;




    private static final String rexexp = "^[0-9]{" + CODE_LENGTH + "}$";

    //@Column(unique=true, nullable=false)
    @NotBlank
    @Pattern(regexp = rexexp, flags = Pattern.Flag.UNICODE_CASE)
    private final String code;

    public Barcode() {
        this.code = generateBarcode();
    }

    public String generateBarcode(){
        StringBuilder code = new StringBuilder();
        Random r = new Random();
        boolean exists = true;
        while (exists){
            for (int i = 0; i < CODE_LENGTH; i++) {
                code.append("0123456789".charAt(r.nextInt(10)));
            }
            if(articleRepository.findByBarcode(new Barcode(code.toString())).isEmpty()){
                exists = false;
            } else {
                code = new StringBuilder();
            }
        }
        return code.toString();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Barcode)) return false;
        Barcode barcode = (Barcode) o;
        return getCode().equals(barcode.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode());
    }

    @Override
    public String toString() {
        return "Barcode{" +
                "code='" + code + '\'' +
                '}';
    }
}
