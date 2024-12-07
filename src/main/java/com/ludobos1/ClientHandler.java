package com.ludobos1;

import com.ludobos1.message.Message;
import com.ludobos1.message.TypeEnum;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
  private final Socket clientSocket;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  public ClientHandler(Socket socket) {
    this.clientSocket = socket;
    try {
      in = new ObjectInputStream(clientSocket.getInputStream());
      out = new ObjectOutputStream(clientSocket.getOutputStream());
      out.flush();
    }catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
  }

  @Override
  public void run() {
    try{
      Message message;
      do  {
        message = (Message) in.readObject();
        handleMessage(message);
      } while (true);
    }catch (IOException | ClassNotFoundException ex) {
      System.out.println(ex.getMessage());
    }
  }
  public void handleMessage(Message message) {
    switch (message.getType()) {
      case MOVE:
        System.out.println("Client sent move");
        // Dodać move handler
        break;
      case JOIN:
        System.out.println("Client joins game");
        // Dodać join handler
        break;
    }
  }
}
