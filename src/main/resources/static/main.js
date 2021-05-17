'use strict';


var chatContainer = document.querySelector('#chat-container');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('#connecting');

var stompClient = null;
var username = null;
var usernameTo = null;


function connect() {
    username = document.querySelector('#username').innerText.trim();
    usernameTo = document.querySelector('#usernameTo').innerText.trim();


    if (username.localeCompare(usernameTo) != 0) {

        chatContainer.style.display = '';

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    else {
        chatContainer.style.display = 'none';
    }
}

// Connect to WebSocket Server.
connect();

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/publicChatRoom', onMessageReceived);

    var time = new Date();

    //Request to get messages from dataBase
    stompClient.send("/app/chat.getMessages",
        {},
        JSON.stringify({sender: username, recipient: usernameTo, content: messageInput.value, time:time, type: 'CHAT'})
    );


    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, recipient: usernameTo, time:time, type: 'JOIN'})
    );



    connectingElement.classList.add('hidden');

}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var time = new Date();
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT',
            time: time,
            recipient: usernameTo
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    //let message = new Array(JSON.parse(payload.body));
    //var message = payload.text.map(JSON.parse);
    var message = JSON.parse(payload.body);
    console.log(message);

    if (message.length > 0) {
        for (var i = 0; i < message.length; i++) {
            var key = message[i];
            //console.log(key.content);
            if ((username.localeCompare(key.recipient) == 0) || (username.localeCompare(key.sender) == 0)) {
                var messageElement = document.createElement('li');
                var time = new Date(key.time);

                if (key.type === 'JOIN') {
                    messageElement.classList.add('event-message');
                    key.content = key.sender + ' joined!';
                } else if (key.type === 'LEAVE') {
                    messageElement.classList.add('event-message');
                    key.content = key.sender + ' left!';
                } else {
                    messageElement.classList.add('chat-message');
                    var usernameElement = document.createElement('strong');
                    usernameElement.classList.add('nickname');
                    //var usernameText = document.createTextNode(message.sender);
                    var usernameText = document.createTextNode(key.sender);
                    usernameElement.appendChild(usernameText);
                    messageElement.appendChild(usernameElement);
                }

                var textElement = document.createElement('span');
                var messageText = document.createTextNode(key.content + ' ' + time.toLocaleString());
                textElement.appendChild(messageText);

                messageElement.appendChild(textElement);

                messageArea.appendChild(messageElement);
                messageArea.scrollTop = messageArea.scrollHeight;
            }
        }
    }
    else {
        if ((username.localeCompare(message.recipient) == 0) || (username.localeCompare(message.sender) == 0)) {
            var messageElement = document.createElement('li');
            var time = new Date(message.time);

            if (message.type === 'JOIN') {
                messageElement.classList.add('event-message');
                message.content = message.sender + ' joined!';
            } else if (message.type === 'LEAVE') {
                messageElement.classList.add('event-message');
                message.content = message.sender + ' left!';
            } else {
                messageElement.classList.add('chat-message');
                var usernameElement = document.createElement('strong');
                usernameElement.classList.add('nickname');
                var usernameText = document.createTextNode(message.sender);
                var usernameText = document.createTextNode(message.sender);
                usernameElement.appendChild(usernameText);
                messageElement.appendChild(usernameElement);
            }

            var textElement = document.createElement('span');
            var messageText = document.createTextNode(message.content + ' ' + time.toLocaleString());
            textElement.appendChild(messageText);

            messageElement.appendChild(textElement);

            messageArea.appendChild(messageElement);
            messageArea.scrollTop = messageArea.scrollHeight;
        }
    }

}


messageForm.addEventListener('submit', sendMessage, true);