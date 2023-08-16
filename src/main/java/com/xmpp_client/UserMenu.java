package com.xmpp_client;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.MessageBuilder;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.StanzaBuilder;
import org.jivesoftware.smack.packet.StanzaFactory;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jivesoftware.smack.SmackException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class UserMenu {

    private AbstractXMPPConnection connection;

    public UserMenu(AbstractXMPPConnection connection) {
        this.connection = connection;
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
            System.out.println("7. Delete Account");
            System.out.println("8. Log Out");
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
                    deleteUser();
                    exit = true;
                    break;
                case 8:
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
                String user = jid.getLocalpartOrNull().toString();
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
            String user = jid.getLocalpartOrNull().toString();
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
                System.out.println("\n[ " + from.asEntityBareJidIfPossible().getLocalpart() + " ]: " + message.getBody() + "\n");
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
    
                    MessageBuilder messageBuilder = StanzaBuilder.buildMessage();
                    messageBuilder.to(entityBareJid)
                                .setBody(userMessage);
                    Message messageToSend = messageBuilder.build();
                    chat.send(messageToSend);
    
                    System.out.print("You: ");
    
                } catch (SmackException.NotConnectedException | InterruptedException | IOException e) {
                    System.out.println("Failed to send message: " + e.getMessage());
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
    
    

}

