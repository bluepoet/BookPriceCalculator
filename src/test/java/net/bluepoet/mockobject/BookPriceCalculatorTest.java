package net.bluepoet.mockobject;

import net.bluepoet.mockobject.model.*;
import net.bluepoet.mockobject.service.*;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by poet.me on 15. 12. 24..
 */
public class BookPriceCalculatorTest {
    private BookPriceCalculator calculator;
    private UserService userService;
    private BookService bookService;
    private CategoryService categoryService;
    private PaymentService paymentService;

    @Before
    public void setUp() throws Exception {
        calculator = new BookPriceCalculator();
        userService = new UserServiceImpl();
        bookService = new BookServiceImpl();
        categoryService = new CategoryServiceImpl();
        bookService.setCategoryService(categoryService);
        paymentService = new PaymentServiceImpl();
        Payment payment = new NormalCardPayment();
        paymentService.setPayment(payment);

        calculator.setUserService(userService);
        calculator.setBookService(bookService);
        calculator.setPaymentService(paymentService);
    }

    @Test
    public void calculate_has_one_coupon_and_has_point_user() throws Exception {
        // Given
        // When
        int result = calculator.calculate(1, 1);

        // Then
        assertThat(result).isEqualTo(8100);
    }

    @Test
    public void calculate_has_many_coupon_and_has_point_user() throws Exception {
        // Given
        // When
        int result = calculator.calculate(5, 1);

        // Then
        assertThat(result).isEqualTo(5760);
    }

    @Test
    public void calculate_has_many_coupon_and_has_point_and_other_no_mathch_category_user() throws Exception {
        // Given
        // When
        int result = calculator.calculate(5, 2);

        // Then
        assertThat(result).isEqualTo(3000);
    }

    @Test
    public void calculate_no_coupon_and_has_point_user() throws Exception {
        // Given
        // When
        int result = calculator.calculate(2, 1);

        // Then
        assertThat(result).isEqualTo(9950);
    }

    @Test
    public void calculate_no_coupon_and_no_point_user() throws Exception {
        // Given
        // When
        int result = calculator.calculate(3, 1);

        // Then
        assertThat(result).isEqualTo(10000);
    }

    @Test
    public void calculate_no_user() throws Exception {
        // Given
        // When
        int result = calculator.calculate(4, 1);

        // Then
        assertThat(result).isEqualTo(0);
    }

    @Test
    public void calculate_has_many_coupon_and_no_point_and_no_match_category() throws Exception {
        // Given
        // When
        int result = calculator.calculate(6, 1);

        // Then
        assertThat(result).isEqualTo(10000);
    }

    @Test
    public void calculate_no_book() throws Exception {
        // Given
        // When
        int result = calculator.calculate(6, 3);

        // Then
        assertThat(result).isEqualTo(0);
    }

    @Test
    public void calculate_has_many_coupon_and_has_point_and_masterCard_payment_user() throws Exception {
        // Given
        paymentService.setPayment(new MasterCardPayment());

        // When
        int result = calculator.calculate(5, 1);

        // Then
        assertThat(result).isEqualTo(5472);
    }

    @Test
    public void calculate_has_many_coupon_and_has_point_and_visaCard_payment_user() throws Exception {
        // Given
        paymentService.setPayment(new VisaCardPayment());

        // When
        int result = calculator.calculate(5, 1);

        // Then
        assertThat(result).isEqualTo(5299);
    }

    @Test
    public void calculate_has_many_coupon_and_has_point_and_accountTransfer_payment_user() throws Exception {
        // Given
        paymentService.setPayment(new AccountTransferPayment());

        // When
        int result = calculator.calculate(5, 1);

        // Then
        assertThat(result).isEqualTo(5760);
    }
}
