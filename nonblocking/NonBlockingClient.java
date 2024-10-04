package TugasRumah.nonblocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NonBlockingClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(SERVER_IP, SERVER_PORT))) {
            socketChannel.configureBlocking(false);
            System.out.println("Connected to server");

            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (true) {
                System.out.print("Enter message: ");
                String message = scanner.nextLine();

                // Kirim pesan ke server
                buffer.put(message.getBytes());
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();

                // Baca pesan dari server
                int bytesRead = socketChannel.read(buffer);
                if (bytesRead > 0) {
                    buffer.flip();
                    String response = new String(buffer.array(), 0, bytesRead);
                    System.out.println("From server: " + response);
                    buffer.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
