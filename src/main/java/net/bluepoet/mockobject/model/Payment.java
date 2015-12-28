package net.bluepoet.mockobject.model;

import lombok.Data;

import java.util.Optional;

/**
 * Created by poet.me on 15. 12. 28..
 */
@Data
public class Payment {
    private Optional<Double> discountRate = Optional.empty();

    public int pay(int price) {
        return discountRate.map(r -> new Double(price - (price * r)).intValue()).orElse(price);
    }
}
