package com.edu.ulab.app.storage;

import com.edu.ulab.app.entity.User;

import java.util.Optional;

public interface UserRepository {
    void save(User user);

    void update(User user);

    Optional<User> findUserById(Long id);

    void remove(User user);
}
