package com.ludobos1;

import com.ludobos1.message.Message;

import java.util.ArrayList;

public class GameSession {
  public Board board;
  private final ArrayList<ClientHandler> clients = new ArrayList<>();
  private String name;
  private int playerNum;

  public GameSession(Board board, String name, int playerNum) {
    this.board = board;
    this.name = name;
    this.playerNum = playerNum;
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
