package com.mszychiewicz.parking.payments;

import lombok.Data;

@Data
public class Money {
    private Double amount;
    private String currency;

    public Money(Double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }
}
