package net.bluepoet.mockobject.service;

import net.bluepoet.mockobject.model.User;

import java.util.Optional;

/**
 * Created by poet.me on 15. 12. 28..
 */
public interface UserService {
    Optional<User> getUserByNo(int no);
}
