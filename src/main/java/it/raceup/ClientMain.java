package it.raceup;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {

    public static void main( String[] args ) {
        try {
            Socket socket = new Socket("localhost", 8080);
            System.out.println("Connected to " + socket.getInetAddress().getHostName());

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            Scanner s = new Scanner(System.in);
            while (true) {
                System.out.print("Insert speed value: ");
                float speed = s.nextFloat();
                output.reset();
                output.writeFloat(speed);
                output.flush();
            }
        } catch (IOException e) {
            System.out.println("Failure: " + e.getMessage());
        }
    }
}
