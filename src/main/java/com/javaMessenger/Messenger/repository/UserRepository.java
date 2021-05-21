package com.javaMessenger.Messenger.repository;

import com.javaMessenger.Messenger.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;


/**
 * Interface contains additional operations with collection "User" of MongoDB
 *
 * @author dmitry
 */
public interface UserRepository extends MongoRepository<User, String> {
    
    User findByEmail(String email);



}
