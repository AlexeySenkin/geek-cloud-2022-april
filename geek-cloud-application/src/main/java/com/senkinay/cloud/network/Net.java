package com.senkinay.cloud.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


import com.senkinay.cloud.model.AbstractMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;


public class Net {
    private final Socket socket;
    private final ObjectDecoderInputStream is;
    private final ObjectEncoderOutputStream os;
    private final String host;

    private final int port;

    public Net(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.socket = new Socket(host, port);

        this.os = new ObjectEncoderOutputStream(socket.getOutputStream());
        this.is = new ObjectDecoderInputStream(socket.getInputStream());
    }

    public AbstractMessage read() throws IOException, ClassNotFoundException {
        return (AbstractMessage) is.readObject();
    }


    public void write(AbstractMessage message) throws IOException {
        os.writeObject(message);
    }


}
