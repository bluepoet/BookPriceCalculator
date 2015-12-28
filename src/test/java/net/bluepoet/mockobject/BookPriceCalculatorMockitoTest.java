package net.bluepoet.mockobject;

import com.google.common.collect.Lists;
import net.bluepoet.mockobject.model.*;
import net.bluepoet.mockobject.service.BookService;
import net.bluepoet.mockobject.service.PaymentService;
import net.bluepoet.mockobject.service.UserService;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.anyInt;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by poet.me on 15. 12. 24..
 */
@RunWith(MockitoJUnitRunner.class)
public class BookPriceCalculatorMockitoTest {
    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private BookPriceCalculator calculator = new BookPriceCalculator();

    private Payment payment = new NormalCardPayment();

    @Before
    public void setUp() throws Exception {
        given(userService.getUserByNo(1)).willReturn(createBaseUser());
        given(bookService.getBookByNo(1)).willReturn(createBaseBook());
        given(paymentService.pay(anyInt())).willAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            int totalPrice = (int) args[0];
            return payment.pay(totalPrice);
        });
    }

    @Test
    public void calculate_has_one_coupon_and_has_point_user() throws Exception {
        // Given
        // When
        int result = calculator.calculate(1, 1);

        // Then
        assertThat(result).isEqualTo(8100);
        verifySpyActions();
    }

    @Test
    public void calculate_has_many_coupon_and_has_point_user() throws Exception {
        // Given
        given(userService.getUserByNo(5)).willReturn(createManyCouponUser());

        // When
        int result = calculator.calculate(5, 1);

        // Then
        assertThat(result).isEqualTo(5760);
        verifySpyActions();
    }

    @Test
    public void calculate_has_many_coupon_and_has_point_and_other_no_mathch_category_user() throws Exception {
        // Given
        given(userService.getUserByNo(5)).willReturn(createManyCouponUser());
        given(bookService.getBookByNo(2)).willReturn(createOtherBook());

        // When
        int result = calculator.calculate(5, 2);

        // Then
        assertThat(result).isEqualTo(3000);
        verifySpyActions();
    }

    @Test
    public void calculate_no_coupon_and_has_point_user() throws Exception {
        // Given
        given(userService.getUserByNo(2)).willReturn(createNoCouponAndHasPointUser());

        // When
        int result = calculator.calculate(2, 1);

        // Then
        assertThat(result).isEqualTo(9950);
        verifySpyActions();
    }

    @Test
    public void calculate_no_coupon_and_no_point_user() throws Exception {
        // Given
        given(userService.getUserByNo(3)).willReturn(createNoCouponAndNoPointUser());

        // When
        int result = calculator.calculate(3, 1);

        // Then
        assertThat(result).isEqualTo(10000);
        verifySpyActions();
    }

    @Test
    public void calculate_no_user() throws Exception {
        // Given
        given(userService.getUserByNo(4)).willReturn(Optional.empty());

        // When
        int result = calculator.calculate(4, 1);

        // Then
        assertThat(result).isEqualTo(0);

        verify(userService, times(1)).getUserByNo(anyInt());
        verify(bookService, times(1)).getBookByNo(anyInt());
        verify(paymentService, never()).pay(anyInt());
    }

    @Test
    public void calculate_has_many_coupon_and_no_point_and_no_match_category() throws Exception {
        // Given
        given(userService.getUserByNo(6)).willReturn(createNoMatchCategoryUser());

        // When
        int result = calculator.calculate(6, 1);

        // Then
        assertThat(result).isEqualTo(10000);
        verifySpyActions();
    }

    @Test
    public void calculate_no_book() throws Exception {
        // Given
        given(bookService.getBookByNo(3)).willReturn(Optional.empty());

        // When
        int result = calculator.calculate(6, 3);

        // Then
        assertThat(result).isEqualTo(0);

        verify(userService, times(1)).getUserByNo(anyInt());
        verify(bookService, times(1)).getBookByNo(anyInt());
        verify(paymentService, never()).pay(anyInt());
    }

    @Test
    public void calculate_has_many_coupon_and_has_point_and_masterCard_payment_user() throws Exception {
        // Given
        stubbingPayment(new MasterCardPayment());

        // When
        int result = calculator.calculate(5, 1);

        // Then
        assertThat(result).isEqualTo(5472);
        verifySpyActions();
    }

    @Test
    public void calculate_has_many_coupon_and_has_point_and_visaCard_payment_user() throws Exception {
        // Given
        stubbingPayment(new VisaCardPayment());

        // When
        int result = calculator.calculate(5, 1);

        // Then
        assertThat(result).isEqualTo(5299);
        verifySpyActions();
    }

    @Test
    public void calculate_has_many_coupon_and_has_point_and_accountTransfer_payment_user() throws Exception {
        // Given
        stubbingPayment(new AccountTransferPayment());

        // When
        int result = calculator.calculate(5, 1);

        // Then
        assertThat(result).isEqualTo(5760);
        verifySpyActions();
    }

    private void stubbingPayment(final Payment payment) {
        given(userService.getUserByNo(5)).willReturn(createManyCouponUser());
        given(paymentService.pay(anyInt())).willAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            int totalPrice = (int) args[0];
            return payment.pay(totalPrice);
        });
    }

    private Optional<List<Category>> createBaseCategories() {
        List categories = new ArrayList<Category>() {
            {
                add(new Category(1, "육아"));
                add(new Category(2, "IT"));
                add(new Category(3, "문화"));
            }
        };
        return Optional.of(categories);
    }

    private Optional<Book> createBaseBook() {
        List categories = new ArrayList<Category>() {
            {
                add(new Category(1, "육아"));
                add(new Category(2, "IT"));
                add(new Category(3, "문화"));
            }
        };
        return Optional.of(new Book(1, 10000, "테스트북", Optional.of(categories)));

    }

    private Optional<User> createBaseUser() {
        User user = new User("bluepoet", "김용훈", 1000);
        Coupon coupon = new Coupon(1, 2, 0.1);
        user.setCoupons(Optional.of(Lists.newArrayList(coupon)));
        return Optional.of(user);
    }

    private Optional<User> createManyCouponUser() {
        User user = new User("mock", "목테스트", 2000);
        Coupon coupon = new Coupon(1, 2, 0.1);
        Coupon otherCoupon = new Coupon(2, 3, 0.2);
        user.setCoupons(Optional.of(Lists.newArrayList(coupon, otherCoupon)));
        return Optional.of(user);
    }

    private Optional<Book> createOtherBook() {
        List categories = new ArrayList<Category>() {
            {
                add(new Category(4, "가정"));
                add(new Category(5, "낚시"));
            }
        };
        return Optional.of(new Book(2, 5000, "테스트북2", Optional.of(categories)));
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

    private void verifySpyActions() {
        verify(userService, times(1)).getUserByNo(anyInt());
        verify(bookService, times(1)).getBookByNo(anyInt());
        verify(paymentService, times(1)).pay(anyInt());
    }
}
