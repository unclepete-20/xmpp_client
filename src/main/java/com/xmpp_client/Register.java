package com.xmpp_client;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;

import java.io.IOException;

import io.github.cdimascio.dotenv.Dotenv;

public class Register {

    private String user;
    private String password;

    public Register(String user, String password) {
        this.user = user;
        this.password = password;
    }

    // Getter and Setter for username
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    // Getter and Setter for password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void register() throws IOException {

        Dotenv dotenv = Dotenv.load();

        String username = getUser();
        String password = getPassword();
        String host = dotenv.get("HOST");

        String xmppDomainString = host;
        DomainBareJid xmppDomain = JidCreate.domainBareFrom(xmppDomainString);

        System.out.println("========================================");
        System.out.println("          Registering New User           ");
        System.out.println("  Host: " + host);
        System.out.println("  Username: " + username);
        System.out.println("========================================");

        try {
            SmackConfiguration.DEBUG = true;

            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(username, password)
                .setXmppDomain(xmppDomain)
                .setHost(host)
                .setSecurityMode(SecurityMode.disabled)
                .build();

            AbstractXMPPConnection connection = new XMPPTCPConnection(config);
            connection.connect();

            AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.createAccount(Localpart.from(username), password);

            System.out.println("\nAccount created successfully!\n");
            connection.disconnect();
        } 
        catch (SmackException | XMPPException | InterruptedException e) {
            System.out.println("[ERROR] An error occurred during registration.");
            e.printStackTrace();
        }
    }
}

