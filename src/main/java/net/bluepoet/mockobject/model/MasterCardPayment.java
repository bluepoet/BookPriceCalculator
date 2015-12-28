package net.bluepoet.mockobject.model;

import java.util.Optional;

/**
 * Created by poet.me on 15. 12. 28..
 */
public class MasterCardPayment extends Payment{
    public MasterCardPayment() {
        setDiscountRate(Optional.of(0.05));
    }
}
