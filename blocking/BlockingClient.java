package TugasRumah.blocking;

import java.io.*;
import java.net.*;

public class BlockingClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            // Thread untuk menerima pesan dari server
            new Thread(() -> {
                String serverMessage;
                try {
                    while ((serverMessage = input.readLine()) != null) {
                        System.out.println("From server: " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Kirim pesan ke server
            String clientMessage;
            while (true) {
                // Tampilkan "Enter Message: " sebelum membaca input dari user
                System.out.print("Enter Message: ");
                clientMessage = consoleInput.readLine();

                // Kirim pesan ke server
                if (clientMessage != null) {
                    output.println(clientMessage);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}