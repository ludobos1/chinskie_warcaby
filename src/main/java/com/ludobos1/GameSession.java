package com.ludobos1;

import com.ludobos1.message.Message;

import java.util.ArrayList;

public class GameSession {
  public Board board;
  private final ArrayList<ClientHandler> clients = new ArrayList<>();
  private final String name;

  public GameSession(Board board, String name) {
    this.board = board;
    this.name = name;
  }

  public void joinClient(ClientHandler client) {
    clients.add(client);
  }

  public void leaveClient(ClientHandler client) {
    clients.remove(client);
  }

  public ArrayList<ClientHandler> getPlayers() {
    return clients;
  }

  public String getName() {
    return name;
  }

  public void broadcastMessage(Message message) {
    for (ClientHandler client : clients) {
      client.sendMessage(message);
    }
  }
}
