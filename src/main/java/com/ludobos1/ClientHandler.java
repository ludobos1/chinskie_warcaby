package com.ludobos1;

import com.ludobos1.message.Message;
import com.ludobos1.message.TypeEnum;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
  private final Socket clientSocket;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private static final List<ClientHandler> clients = new ArrayList<>();

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
        try {
            synchronized (clients) {
                clients.add(this);
                System.out.println("Nowy klient połączony. Liczba klientów: " + clients.size());
            }
            while (true) {
                Message message = (Message) in.readObject();
                handleMessage(message);
            }

        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Błąd komunikacji z klientem: " + ex.getMessage());
        } finally {
            disconnect();
        }
    }
     private void handleMessage(Message message) {
        switch (message.getType()) {
            case MOVE:
                System.out.println("Odebrano ruch od klienta.");
                broadcastMessage(message); 
                break;
            case JOIN:
                System.out.println("Klient dołączył do gry.");
                break;
            default:
                System.out.println("Nieznany typ wiadomości: " + message.getType());
        }
    }

    private void broadcastMessage(Message message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    private void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Błąd podczas wysyłania wiadomości: " + e.getMessage());
        }
    }

    private void disconnect() {
        try {
            synchronized (clients) {
                clients.remove(this);
                System.out.println("Klient rozłączony. Liczba klientów: " + clients.size());
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            System.out.println("Błąd podczas rozłączania klienta: " + e.getMessage());
        }
    }
}
