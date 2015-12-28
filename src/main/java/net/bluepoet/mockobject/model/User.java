package net.bluepoet.mockobject.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

/**
 * Created by poet.me on 15. 12. 28..
 */
@Data
@RequiredArgsConstructor
public class User {
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