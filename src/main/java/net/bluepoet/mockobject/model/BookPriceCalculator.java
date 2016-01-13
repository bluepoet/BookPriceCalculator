package net.bluepoet.mockobject.model;

import lombok.Data;
import net.bluepoet.mockobject.service.BookService;
import net.bluepoet.mockobject.service.PaymentService;
import net.bluepoet.mockobject.service.UserService;

import java.util.Optional;

/**
 * Created by poet.me on 15. 12. 28..
 */
@Data
public class BookPriceCalculator {
    private UserService userService;
    private BookService bookService;
    private PaymentService paymentService;

    public int calculate(int userNo, int bookNo) {
        Optional<Book> book = bookService.getBookByNo(bookNo);
        Optional<User> user = userService.getUserByNo(userNo);
        double totalPrice = 0;

        if (!book.isPresent() || !user.isPresent()) {
            return new Double(totalPrice).intValue();
        } else {
            totalPrice = book.get().getPrice();
        }

        if (user.get().hasPoint()) {
            totalPrice = book.get().getPrice() - user.get().getPoint();
        }

        if (user.get().getCoupons().isPresent()) {
            for (Coupon c : user.get().getCoupons().get()) {
                if (c.isAppliable(book.get())) {
                    totalPrice = totalPrice - (totalPrice * c.getDiscountRate());
                }
            }
        }

        return paymentService.pay(new Double(totalPrice).intValue());
    }

    private boolean testMethod(int i) {
        if(i == 0) {
            return true;
        }

        return false;
    }
}
