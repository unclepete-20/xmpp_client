package com.xmpp_client;
import java.io.IOException;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import io.github.cdimascio.dotenv.Dotenv;

public class Login {
    private String user;
    private String password;

    public Login(String user, String password) {
        this.user = user;
        this.password = password;
    }

    // Getter and Setter for the username
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    // Getter and Setter for the password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AbstractXMPPConnection login() throws XmppStringprepException {
        // Logic for logging in using user, password, host, and port attributes.
        Dotenv dotenv = Dotenv.load();

        String username = getUser();
        String password = getPassword();
        String host = dotenv.get("HOST");

        String xmppDomainString = host; 
        DomainBareJid xmppDomain = JidCreate.domainBareFrom(xmppDomainString);

        System.out.println("========================================");
        System.out.println("          Attempting to Connect          ");
        System.out.println("  Host: " + host);
        System.out.println("  Username: " + username);
        System.out.println("========================================");

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(username, password)
            .setXmppDomain(xmppDomain)
            .setHost(host)
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .build();

        AbstractXMPPConnection conn = new XMPPTCPConnection(config);
        try {
            conn.connect();
            if (conn.isConnected()) {
                System.out.println("\nConnected to the server successfully.\n");
            }
    
            conn.login(username, password);
    
            if (conn.isAuthenticated()) {
                System.out.println("Authenticated successfully!\n");
            } else {
                System.out.println("[ERROR] Failed to authenticate connection.\n");
            }
        } catch (SmackException | IOException | XMPPException | InterruptedException e) {
            System.out.println("[ERROR] An error occurred during connection.");
            e.printStackTrace();
        }

        return conn;
    }
}


