package com.javaMessenger.Messenger.controller;

import com.javaMessenger.Messenger.domain.ChatMessage;
import com.javaMessenger.Messenger.service.*;
import com.javaMessenger.Messenger.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Controller for transfer message tasks
 *
 * @author dmitry
 * */
@Controller
public class WebSocketController {
    @Autowired
    private ChatMessageService messageService;

    /**
     * Get message from client, save to mongoDB and send to WebSocket topic
     *  */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/publicChatRoom")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        //Adding to mongodb
        messageService.saveMessage(chatMessage);
        return chatMessage;
    }

    /**
     * Add user in chat room (enter/leave to chat)
     *  */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/publicChatRoom")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    /**
     * Get messages from mondoDB before opening chat
     *  */
    @MessageMapping("/chat.getMessages")
    @SendTo("/topic/publicChatRoom")
    public List<ChatMessage> getMessages(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {


        List<ChatMessage> listOfMessages = messageService.getListOfMessages(chatMessage.getSender(), chatMessage.getRecipient());

        return listOfMessages;

    }

}
