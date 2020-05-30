package com.haw.lebensmittelladen.article.domain.dtos;

import com.haw.lebensmittelladen.article.util.constraints.IBANConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ArticleBuyBankDTO {

    //als extra objekt, da unklar, ob mit oder ohne passwort?
    @NotNull
    @IBANConstraint
    private String iBan;
}
