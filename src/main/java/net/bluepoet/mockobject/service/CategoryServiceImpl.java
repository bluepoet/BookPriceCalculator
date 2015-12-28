package net.bluepoet.mockobject.service;

import net.bluepoet.mockobject.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by poet.me on 15. 12. 28..
 */
public class CategoryServiceImpl implements CategoryService {
    @Override
    public Optional<List<Category>> getCategoriesByBookNo(int bookNo) {
        if (bookNo == 1) {
            List categories = new ArrayList<Category>() {
                {
                    add(new Category(1, "육아"));
                    add(new Category(2, "IT"));
                    add(new Category(3, "문화"));
                }
            };
            return Optional.of(categories);
        } else if(bookNo == 2) {
            List categories = new ArrayList<Category>() {
                {
                    add(new Category(4, "가정"));
                    add(new Category(5, "낚시"));
                }
            };
            return Optional.of(categories);
        }

        return Optional.empty();
    }
}
