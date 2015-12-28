package net.bluepoet.mockobject.model;

import java.util.Optional;

/**
 * Created by poet.me on 15. 12. 28..
 */
public class VisaCardPayment extends Payment {
    public VisaCardPayment() {
        setDiscountRate(Optional.of(0.08));
    }
}
