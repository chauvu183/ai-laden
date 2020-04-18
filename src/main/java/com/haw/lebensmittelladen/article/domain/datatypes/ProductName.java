package com.haw.lebensmittelladen.article.domain.datatypes;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@NoArgsConstructor
@Embeddable
public class ProductName {

    private String companyName;

    private String productName;

}
