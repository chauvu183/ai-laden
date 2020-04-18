package com.haw.lebensmittelladen.article.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Value
@ResponseStatus(HttpStatus.BAD_REQUEST)
@EqualsAndHashCode(callSuper = false)
public class ExceptionExample extends Exception {

    private final Long bookingId;

    public ExceptionExample(Long bookingId) {
        super(String.format("Booking with number %d was not confirmed.", bookingId));

        this.bookingId = bookingId;
    }
}
