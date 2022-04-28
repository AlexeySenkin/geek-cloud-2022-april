package com.senkinay.cloud.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Net {
    private final Socket socket;
    private final DataInputStream is;
    private final DataOutputStream os;

    private final String host;
    private final int port;

    public Net(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.socket = new Socket(host, port);
        this.is = new DataInputStream(socket.getInputStream());
        this.os = new DataOutputStream(socket.getOutputStream());
    }

    public Long readLong() throws IOException {
        return is.readLong();
    }
    public String readUtf() throws IOException {
        return is.readUTF();
    }
}
