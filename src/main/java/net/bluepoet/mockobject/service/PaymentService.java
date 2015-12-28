package net.bluepoet.mockobject.service;

import net.bluepoet.mockobject.model.Payment;

/**
 * Created by poet.me on 15. 12. 28..
 */
public interface PaymentService {
    void setPayment(Payment payment);
    int pay(int price);
}
