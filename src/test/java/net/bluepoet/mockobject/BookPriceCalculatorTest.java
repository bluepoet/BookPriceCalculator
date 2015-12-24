package net.bluepoet.mockobject;

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
        calculator.setUserService(userService);
        calculator.setBookService(bookService);
        Coupon coupon = new Coupon(1, 2, 0.1);
        Payment payment = new MasterCardPayment();

        // When
        int result = calculator.calculate("bluepoet", 1, coupon, payment);

        // Then
        assertThat(result).isEqualTo(7695);
    }

    @Data
    private class BookPriceCalculator {
        private UserService userService;
        private BookService bookService;

        public int calculate(String userId, int bookNo, Coupon coupon, Payment payment) {
            double totalPrice = 0;
            Book book = bookService.getBookByNo(1);
            User user = userService.getUserById(userId);
            user.addCoupon(coupon);

            if (user.hasPoint()) {
                totalPrice = book.getPrice() - user.getPoint();
            }

            if (coupon.isAppliable(book)) {
                totalPrice = totalPrice - (totalPrice * coupon.getDiscountRate());
            }

            return payment.pay(new Double(totalPrice).intValue());
        }
    }

    private interface UserService {
        User getUserById(String id);
    }

    private class UserServiceImpl implements UserService {
        @Override
        public User getUserById(String userId) {
            return new User("bluepoet", "김용훈", 1000);
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
