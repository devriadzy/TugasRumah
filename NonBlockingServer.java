package TugasRumah.nonblocking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class NonBlockingServer {
    private static final int SERVER_PORT = 8888;

    public static void main(String[] args) {
        try (Selector selector = Selector.open();
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Non-Blocking Server started on port " + SERVER_PORT);

            while (true) {
                selector.select(); // Tunggu hingga channel siap

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        // Terima koneksi dari klien
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = serverChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println("New client connected: " + socketChannel.getRemoteAddress());
                    } else if (key.isReadable()) {
                        // Baca pesan dari klien
                        SocketChannel socketChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int bytesRead = socketChannel.read(buffer);
                        if (bytesRead == -1) {
                            socketChannel.close();
                            System.out.println("Client disconnected");
                        } else {
                            buffer.flip();
                            String message = new String(buffer.array(), 0, bytesRead);
                            System.out.println("Received from client: " + message);
                            // Kirim pesan kembali ke klien
                            broadcast(selector, message);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void broadcast(Selector selector, String message) {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        for (SelectionKey key : selector.keys()) {
            if (key.isValid() && key.channel() instanceof SocketChannel) {
                SocketChannel sc = (SocketChannel) key.channel();
                try {
                    sc.write(buffer);
                    buffer.rewind();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
