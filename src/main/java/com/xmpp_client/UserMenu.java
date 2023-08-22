package com.xmpp_client;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.form.FillableForm;
import org.jivesoftware.smackx.xdata.form.Form;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Set;

import org.jivesoftware.smack.SmackException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserMenu {

    private AbstractXMPPConnection connection;
    private final List<EntityBareJid> pendingFriendRequests = new ArrayList<>();
    private final List<EntityBareJid> pendingGroupInvites = new ArrayList<>();
    private final List<String> receivedFilesBase64 = new ArrayList<>();

    public UserMenu(AbstractXMPPConnection connection) {
        this.connection = connection;
        listenForSubscriptionRequests();
        listenForGroupInvites();
        listenForFriendStatusChanges();
    }

    public void showMenu(Scanner scanner) throws XMPPException, SmackException, InterruptedException, IOException {

        boolean exit = false;

        while (!exit) {
            System.out.println("=== User Menu ===");
            System.out.println("1. Show My Profile");
            System.out.println("2. Update Presence Status");
            System.out.println("3. List of Contacts");
            System.out.println("4. Add new Contact");
            System.out.println("5. Manage Messages");
            System.out.println("6. Manage Contacts");
            System.out.println("7. Manage Groups");
            System.out.println("8. Delete Account");
            System.out.println("9. Manage Friend Requests");
            System.out.println("10. Manage Group Invitations");
            System.out.println("11. Manage Group Invitations");
            System.out.println("12. Log Out");
            System.out.print("\nEnter your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    showProfile();
                    break;
                case 2:
                    updatePresence(scanner);
                    break;
                case 3:
                    listContacts();
                    break;
                case 4:
                    addNewContact(scanner);
                    break;
                case 5:
                    manageMessages(scanner);
                    break;
                case 6:
                    manageContactsMenu(scanner);
                    break;
                case 7:
                    manageGroupChats(scanner);
                    break;
                case 8:
                    deleteUser();
                    exit = true;
                    break;
                case 9:
                    manageFriendRequests(scanner);
                    break;
                case 10:
                    manageGroupInvitations(scanner);
                    break;
                case 11:
                    manageReceivedFiles(scanner);
                    break;
                case 12:
                    logOut();
                    exit = true;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.\n");
            }
        }
    }

    private void updatePresence(Scanner scanner) throws SmackException.NotConnectedException, InterruptedException {
        displayHeader("Update Presence Status");
        System.out.println("1. Available");
        System.out.println("2. Away");
        System.out.println("3. Do Not Disturb");
        System.out.print("Enter your status: ");
    
        int statusChoice = scanner.nextInt();
        Presence updatedPresence;
    
        switch (statusChoice) {
            case 1:
                updatedPresence = new Presence(Presence.Type.available);
                break;
            case 2:
                updatedPresence = new Presence(Presence.Type.available, "Away getting coffee", 1, Presence.Mode.away);
                break;
            case 3:
                updatedPresence = new Presence(Presence.Type.available, "Do Not Disturb", 1, Presence.Mode.dnd);
                break;
            default:
                System.out.println("Invalid status. Setting to Available.");
                updatedPresence = new Presence(Presence.Type.available);
        }
    
        connection.sendStanza(updatedPresence);
        System.out.println("\nPresence status updated successfully!\n");
    }
    
    
    

    private void logOut() {
        connection.disconnect();
        System.out.println("\nLogged out successfully!\n");
    }

    private void showProfile() {
        displayHeader("My Profile");
        String username = connection.getUser().toString(); // Get the entire JID as a string
        Roster roster = Roster.getInstanceFor(connection);
        Presence presence = roster.getPresence(connection.getUser().asEntityBareJidIfPossible()); // Get presence for the user's bare JID
    
        System.out.println("JID: " + username); // Display the entire JID
        System.out.println("Status: " + presence.getStatus());
        System.out.println("Mode: " + presence.getMode());
    }

    private void deleteUser() throws InterruptedException {
        try {
            AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.deleteAccount();
            System.out.println("\nAccount deleted successfully!\n");
        } catch (XMPPException | SmackException e) {
            System.out.println("\nFailed to delete account: " + e.getMessage());
        }
    }

    private void addNewContact(Scanner scanner) {
        displayHeader("Add New Contact");
        System.out.print("Enter the JID of the contact you want to add: ");
        String jidString = scanner.next();
        System.out.print("Enter a name for this contact: ");
        String name = scanner.next();
    
        // Convert the JID string to a BareJid object
        BareJid jid;
        try {
            jid = JidCreate.bareFrom(jidString);
        } catch (XmppStringprepException e) {
            System.out.println("\nInvalid JID format.\n");
            return;
        }
    
        // Get the roster associated with the connection
        Roster roster = Roster.getInstanceFor(connection);
    
        // Create the new entry and request subscription
        try {
            roster.createItemAndRequestSubscription(jid, name, null);
            System.out.println("Contact added successfully!");
        } catch (SmackException.NotLoggedInException | SmackException.NoResponseException | 
                 XMPPException.XMPPErrorException | SmackException.NotConnectedException | 
                 InterruptedException e) {
            System.out.println("Failed to add contact: " + e.getMessage());
        }
    }

    private void listContacts() {
        displayHeader("Contacts List");
        Roster roster = Roster.getInstanceFor(connection);
        System.out.println(String.format("%-25s %-20s %-15s", "Username", "Status", "Mode"));
        System.out.println(String.format("%-25s %-20s %-15s", "--------", "------", "----"));
        for (RosterEntry entry : roster.getEntries()) {
            BareJid jid = entry.getJid();
            String user = jid.getLocalpartOrNull().toString();
            Presence presence = roster.getPresence(jid);
            String status = presence.getStatus() != null ? presence.getStatus() : "N/A";
            String mode = presence.getMode() != null ? presence.getMode().toString() : "available";
            System.out.println(String.format("%-25s %-20s %-15s\n", user, status, mode));
        }
    }
    
    private void manageContactsMenu(Scanner scanner) {
        displayHeader("Manage Contacts");
        Roster roster = Roster.getInstanceFor(connection);
        boolean goBack = false;
        
        while (!goBack) {
            int i = 1;
            for (RosterEntry entry : roster.getEntries()) {
                BareJid jid = entry.getJid();
                String user = (jid.getLocalpartOrNull() != null) ? jid.getLocalpartOrNull().toString() : "Unknown User";
                System.out.println(i + ". " + user);
                i++;
            }
            System.out.println(i + ". Go Back");
            System.out.print("\nSelect a contact to view details or go back: ");
    
            int choice = scanner.nextInt();
            if (choice == i) {
                goBack = true;
            } else if (choice > 0 && choice < i) {
                showContactDetails(roster.getEntries().toArray(new RosterEntry[0])[choice - 1]);
            } else {
                System.out.println("\nInvalid choice. Please try again.\n");
            }
        }
    }
    
    private void showContactDetails(RosterEntry entry) {
        Roster roster = Roster.getInstanceFor(connection);
        BareJid jid = entry.getJid();
        String user = jid.getLocalpartOrNull().toString();
        String name = entry.getName();
        Presence presence = roster.getPresence(jid);
        String status = presence.getStatus() != null ? presence.getStatus() : "N/A";
        String mode = presence.getMode() != null ? presence.getMode().toString() : "available";
        
        // Fetching and displaying vCard information
        VCardManager vCardManager = VCardManager.getInstanceFor(connection);
        try {
            VCard vCard = vCardManager.loadVCard((EntityBareJid) jid);
            String fullName = vCard.getField("FN");
            String email = vCard.getEmailHome();
            String organization = vCard.getOrganization();
            
            // Extract address and geolocation information
            String address = vCard.getAddressFieldHome("STREET");
            String city = vCard.getAddressFieldHome("LOCALITY");
            String state = vCard.getAddressFieldHome("REGION");
            String country = vCard.getAddressFieldHome("CTRY");
            String postalCode = vCard.getAddressFieldHome("PCODE");
    
            System.out.println("\n=== Contact Details ===");
            System.out.println("Username: " + user);
            System.out.println("Name: " + name);
            System.out.println("Full Name (from vCard): " + fullName);
            System.out.println("Email (from vCard): " + email);
            System.out.println("Organization (from vCard): " + organization);
            System.out.println("Address (from vCard): " + address);
            System.out.println("City (from vCard): " + city);
            System.out.println("State (from vCard): " + state);
            System.out.println("Country (from vCard): " + country);
            System.out.println("Postal Code (from vCard): " + postalCode);
            System.out.println("Status: " + status);
            System.out.println("Mode: " + mode);
            System.out.println("========================\n");
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | 
                 SmackException.NotConnectedException | InterruptedException e) {
            System.out.println("Failed to fetch vCard for " + user + ": " + e.getMessage());
        }
    }

    private void displayHeader(String title) {
        System.out.println("\n========================================");
        System.out.println("          " + title);
        System.out.println("========================================");
    }
    
    private BareJid selectContactForChat(Scanner scanner) {
        displayHeader("Select Contact to Chat With");
    
        Roster roster = Roster.getInstanceFor(connection);
        List<RosterEntry> entries = new ArrayList<>(roster.getEntries());
    
        int i = 1;
        for (RosterEntry entry : entries) {
            BareJid jid = entry.getJid();
            String user = (jid.getLocalpartOrNull() != null) ? jid.getLocalpartOrNull().toString() : "Unknown User";
            System.out.println(i + ". " + user);
            i++;
        }
        
        System.out.print("\nSelect a contact by number: ");
        int choice = scanner.nextInt();
    
        if (choice > 0 && choice <= entries.size()) {
            return entries.get(choice - 1).getJid();
        } else {
            System.out.println("\nInvalid choice. Please try again.\n");
            return null;
        }
    }
    
    private void manageMessages(Scanner scanner) throws IOException {
        BareJid jid = selectContactForChat(scanner);
        if (jid == null) {
            return;
        }
    
        EntityBareJid entityBareJid;
        try {
            entityBareJid = JidCreate.entityBareFrom(jid);
        } catch (XmppStringprepException e) {
            System.out.println("Invalid JID format: " + e.getMessage());
            return;
        }
    
        ChatManager chatManager = org.jivesoftware.smack.chat2.ChatManager.getInstanceFor(connection);
        Chat chat = chatManager.chatWith(entityBareJid);
    
        System.out.println("\n===========================================");
        System.out.println("   Chatting with: " + jid.asEntityBareJidIfPossible().getLocalpart());
        System.out.println("===========================================\n");

    
        Thread receiveMessageThread = new Thread(() -> {
            chatManager.addIncomingListener((from, message, chat1) -> {
                if (message.getBody().startsWith("FILE:")) {
                    receivedFilesBase64.add(message.getBody().substring(5)); // Omit the "FILE:" part
                    System.out.println("\n[New File] From: " + from.asEntityBareJidIfPossible().getLocalpart() + "\n");
                } else {
                    System.out.println("\n[ " + from.asEntityBareJidIfPossible().getLocalpart() + " ]: " + message.getBody() + "\n");
                }
                System.out.print("You: ");
            });
    
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
    
        Thread sendMessageThread = new Thread(() -> {
            System.out.print("Type 'exit' to end the chat or 'sendfile' to send a file.\nYou: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            boolean endChat = false;
            while (!endChat) {
                try {
                    String userMessage = reader.readLine();
                    if ("exit".equalsIgnoreCase(userMessage)) {
                        endChat = true;
                        receiveMessageThread.interrupt();
                        System.out.println("\nChat ended. Type 'exit' to return to the menu.");
                        continue;
                    }
                    if ("sendfile".equalsIgnoreCase(userMessage)) {
                        System.out.print("Enter the file path to send: ");
                        String filePath = reader.readLine();
                        sendFileUsingBase64(chat, filePath);
                        System.out.print("You: ");
                        continue;
                    }
                    chat.send(userMessage);
                    System.out.print("You: ");
                } catch (Exception e) {
                    System.out.println("\nFailed to send message: " + e.getMessage());
                }
            }
        });
    
        receiveMessageThread.start();
        sendMessageThread.start();
    
        try {
            sendMessageThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void manageGroupChats(Scanner scanner) throws XMPPException, SmackException, InterruptedException, IOException {
        boolean goBack = false;
    
        while (!goBack) {
            System.out.println("\n=== Group Chats Menu ===");
            System.out.println("1. Create a Group");
            System.out.println("2. Join a Group");
            System.out.println("3. List My Groups");
            System.out.println("4. Chat in a Group");
            System.out.println("5. Go Back");
            System.out.print("\nEnter your choice: ");
    
            int choice = scanner.nextInt();
    
            switch (choice) {
                case 1:
                    createGroup(scanner);
                    break;
                case 2:
                    joinGroup(scanner);
                    break;
                case 3:
                    listMyGroups();
                    break;
                case 4:
                    chatInGroup(scanner);
                    break;
                case 5:
                    goBack = true;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.\n");
            }
        }
    }
    
    private void createGroup(Scanner scanner) throws XmppStringprepException, InterruptedException {
        displayHeader("Create Group");
    
        System.out.print("\nEnter the name of the group you want to create: ");
        String roomName = scanner.next();
    
        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat muc = multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom(roomName + "@conference.alumchat.xyz"));
    
        try {
            muc.createOrJoin(Resourcepart.from(connection.getUser().getLocalpartOrNull().toString()));
            Form configForm = muc.getConfigurationForm();
            FillableForm answerForm = configForm.getFillableForm();
    
            // Assuming you want the room to be public
            answerForm.setAnswer("muc#roomconfig_publicroom", true);
    
            muc.sendConfigurationForm(answerForm);
    
            System.out.println("Group created and joined successfully!");
        } catch (XMPPException.XMPPErrorException | SmackException e) {
            System.out.println("Error creating/joining the group: " + e.getMessage());
        }
    }

    private void joinGroup(Scanner scanner) throws XmppStringprepException, InterruptedException {
        displayHeader("Join Group");

        System.out.print("\nEnter the name of the group you want to join: ");
        String roomName = scanner.next();

        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        MultiUserChat muc = multiUserChatManager.getMultiUserChat(JidCreate.entityBareFrom(roomName + "@conference.your_server.com"));

        try {
            muc.join(Resourcepart.from(connection.getUser().getLocalpartOrNull().toString()));
            System.out.println("Group joined successfully!");
        } catch (XMPPException.XMPPErrorException | SmackException e) {
            System.out.println("Error joining the group: " + e.getMessage());
        }
    }

    private void listMyGroups() {
        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        try {
            Set<EntityBareJid> joinedRoomsSet = multiUserChatManager.getJoinedRooms();
            List<EntityBareJid> joinedRooms = new ArrayList<>(joinedRoomsSet);
            System.out.println("\n=== My Groups ===");
            for (EntityBareJid room : joinedRooms) {
                System.out.println(room.getLocalpart().toString());
            }
            System.out.println("==================");
        } catch (Exception e) {
            System.out.println("\nFailed to list groups: " + e.getMessage());
        }
    }

    private void chatInGroup(Scanner scanner) throws IOException {
        System.out.print("\nEnter the name of the group to chat in: ");
        String groupName = scanner.next();

        MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        EntityBareJid groupJid;
        try {
            groupJid = JidCreate.entityBareFrom(groupName + "@conference.yourserver.com"); // Asume que "conference.yourserver.com" es tu servicio MUC
            MultiUserChat muc = multiUserChatManager.getMultiUserChat(groupJid);

            // Para recibir mensajes
            Thread receiveMessageThread = new Thread(() -> {
                muc.addMessageListener(new MessageListener() {
                    @Override
                    public void processMessage(Message message) {
                        System.out.println("\n[ " + message.getFrom().getResourceOrEmpty().toString() + " ]: " + message.getBody() + "\n");
                        System.out.print("You: ");
                    }
                });

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });

            // Para enviar mensajes
            Thread sendMessageThread = new Thread(() -> {
                System.out.print("Type 'exit' to end the chat.\nYou: ");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                boolean endChat = false;
                while (!endChat) {
                    try {
                        String userMessage = reader.readLine();
                        if ("exit".equalsIgnoreCase(userMessage)) {
                            endChat = true;
                            receiveMessageThread.interrupt();
                            System.out.println("\nChat ended. Type 'exit' to return to the menu.");
                            continue;
                        }
                        muc.sendMessage(userMessage);
                        System.out.print("You: ");
                    } catch (Exception e) {
                        System.out.println("\nFailed to send message: " + e.getMessage());
                    }
                }
            });

            receiveMessageThread.start();
            sendMessageThread.start();

            try {
                sendMessageThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("\nFailed to chat in group: " + e.getMessage());
        }
    }
    

    private void manageFriendRequests(Scanner scanner) {
        displayHeader("Friend Requests");
        synchronized (pendingFriendRequests) {
            if (pendingFriendRequests.isEmpty()) {
                System.out.println("No pending friend requests.");
                return;
            }
    
            for (int i = 0; i < pendingFriendRequests.size(); i++) {
                System.out.println((i + 1) + ". Friend request from: " + pendingFriendRequests.get(i));
            }
    
            System.out.print("\nSelect a request to accept (0 to go back): ");
            int choice = scanner.nextInt();
            if (choice == 0) return;
    
            if (choice > 0 && choice <= pendingFriendRequests.size()) {
                EntityBareJid fromJid = pendingFriendRequests.remove(choice - 1);
                
                // Accept the friend request here
                Presence subscribed = new Presence(Presence.Type.subscribed);
                subscribed.setTo(fromJid);
                try {
                    connection.sendStanza(subscribed);
                    System.out.println("Friend request accepted successfully!");
                    
                    // Optional: If you also want to send a subscription request back to the user
                    Presence subscribe = new Presence(Presence.Type.subscribe);
                    subscribe.setTo(fromJid);
                    connection.sendStanza(subscribe);
                    
                } catch (SmackException.NotConnectedException | InterruptedException e) {
                    System.out.println("Error accepting friend request: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
    
    
    private void manageGroupInvitations(Scanner scanner) throws XmppStringprepException, InterruptedException {
        displayHeader("Group Invitations");
        synchronized (pendingGroupInvites) {
            if (pendingGroupInvites.isEmpty()) {
                System.out.println("No pending group invitations.");
                return;
            }
    
            for (int i = 0; i < pendingGroupInvites.size(); i++) {
                System.out.println((i + 1) + ". Invitation to join: " + pendingGroupInvites.get(i));
            }
    
            System.out.print("\nSelect an invitation to accept (0 to go back): ");
            int choice = scanner.nextInt();
            if (choice == 0) return;
    
            if (choice > 0 && choice <= pendingGroupInvites.size()) {
                EntityBareJid roomJid = pendingGroupInvites.remove(choice - 1);
                MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
                MultiUserChat muc = multiUserChatManager.getMultiUserChat(roomJid);
                
                // Join the group here...
                try {
                    muc.join(Resourcepart.from(connection.getUser().getLocalpartOrNull().toString()));
                    System.out.println("Group joined successfully!");
                } catch (XMPPException.XMPPErrorException | SmackException e) {
                    System.out.println("Error joining the group: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
    

    private void listenForSubscriptionRequests() {
        connection.addAsyncStanzaListener(stanza -> {
            if (stanza instanceof Presence) {
                Presence presence = (Presence) stanza;
                if (presence.getType() == Presence.Type.subscribe) {
                    pendingFriendRequests.add(presence.getFrom().asEntityBareJidIfPossible()); // Add to the list
                }
            }
        }, presenceFilter -> presenceFilter instanceof Presence && ((Presence) presenceFilter).getType() == Presence.Type.subscribe);
    }
    
    
    private void listenForGroupInvites() {
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(connection);
        manager.addInvitationListener((conn, room, inviter, reason, password, message, invite) -> {
            synchronized (pendingGroupInvites) {
                pendingGroupInvites.add(room.getRoom());
            }
        });
    }
    

    private void listenForFriendStatusChanges() {
        connection.addAsyncStanzaListener(stanza -> {
            if (stanza instanceof Presence) {
                Presence presence = (Presence) stanza;
                String from = presence.getFrom().asEntityBareJidIfPossible().toString();
                if (presence.getType() == Presence.Type.available) {
                    String status = presence.getStatus() != null ? presence.getStatus() : "Available";
                    String mode = presence.getMode() != null ? presence.getMode().toString() : "Online";
                    System.out.println("[Status Change] " + from + " is now " + mode + ": " + status);
                } else if (presence.getType() == Presence.Type.unavailable) {
                    System.out.println("[Status Change] " + from + " is now Offline.");
                }
            }
        }, presenceFilter -> presenceFilter instanceof Presence);
    }
    
    private void manageReceivedFiles(Scanner scanner) throws IOException {
        boolean goBack = false;
        while (!goBack) {
            System.out.println("\n=== Manage Received Files ===");
            for (int i = 0; i < receivedFilesBase64.size(); i++) {
                System.out.println((i + 1) + ". File " + (i + 1));
            }
            System.out.println("0. Go Back");
            System.out.print("\nSelect a file to save or go back: ");
            int choice = scanner.nextInt();
            if (choice == 0) {
                goBack = true;
            } else if (choice > 0 && choice <= receivedFilesBase64.size()) {
                System.out.print("Enter the path with filename to save the file: ");
                scanner.nextLine();  // Clear buffer
                String path = scanner.nextLine();
                saveFileFromBase64(receivedFilesBase64.get(choice - 1), path);
                System.out.println("File saved successfully!");
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private void saveFileFromBase64(String base64Content, String path) throws IOException {
        byte[] data = Base64.getDecoder().decode(base64Content);
        Files.write(Paths.get(path), data);
    }
    

    // Convert file to Base64 encoded string
    public static String encodeFileToBase64(String filePath) throws Exception {
        byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
        return Base64.getEncoder().encodeToString(fileContent);
    }

    // Convert Base64 encoded string back to file
    public static void decodeBase64ToFile(String encodedString, String outputPath) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        Files.write(Paths.get(outputPath), decodedBytes);
    }

    private void sendFileUsingBase64(Chat chat, String filePath) throws IOException, NotConnectedException, InterruptedException {
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        String encodedString = Base64.getEncoder().encodeToString(fileBytes);
        String messageToSend = "FILE:" + Paths.get(filePath).getFileName().toString() + ":" + encodedString;
        chat.send(messageToSend);
    }
  

}

