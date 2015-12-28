package net.bluepoet.mockobject;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by poet.me on 15. 12. 24..
 */
public class BookPriceCalculatorTest {

    @Test
    public void calculate() throws Exception {
        // Given
        BookPriceCalculator calculator = new BookPriceCalculator();

        UserService userService = new UserServiceImpl();
        BookService bookService = new BookServiceImpl();
        CategoryService categoryService = new CategoryServiceImpl();
        bookService.setCategoryService(categoryService);
        PaymentService paymentService = new PaymentServiceImpl();
        Payment payment = new MasterCardPayment();
        paymentService.setPayment(payment);

        calculator.setUserService(userService);
        calculator.setBookService(bookService);
        calculator.setPaymentService(paymentService);

        // When
        int result = calculator.calculate("bluepoet", 1);

        // Then
        assertThat(result).isEqualTo(7695);
    }

    @Data
    private class BookPriceCalculator {
        private UserService userService;
        private BookService bookService;
        private PaymentService paymentService;

        public int calculate(String userNo, int bookNo) {
            double totalPrice = 0;
            Book book = bookService.getBookByNo(1);
            User user = userService.getUserByNo(userNo);

            if (user.hasPoint()) {
                totalPrice = book.getPrice() - user.getPoint();
            }

            if (user.getCoupons().isPresent()) {
                for (Coupon c : user.getCoupons().get()) {
                    totalPrice = totalPrice - (totalPrice * c.getDiscountRate());
                }
            }

            return paymentService.pay(new Double(totalPrice).intValue());
        }
    }

    private interface UserService {
        User getUserByNo(String no);
    }

    private class UserServiceImpl implements UserService {
        @Override
        public User getUserByNo(String userNo) {
            User user = new User("bluepoet", "김용훈", 1000);
            Coupon coupon = new Coupon(1, 2, 0.1);
            user.setCoupons(Optional.of(Lists.newArrayList(coupon)));
            return user;
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

        Book getBookByNo(int no);
    }

    private class BookServiceImpl implements BookService {
        private CategoryService categoryService;

        public void setCategoryService(CategoryService categoryService) {
            this.categoryService = categoryService;
        }

        @Override
        public Book getBookByNo(int no) {
            List<Category> categories = categoryService.getCategoriesByBookNo(1);
            return new Book(1, 10000, "테스트북", categories);
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

        public double discountPrice(double price) {
            return price - (price * getDiscountRate());
        }
    }
}
