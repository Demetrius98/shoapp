package com.javaMessenger.Messenger.repository;



import com.javaMessenger.Messenger.domain.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Interface contains additional operations with collection "Role" of MongoDB
 * */
public interface RoleRepository extends MongoRepository<Role, String> {
    
    Role findByRole(String role);
}

