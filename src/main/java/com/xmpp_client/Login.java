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

    // Getter y Setter para el nombre de user
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    // Getter y Setter para la contraseña
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void login() throws XmppStringprepException {
        // Lógica para iniciar sesión utilizando los atributos de user, contraseña, host y port.
        Dotenv dotenv = Dotenv.load();

        String username = getUser();
        String password = getPassword();
        String host = dotenv.get("HOST");
        int port = Integer.parseInt(dotenv.get("PORT"));

        String xmppDomainString = host; 

        DomainBareJid xmppDomain = JidCreate.domainBareFrom(xmppDomainString);

        System.out.println(host);
        System.out.println(port);

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(username, password)
            .setXmppDomain(xmppDomain)
            .setHost(host)
            .setPort(port)
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
