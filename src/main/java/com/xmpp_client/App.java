package com.xmpp_client;
import java.io.IOException;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword("username", "password")
            .setHost("your_host")
            .setPort(5222)
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .build();

        AbstractXMPPConnection conn = new XMPPTCPConnection(config);
        try {
            conn.connect();
            if(conn.isConnected()) {
                System.out.println("Connected");
            }
        } catch (SmackException | IOException | XMPPException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
