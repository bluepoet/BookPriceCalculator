package net.bluepoet.mockobject.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by poet.me on 15. 12. 28..
 */
@Data
@AllArgsConstructor
public class Coupon {
    private int no;
    private int categoryNo;
    private double discountRate;

    public boolean isAppliable(Book book) {
        return book.getCategories().map(cl -> cl.stream().anyMatch(c -> c.getNo() == this.categoryNo)).orElse(false);
    }
}
