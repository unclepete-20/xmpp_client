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
    
    while (!exit) {
        // Mostrar el menú
        System.out.println("=== ChatApp ===");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Registrarse");
        System.out.println("3. Salir");
        System.out.print("Ingrese la opción deseada: ");

        // Leer la opción ingresada por el usuario
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
                System.out.println("Opción inválida. Por favor, seleccione una opción válida.");
        }
    }

    System.out.println("Gracias por usar ChatApp. ¡Hasta pronto!");
    scanner.close();
}

    private static void iniciarSesion(Scanner scanner) throws XmppStringprepException, XMPPException, SmackException, InterruptedException {
        // Lógica para iniciar sesión
        System.out.println("== Iniciar Sesión ==");

        System.out.print("Nombre de usuario: ");
        String user = scanner.next();

        System.out.print("Contraseña: ");
        String password = scanner.next();


        Login login = new Login(user, password);

        AbstractXMPPConnection connection = login.login();

        UserMenu menu = new UserMenu(connection);
        menu.showMenu(scanner);
    }

    private static void registrarse(Scanner scanner) throws IOException {
        // Lógica para el registro de nuevos usuarios
        System.out.println("== Registrarse ==");
        
        System.out.print("Nombre de usuario: ");
        String user = scanner.next();

        System.out.print("Contraseña: ");
        String password = scanner.next();

        Register register = new Register(user, password);

        register.register();

    }
}
