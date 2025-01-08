package com.ludobos1;

import com.ludobos1.message.Message;
import com.ludobos1.message.SessionsMessage;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
  private final Socket clientSocket;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private static final List<ClientHandler> clients = new ArrayList<>();
  private static final List<GameSession> gameSessions = new ArrayList<>();
  private static final List<String> sessions = new ArrayList<>();
  //playerId to litera
  private final char playerId='A';

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
                if (!sessions.isEmpty()) {
                  Message sessionsMessage = new SessionsMessage(sessions);
                  this.sendMessage(sessionsMessage);
                }
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
                for (GameSession gameSession : gameSessions) {
                  if (gameSession.getPlayers().contains(this)) {
                    gameSession.broadcastMessage(message);
                  }
                }
                break;
            case JOIN:
                System.out.println("Klient dołączył do gry.");
                int index = Integer.parseInt(message.getContent());
                gameSessions.get(index).joinClient(this);
                break;
            case CREATE:
                System.out.println("Klient dodał sesje.");
                String content = message.getContent();
                String[] split = content.split(",");

            // jakie dane sa w parseint split 0 split 1?? //
            Board board = new Board(Integer.parseInt(split[0]), playerId);
                GameSession gameSession = new GameSession(board, split[4]);
                gameSession.joinClient(this);
                gameSessions.add(gameSession);
                sessions.add(gameSession.getName());
                Message sessionsMessage = new SessionsMessage(sessions);
                for (ClientHandler client : clients) {
                  client.sendMessage(sessionsMessage);
                }
                break;
            default:
                System.out.println("Nieznany typ wiadomości: " + message.getType());
        }
    }

    public void sendMessage(Message message) {
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
