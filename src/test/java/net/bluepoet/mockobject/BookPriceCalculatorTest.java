package net.bluepoet.mockobject;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        int result = calculator.calculate(6, 2);

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

    @Data
    private class BookPriceCalculator {
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
    }

    private interface UserService {
        Optional<User> getUserByNo(int no);
    }

    private class UserServiceImpl implements UserService {
        @Override
        public Optional<User> getUserByNo(int userNo) {
            if (userNo == 1) {
                User user = new User("bluepoet", "김용훈", 1000);
                Coupon coupon = new Coupon(1, 2, 0.1);
                user.setCoupons(Optional.of(Lists.newArrayList(coupon)));
                return Optional.of(user);
            } else if (userNo == 2) {
                return Optional.of(new User("kakao", "카카오", 50));
            } else if (userNo == 3) {
                return Optional.of(new User("test", "테스트", 0));
            } else if (userNo == 5) {
                User user = new User("mock", "목테스트", 2000);
                Coupon coupon = new Coupon(1, 2, 0.1);
                Coupon otherCoupon = new Coupon(2, 3, 0.2);
                user.setCoupons(Optional.of(Lists.newArrayList(coupon, otherCoupon)));
                return Optional.of(user);
            } else if (userNo == 6) {
                User user = new User("mock1", "목테스트1", 0);
                Coupon coupon = new Coupon(3, 4, 0.1);
                Coupon otherCoupon = new Coupon(4, 5, 0.2);
                user.setCoupons(Optional.of(Lists.newArrayList(coupon, otherCoupon)));
                return Optional.of(user);
            }

            return Optional.empty();
        }
    }

    @Data
    @RequiredArgsConstructor
    class User {
        @NonNull
        private String userId;
        @NonNull
        private String userName;
        private Optional<List<Coupon>> coupons = Optional.empty();
        @NonNull
        private int point;

        public boolean hasPoint() {
            if (this.point > 0) {
                return true;
            }

            return false;
        }

        public void addCoupon(Coupon coupon) {
            this.coupons.ifPresent(c -> c.add(coupon));
        }
    }

    private interface BookService {
        void setCategoryService(CategoryService categoryService);

        Optional<Book> getBookByNo(int no);
    }

    private class BookServiceImpl implements BookService {
        private CategoryService categoryService;

        public void setCategoryService(CategoryService categoryService) {
            this.categoryService = categoryService;
        }

        @Override
        public Optional<Book> getBookByNo(int no) {
            if (no == 1) {
                List<Category> categories = categoryService.getCategoriesByBookNo(1);
                return Optional.of(new Book(1, 10000, "테스트북", categories));
            }

            return Optional.empty();
        }
    }

    @Data
    @AllArgsConstructor
    private class Book {
        private int no;
        private int price;
        private String name;
        private List<Category> categories;
    }

    private interface CategoryService {
        List<Category> getCategoriesByBookNo(int no);
    }

    public class CategoryServiceImpl implements CategoryService {

        @Override
        public List<Category> getCategoriesByBookNo(int no) {
            return new ArrayList<Category>() {
                {
                    add(new Category(1, "육아"));
                    add(new Category(2, "IT"));
                    add(new Category(3, "문화"));
                }
            };
        }
    }

    @Data
    @AllArgsConstructor
    private class Category {
        private int no;
        private String name;
    }

    private interface PaymentService {
        void setPayment(Payment payment);

        int pay(int price);
    }

    private class PaymentServiceImpl implements PaymentService {
        private Payment payment;

        public void setPayment(Payment payment) {
            this.payment = payment;
        }

        @Override
        public int pay(int price) {
            return payment.pay(price);
        }
    }

    @Data
    private class Payment {
        private Optional<Double> discountRate = Optional.empty();

        public int pay(int price) {
            return discountRate.map(r -> new Double(price - (price * r)).intValue()).orElse(price);
        }
    }

    private class MasterCardPayment extends Payment {
        public MasterCardPayment() {
            setDiscountRate(Optional.of(0.05));
        }
    }

    private class VisaCardPayment extends Payment {
        public VisaCardPayment() {
            setDiscountRate(Optional.of(0.08));
        }
    }

    private class NormalCardPayment extends Payment {
    }

    private class AccountTransferPayment extends Payment {
    }

    @Data
    @AllArgsConstructor
    private class Coupon {
        private int no;
        private int categoryNo;
        private double discountRate;

        public boolean isAppliable(Book book) {
            return book.getCategories().stream().anyMatch(c -> c.getNo() == this.categoryNo);
        }
    }
}
