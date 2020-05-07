package com.haw.lebensmittelladen.article.domain.dtos;

import com.haw.lebensmittelladen.article.domain.datatypes.Barcode;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ArticleCreateDTO {

    @ApiModelProperty()
    private Barcode barcode;

    @ApiModelProperty(required = true)
    @NotNull
    private String productName;

    @ApiModelProperty(required = true)
    @NotNull
    private String productFullName;

    @ApiModelProperty()
    private String company;

    @ApiModelProperty(required = true)
    @NotNull
    private Double price;

    @ApiModelProperty(required = true)
    @NotNull
    @Min(0)
    @Max(Integer.MAX_VALUE)
    private Integer quantity;
}
