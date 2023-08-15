package com.xmpp_client;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smack.SmackException;

import java.util.Scanner;

public class UserMenu {

    private AbstractXMPPConnection connection;

    public UserMenu(AbstractXMPPConnection connection) {
        this.connection = connection;
    }

    public void showMenu(Scanner scanner) throws XMPPException, SmackException, InterruptedException {

        boolean exit = false;

        while (!exit) {
            System.out.println("=== User Menu ===");
            System.out.println("1. Show My Profile");
            System.out.println("2. Update Presence Status");
            System.out.println("3. Manage Messages");
            System.out.println("4. Manage Contacts");
            System.out.println("5. Delete Account");
            System.out.println("6. Log Out");
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
                    // Manage Contacts
                    break;
                case 4:
                    
                    break;
                case 5:
                    deleteUser();
                    exit = true;
                    break;
                case 6:
                    logOut();
                    exit = true;
                    break;
                default:
                    System.out.println("\nInvalid choice. Please try again.\n");
            }
        }
    }

    private void updatePresence(Scanner scanner) throws SmackException.NotConnectedException, InterruptedException {
        System.out.println("=== Update Presence Status ===");
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
        String username = connection.getUser().toString(); // Get the entire JID as a string
        Roster roster = Roster.getInstanceFor(connection);
        Presence presence = roster.getPresence(connection.getUser().asEntityBareJidIfPossible()); // Get presence for the user's bare JID
    
        System.out.println("=== My Profile ===");
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
    

}

