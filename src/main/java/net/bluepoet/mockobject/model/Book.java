package net.bluepoet.mockobject.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Optional;

/**
 * Created by poet.me on 15. 12. 28..
 */
@Data
@AllArgsConstructor
public class Book {
    private int no;
    private int price;
    private String name;
    private Optional<List<Category>> categories;
}
