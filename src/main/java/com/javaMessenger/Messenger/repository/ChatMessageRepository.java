package com.javaMessenger.Messenger.repository;

import com.javaMessenger.Messenger.domain.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    ChatMessage findBySender (String sender);
}
