package com.javaMessenger.Messenger.service;

import com.javaMessenger.Messenger.domain.ChatMessage;
import com.javaMessenger.Messenger.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Class contains operations with collection "messages" of mongoDB
 *
 * @author dmitry
 * */
@Service
public class ChatMessageService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private MongoOperations mongoOperations;

    /**
     * Method for saving message to MongoDB
     *
     * @param message
     * */
    public void saveMessage (ChatMessage message) {
      message.setSender(message.getSender());
      message.setType(message.getType());
      message.setContent(message.getContent());
      message.setRecipient(message.getRecipient());
      message.setTime(message.getTime());

      chatMessageRepository.save(message);
    }

    /**
     * Get List of Messages from MongoDB
     *
     * @param sender - full name of sender
     * @param recipient - full name of recipient
     *  */
    public List <ChatMessage> getListOfMessages (String sender, String recipient) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sender").is(sender));
        query.addCriteria(Criteria.where("recipient").is(recipient));
        return mongoOperations.find(query, ChatMessage.class);
    }
}
