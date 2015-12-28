package net.bluepoet.mockobject.service;

import net.bluepoet.mockobject.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Created by poet.me on 15. 12. 28..
 */
public interface CategoryService {
    Optional<List<Category>> getCategoriesByBookNo(int bookNo);
}
