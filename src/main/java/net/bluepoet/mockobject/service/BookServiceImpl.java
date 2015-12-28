package net.bluepoet.mockobject.service;

import net.bluepoet.mockobject.model.Book;
import net.bluepoet.mockobject.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Created by poet.me on 15. 12. 28..
 */
public class BookServiceImpl implements BookService {
    private CategoryService categoryService;

    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public Optional<Book> getBookByNo(int no) {
        if (no == 1) {
            Optional<List<Category>> categories = categoryService.getCategoriesByBookNo(1);
            return Optional.of(new Book(1, 10000, "테스트북", categories));
        } else if(no == 2) {
            Optional<List<Category>> categories = categoryService.getCategoriesByBookNo(2);
            return Optional.of(new Book(2, 5000, "테스트북2", categories));
        }

        return Optional.empty();
    }
}