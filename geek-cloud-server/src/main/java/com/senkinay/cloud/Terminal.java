package com.senkinay.cloud;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class Terminal {

    // TODO: 19.04.2022 implement commands: cat, cd, mkdir, touch

    private final Path dir;
    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private final ByteBuffer buffer = ByteBuffer.allocate(256);

    public Terminal() throws IOException {

        dir = Path.of("D:/GeegBrains/JAVA/geek-cloud-2022-april/files-server");


        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(8189));
        serverChannel.configureBlocking(false);

        selector = Selector.open();

        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started on port: 8189");

        while (serverChannel.isOpen()) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            try {
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    }
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                    iterator.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRead(SelectionKey key) throws IOException {

        SocketChannel channel = (SocketChannel) key.channel();

        String message = readMessageFromChannel(channel).trim();

        System.out.println("Received: " + message);

        switch (message) {
            case "1" ->  //ls
                    channel.write(ByteBuffer.wrap(
                                    getLsResultString().getBytes(StandardCharsets.UTF_8)
                            )
                    );
            case "2" ->   //cat
                    channel.write(ByteBuffer.wrap(
                                    getCatResultString("file-1.java").getBytes(StandardCharsets.UTF_8)
                            )
                    );
            case "3" ->   //mkdir
                    channel.write(ByteBuffer.wrap(
                                    getMkDirResultString("server-1").getBytes(StandardCharsets.UTF_8)
                            )
                    );
            default -> channel.write(ByteBuffer.wrap("Unknown command\n\r".getBytes(StandardCharsets.UTF_8)));
        }
        channel.write(ByteBuffer.wrap("-> ".getBytes(StandardCharsets.UTF_8)));
    }

    private String getLsResultString() throws IOException {
        return Files.list(dir)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.joining("\n\r")) + "\n\r";
    }

    private String getCatResultString(String fileName) throws IOException {
        Path file = Path.of(dir.toString() + "/" + fileName );
        byte[] bytes = Files.readAllBytes(file);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private String getMkDirResultString(String dirName) {
        try {
            Path file = Path.of(dir.toString() + "/" + dirName );
            Files.createDirectory(file);
        } catch(FileAlreadyExistsException e){
            String error = "folder already exists";
            byte[] result = error.getBytes(StandardCharsets.UTF_8);
            return new String(result, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] result = dirName.getBytes(StandardCharsets.UTF_8);
        return new String(result, StandardCharsets.UTF_8);
    }

    private String readMessageFromChannel(SocketChannel channel) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int readCount = channel.read(buffer);
            if (readCount == -1) {
                channel.close();
                break;
            }
            if (readCount == 0) {
                break;
            }
            buffer.flip();
            while (buffer.hasRemaining()) {
                sb.append((char) buffer.get());
            }
            buffer.clear();
        }
        return sb.toString();
    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        System.out.println("Client accepted...");
        channel.write(ByteBuffer.wrap("Welcome in SenkinAlexey terminal!\n\r-> ".getBytes(StandardCharsets.UTF_8)));
    }

    public static void main(String[] args) throws IOException {
        new Terminal();
    }
}
