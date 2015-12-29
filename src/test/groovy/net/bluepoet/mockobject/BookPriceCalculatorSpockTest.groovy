package net.bluepoet.mockobject

import com.google.common.collect.Lists
import net.bluepoet.mockobject.model.*
import net.bluepoet.mockobject.service.BookService
import net.bluepoet.mockobject.service.PaymentService
import net.bluepoet.mockobject.service.UserService
import spock.lang.Specification

/**
 * Created by poet.me on 15. 12. 29..
 */
class BookPriceCalculatorSpockTest extends Specification {
    def userService
    def bookService
    def paymentService
    def calculator = new BookPriceCalculator()
    def payment = new NormalCardPayment();

    def setup() {
        userService = Mock(UserService)
        bookService = Mock(BookService)
        paymentService = Mock(PaymentService)

        calculator.setUserService(userService)
        calculator.setBookService(bookService)
        calculator.setPaymentService(paymentService)

        userService.getUserByNo(1) >> createBaseUser()
        bookService.getBookByNo(1) >> createBaseBook()
        paymentService.pay(_) >> { payment.pay(it[0]) }
    }

    def "하나의 쿠폰과 포인트를 가진 유저의 책 가격을 계산한다"() {
        when:
        def result = calculator.calculate(1, 1)
        then:
        result == 8100
    }

    def "2개의 쿠폰과 포인트를 가지고 있는 유저의 책 가격을 계산한다"() {
        given:
        userService.getUserByNo(5) >> createManyCouponUser()
        when:
        def result = calculator.calculate(5, 1)
        then:
        result == 5760
    }

    def Optional<User> createBaseUser() {
        def user = new User("bluepoet", "김용훈", 1000);
        def coupon = new Coupon(1, 2, 0.1);
        user.setCoupons(Optional.of(Lists.newArrayList(coupon)));
        return Optional.of(user);
    }

    def Optional<Book> createBaseBook() {
        def categories = [new Category(1, "육아"), new Category(2, "IT"), new Category(3, "문화")]
        return Optional.of(new Book(1, 10000, "테스트북", Optional.of(categories)));
    }

    def Optional<User> createManyCouponUser() {
        User user = new User("mock", "목테스트", 2000);
        Coupon coupon = new Coupon(1, 2, 0.1);
        Coupon otherCoupon = new Coupon(2, 3, 0.2);
        user.setCoupons(Optional.of(Lists.newArrayList(coupon, otherCoupon)));
        return Optional.of(user);
    }
}
