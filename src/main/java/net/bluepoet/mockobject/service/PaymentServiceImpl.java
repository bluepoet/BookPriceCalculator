package net.bluepoet.mockobject.service;

import net.bluepoet.mockobject.model.Payment;

/**
 * Created by poet.me on 15. 12. 28..
 */
public class PaymentServiceImpl implements PaymentService {
    private Payment payment;

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @Override
    public int pay(int price) {
        return payment.pay(price);
    }
}
