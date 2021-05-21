package com.javaMessenger.Messenger.repository;

import com.javaMessenger.Messenger.domain.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Interface contains additional operations with collection "Message" of MongoDB
 * */
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    ChatMessage findBySender (String sender);
}
