package com.senkinay.cloud;

import com.senkinay.cloud.network.Net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CopyFileToServer implements Runnable {

    public final File file;
    public final Net net;

    String command = "#copyfile#";


    public CopyFileToServer(Net net, File file) {
        this.net = net;
        this.file = file;
    }

    private void doCopy() {
        try{
            this.net.writeUTF(command);
            this.net.getOs().writeLong(file.length());
            this.net.getOs().writeUTF(file.getName());
            FileInputStream fis = new FileInputStream(file);
            byte [] buffer = new byte[64 * 1024];
            int count;
            while((count = fis.read(buffer)) != -1){
                this.net.getOs().write(buffer, 0, count);
            }
            this.net.getOs().flush();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        doCopy();
    }
}
