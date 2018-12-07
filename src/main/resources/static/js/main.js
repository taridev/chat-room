'use strict';

var nameInput = $('#name');
var roomInput = $('#room-id');
var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var roomIdDisplay = document.querySelector('#room-id-display');
/* Ajout */
var listUsers = document.querySelector('#listUsers');
var userInfos = document.querySelector('#userInfos');
var avatarBig = document.createElement('img');

var stompClient = null;
var currentSubscription;
var username = null;
var roomId = null;
var topic = null;
var user = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = nameInput.val().trim();
    Cookies.set('name', username);
    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');
        listUsers.classList.remove('hidden');
        userInfos.classList.remove('hidden');

        $.ajax({
            type: "GET",
            contentType : "application/json",
            dataType: "json",
            url: `/user/${username}`,
            success: function (result) {
                user = result;
            },
            error: function(e) {
                console.log('oups');
            },
            async:false
        });



        avatarBig.classList.add('avatarBig');
        avatarBig.src = user.image;
        userInfos.appendChild(avatarBig);
        var userBigName = document.createElement('h2');
        userBigName.classList.add('userBigName');
        var textUserName = document.createTextNode(username);
        userBigName.appendChild(textUserName);
        userInfos.appendChild(userBigName);

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

// Leave the current room and enter a new one.
function enterRoom(newRoomId) {

    roomId = newRoomId;
    Cookies.set('roomId', roomId);
    roomIdDisplay.textContent = roomId;
    topic = `/app/chat/${newRoomId}`;

    if (currentSubscription) {
        currentSubscription.unsubscribe();
    }
    currentSubscription = stompClient.subscribe(`/channel/${roomId}`, onMessageReceived);

    stompClient.send(`${topic}/addUser`,
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    );
}

function onConnected() {
    enterRoom(roomInput.val());
    connectingElement.classList.add('hidden');
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent.startsWith('/join ')) {
        var newRoomId = messageContent.substring('/join '.length);
        enterRoom(newRoomId);
        while (messageArea.firstChild) {
            messageArea.removeChild(messageArea.firstChild);
        }
    } else if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT',
            createdAt: new Date()
        };
        stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(chatMessage));
    }
    messageInput.value = '';
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if (message.type == 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender.username + ' a rejoint la discussion !';
    } else if (message.type == 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender.username + ' a quitté la discussion !';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('img');
        avatarElement.classList.add('avatarImg');
        avatarElement.src = message.sender.image;

        messageElement.appendChild(avatarElement);

        var date = new Date(message.createdAt).toLocaleString('fr-FR', {timezone: 'UTC'});

        var usernameElement = document.createElement('span');
        var dateElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender.username);
        var dateText = document.createTextNode(' - ' + date);
        usernameElement.appendChild(usernameText);
        dateElement.appendChild(dateText);
        messageElement.appendChild(usernameElement);
        usernameElement.appendChild(dateElement);

    }
    var messageText = null;
    var textElement = document.createElement('p');
    if (message.type == 'LEAVE' || message.type == 'JOIN') {
        var avatar = document.createElement('img');
        avatar.src = message.sender.image;
        avatar.style.width = '20px';
        textElement.style.verticalAlign = 'middle';
        messageText = document.createTextNode('  ' + message.content);
        textElement.appendChild(avatar);
    } else {
        messageText = document.createTextNode(message.content);
    }
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

$(document).ready(function() {
    var savedName = Cookies.get('name');
    if (savedName) {
        nameInput.val(savedName);
    }

    var savedRoom = Cookies.get('roomId');
    if (savedRoom) {
        roomInput.val(savedRoom);
    }

    usernamePage.classList.remove('hidden');
    usernameForm.addEventListener('submit', connect, true);
    messageForm.addEventListener('submit', sendMessage, true);
});