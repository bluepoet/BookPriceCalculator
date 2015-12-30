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
        1 * paymentService.pay(_) >> 8100
    }

    def "2개의 쿠폰과 포인트를 가진 유저의 책 가격을 계산한다"() {
        given:
        userService.getUserByNo(5) >> createManyCouponUser()

        when:
        def result = calculator.calculate(5, 1)

        then:
        result == 5760
        1 * paymentService.pay(_) >> 5760
    }

    def "2개의 쿠폰과 포인트를 가진 유저지만, 쿠폰할인혜택이 없는 유저의 책 가격을 계산한다"() {
        given:
        userService.getUserByNo(5) >> createManyCouponUser()
        bookService.getBookByNo(2) >> createOtherBook()

        when:
        def result = calculator.calculate(5, 2)

        then:
        result == 3000
        1 * paymentService.pay(_) >> 3000
    }

    def "2개의 쿠폰이 있고 포인트가 없는 유저지만, 쿠폰할인혜택이 없는 유저의 책 가격을 계산한다"() {
        given:
        userService.getUserByNo(6) >> createNoMatchCategoryUser()

        when:
        def result = calculator.calculate(6, 1)

        then:
        result == 10000
        1 * paymentService.pay(_) >> 10000
    }

    def "쿠폰은 없고 포인트만 있는 유저의 책 가격을 계산한다"() {
        given:
        userService.getUserByNo(2) >> createNoCouponAndHasPointUser()

        when:
        def result = calculator.calculate(2, 1)

        then:
        result == 9950
        1 * paymentService.pay(_) >> 9950
    }

    def "쿠폰은 없고 포인트도 없는 유저의 책 가격을 계산한다"() {
        given:
        userService.getUserByNo(3) >> createNoCouponAndHasPointUser()

        when:
        def result = calculator.calculate(3, 1)

        then:
        result == 10000
        1 * paymentService.pay(_) >> 10000
    }

    def "회원이 아닌 유저의 요청일 경우 예외처리로 0원을 리턴한다"() {
        given:
        userService.getUserByNo(4) >> Optional.empty()

        when:
        def result = calculator.calculate(4, 1)

        then:
        result == 0
        0 * paymentService.pay(_)
    }

    def "존재하지 않는 책일 경우 예외처리로 0원을 리턴한다"() {
        given:
        bookService.getBookByNo(3) >> Optional.empty()

        when:
        def result = calculator.calculate(6, 3)

        then:
        result == 0
        0 * paymentService.pay(_)
    }

    def "2개의 쿠폰과 포인트를 가진 유저가 마스터카드로 결제시 책 가격을 계산한다"() {
        given:
        def payment = new MasterCardPayment()
        paymentService.pay(_) >> { payment.pay(it[0]) }
        userService.getUserByNo(5) >> createManyCouponUser()

        when:
        def result = calculator.calculate(5, 1)

        then:
        result == 5472
        1 * paymentService.pay(_) >> 5472
    }

    def "2개의 쿠폰과 포인트를 가진 유저가 비자카드로 결제시 책 가격을 계산한다"() {
        given:
        def payment = new VisaCardPayment()
        paymentService.pay(_) >> { payment.pay(it[0]) }
        userService.getUserByNo(5) >> createManyCouponUser()

        when:
        def result = calculator.calculate(5, 1)

        then:
        result == 5299
        1 * paymentService.pay(_) >> 5299
    }

    def "2개의 쿠폰과 포인트를 가진 유저가 계좌이체로 결제시 책 가격을 계산한다"() {
        given:
        def payment = new AccountTransferPayment()
        paymentService.pay(_) >> { payment.pay(it[0]) }
        userService.getUserByNo(5) >> createManyCouponUser()

        when:
        def result = calculator.calculate(5, 1)

        then:
        result == 5760
        1 * paymentService.pay(_) >> 5760
    }

    private Optional<User> createBaseUser() {
        def user = new User("bluepoet", "김용훈", 1000);
        def coupon = new Coupon(1, 2, 0.1);
        user.setCoupons(Optional.of(Lists.newArrayList(coupon)));
        return Optional.of(user);
    }

    private Optional<Book> createBaseBook() {
        def categories = [new Category(1, "육아"), new Category(2, "IT"), new Category(3, "문화")]
        return Optional.of(new Book(1, 10000, "테스트북", Optional.of(categories)));
    }

    private Optional<Book> createOtherBook() {
        def categories = [new Category(4, "가정"), new Category(5, "낚시")]
        return Optional.of(new Book(2, 5000, "테스트북2", Optional.of(categories)));
    }

    private Optional<User> createManyCouponUser() {
        User user = new User("mock", "목테스트", 2000);
        Coupon coupon = new Coupon(1, 2, 0.1);
        Coupon otherCoupon = new Coupon(2, 3, 0.2);
        user.setCoupons(Optional.of(Lists.newArrayList(coupon, otherCoupon)));
        return Optional.of(user);
    }

    private Optional<User> createNoCouponAndHasPointUser() {
        return Optional.of(new User("kakao", "카카오", 50));
    }

    private Optional<User> createNoCouponAndNoPointUser() {
        return Optional.of(new User("test", "테스트", 0));
    }

    private Optional<User> createNoMatchCategoryUser() {
        User user = new User("mock1", "목테스트1", 0);
        Coupon coupon = new Coupon(3, 4, 0.1);
        Coupon otherCoupon = new Coupon(4, 5, 0.2);
        user.setCoupons(Optional.of(Lists.newArrayList(coupon, otherCoupon)));
        return Optional.of(user);
    }
}
