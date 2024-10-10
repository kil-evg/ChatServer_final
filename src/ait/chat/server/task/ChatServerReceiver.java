package ait.chat.server.task;

import ait.chat.model.Message;
import ait.mediation.BlkQueue;

import java.io.*;
import java.net.Socket;

public class ChatServerReceiver implements Runnable {
    private final Socket socket;
    private final BlkQueue<Message> messageBox;

    public ChatServerReceiver(Socket socket, BlkQueue<Message> messageBox) {
        this.socket = socket;
        this.messageBox = messageBox;
    }

    @Override
    public void run() {
        try (Socket socket = this.socket;
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());) {
            while (true) {
                try {
                    Message message = (Message) ois.readObject();
                    messageBox.push(message);
                } catch (EOFException e) {
                    System.out.println("Connection: " + socket.getInetAddress() + ":" + socket.getPort() + ", closed");
                    break;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
