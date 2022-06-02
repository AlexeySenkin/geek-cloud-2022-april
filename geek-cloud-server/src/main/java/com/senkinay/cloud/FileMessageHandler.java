package com.senkinay.cloud;

import java.io.*;
import java.net.Socket;

public class FileMessageHandler implements Runnable {

    private File dir;

    private final DataInputStream is;
    private final DataOutputStream os;

    public FileMessageHandler(Socket socket) throws IOException {

        this.is = new DataInputStream(socket.getInputStream());
        this.os = new DataOutputStream(socket.getOutputStream());

        getFilesFromServerDi();

        System.out.println("Client accepted!");
    }

    private void getFilesFromServerDi() throws IOException {
        this.dir = new File("files-server");
        os.writeUTF("#list#");
        String[] files = dir.list();

        os.writeLong(files.length);

        for (String file : files) {
            os.writeUTF(file);
        }
    }

    private void copyFileOnServer(String command) throws IOException {
        long fileSize = is.readLong();
        String fileName = is.readUTF();
        byte[] buffer = new byte[64 * 1024];
        FileOutputStream fos = new FileOutputStream(dir.getName() + "/" + fileName);
        int count;
        int total = 0;
        while ((count = is.read(buffer, 0, (4096))) != -1){
            total += count;
            fos.write(buffer, 0, count);

            if(total == fileSize)
                break;
        }
        fos.flush();
        fos.close();

        System.err.println(command + " : " + fileName + ". OK.");
    }

    @Override
    public void run() {
        try {
            while (true) {

                String command = is.readUTF();

                if (command.equals("#copyfile#")) {
                    copyFileOnServer(command);
                    getFilesFromServerDi();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
