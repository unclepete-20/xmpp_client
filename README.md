# XMPP Client Project using Smack Library 🚀

## Introduction 📘

This project is a comprehensive implementation of a chat client built upon the Extensible Messaging and Presence Protocol (XMPP) standard. Developed in Java, it employs the Smack library, with Maven orchestrating the project's dependencies.

## Overview 🌐

XMPP is a robust communication protocol designed for real-time communication, which uses XML to send and receive messages. While XMPP has several applications, from online gaming to IoT, this project specifically targets instant messaging.

## Setup 🛠

**Prerequisite:** Make sure Maven is installed on your system.

- **Clone/Download:** Obtain the repository either by cloning or downloading.
- **Dependency Management:** Navigate to the project directory in your terminal and run:

```shell
  mvn clean install
```

This command fetches and installs all required dependencies as detailed in the `pom.xml` file.

## Core Features 🌟

### User Authentication 🔐

- **Registration:** New users can sign up for the service, as managed by the `Register.java` class.
- **Login:** Returning users can log into their accounts. This is handled in the `Login.java` class.

### Instant Messaging 💬

- **Peer Messaging:** Users can send and receive messages in a seamless chat interface, with real-time updates.
- **File Transfer:** The application supports sending and receiving files. Files are encoded in Base64 for transfer and decoded upon receipt. This feature is empowered by the `FileBase64.java` class.

### Group Chat 👥

Built with the `UserMenu.java` class, this feature boasts:

- **Group Creation:** Users can create public, persistent groups. This means once a group is created, it remains active for any user to join later on.
- **Group Joining:** Join existing chat groups.
- **Active Group Listing:** View all groups a user is part of.
- **Real-time Group Chatting:** Engage in dynamic group conversations.

### Presence Notifications 🟢🔴

The application provides real-time online/offline status updates about contacts, ensuring users are always aware of their contacts' presence status.

### Friend and Group Management 🤝

With functionalities encapsulated in `UserMenu.java`:

- **Friend Requests:** Send, view, and manage friend requests.
- **Group Invitations:** Receive, accept, or decline group invitations.

### XMPP Extensions (XEPs) 🧩

The project incorporates multiple XEPs (XMPP Extension Protocols) to extend the core functionalities of XMPP:

- **XEP-0030:** Service Discovery
- **XEP-0045:** Multi-User Chat (for group chats)
- **XEP-0077:** In-Band Registration (for user registration)
- **XEP-0199:** XMPP Ping (to check for network activity)

## Behind the Code 🖥

The project is structured in a modular fashion, ensuring clean and maintainable code. Key functionalities are isolated into their dedicated classes, promoting a separation of concerns. The use of the Smack library streamlines XMPP interactions, while Maven ensures a harmonized project ecosystem.

## Conclusion 🎉

This XMPP client is a testament to the flexibility and versatility of the XMPP protocol. Combining Java's robustness with the Smack library's capabilities, it's a scalable and feature-rich application ready for real-world scenarios.
