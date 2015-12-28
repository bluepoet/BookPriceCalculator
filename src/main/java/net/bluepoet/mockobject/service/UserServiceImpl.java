package net.bluepoet.mockobject.service;

import com.google.common.collect.Lists;
import net.bluepoet.mockobject.model.Coupon;
import net.bluepoet.mockobject.model.User;

import java.util.Optional;

/**
 * Created by poet.me on 15. 12. 28..
 */
public class UserServiceImpl implements UserService {
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
