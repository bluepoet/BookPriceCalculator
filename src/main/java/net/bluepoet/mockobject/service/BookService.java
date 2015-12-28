package net.bluepoet.mockobject.service;

import net.bluepoet.mockobject.model.Book;

import java.util.Optional;

/**
 * Created by poet.me on 15. 12. 28..
 */
public interface BookService {
    void setCategoryService(CategoryService categoryService);
    Optional<Book> getBookByNo(int no);
}
