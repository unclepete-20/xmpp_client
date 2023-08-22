package com.xmpp_client;
import java.io.IOException;
import java.util.Scanner;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jxmpp.stringprep.XmppStringprepException;

public final class App {
    private App() {
    }

    public static void main(String[] args) throws IOException, XMPPException, SmackException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        
        System.out.println("========================================");
        System.out.println("              WELCOME TO LIBBER         ");
        System.out.println("========================================");

        while (!exit) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrarse");
            System.out.println("3. Salir");
            System.out.print("\nSeleccione una opción: ");

            int opcion = scanner.nextInt();
            
            switch (opcion) {
                case 1:
                    iniciarSesion(scanner);
                    break;
                case 2:
                    registrarse(scanner);
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    System.out.println("\n[ERROR] Opción inválida. Intente nuevamente.\n");
            }
        }

        System.out.println("\n========================================");
        System.out.println("    Gracias por usar Libber. ¡Adiós!   ");
        System.out.println("========================================");
        scanner.close();
    }

    private static void iniciarSesion(Scanner scanner) throws XmppStringprepException, XMPPException, SmackException, InterruptedException {
        System.out.println("\n----------------------------------------");
        System.out.println("              Iniciar Sesión             ");
        System.out.println("----------------------------------------");

        System.out.print("\nNombre de usuario: ");
        String user = scanner.next();

        System.out.print("Contraseña: ");
        String password = scanner.next();

        Login login = new Login(user, password);

        try { 
            AbstractXMPPConnection connection = login.login();
            if (connection.isAuthenticated()) {
                UserMenu menu = new UserMenu(connection);
                menu.showMenu(scanner);
            } else {
                System.out.println("\n[ERROR] Fallo en la autenticación.\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void registrarse(Scanner scanner) throws IOException {
        System.out.println("\n----------------------------------------");
        System.out.println("               Registrarse               ");
        System.out.println("----------------------------------------");

        System.out.print("\nNombre de usuario: ");
        String user = scanner.next();

        System.out.print("Contraseña: ");
        String password = scanner.next();

        Register register = new Register(user, password);
        register.register();
    }
}

