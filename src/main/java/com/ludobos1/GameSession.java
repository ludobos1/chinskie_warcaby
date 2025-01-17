package com.ludobos1;

import com.ludobos1.message.Message;

import java.util.ArrayList;

/**
 * Reprezentuje sesję gry, która zarządza graczami, planszą i przesyłaniem wiadomości między klientami.
 */
public class GameSession {
  public Board board;
  private final ArrayList<ClientHandler> clients = new ArrayList<>();
  private final String name;

  /**
   * Tworzy nową sesję gry.
   *
   * @param board plansza do gry
   * @param name  nazwa sesji gry
   */
  public GameSession(Board board, String name) {
    this.board = board;
    this.name = name;
  }

  /**
   * Dodaje klienta do sesji gry.
   *
   * @param client klient do dodania do sesji
   */
  public void joinClient(ClientHandler client) {
    clients.add(client);
  }

  /**
   * Usuwa klienta z sesji gry.
   *
   * @param client klient do usunięcia z sesji
   */
  public void leaveClient(ClientHandler client) {
    clients.remove(client);
  }

  /**
   * Zwraca listę graczy biorących udział w sesji.
   *
   * @return lista graczy
   */
  public ArrayList<ClientHandler> getPlayers() {
    return clients;
  }


  /**
   * Zwraca nazwę sesji gry.
   *
   * @return nazwa sesji
   */
  public String getName() {
    return name;
  }

  /**
   * Rozsyła wiadomość do wszystkich graczy w sesji.
   *
   * @param message wiadomość do rozesłania
   */
  public void broadcastMessage(Message message) {
    for (ClientHandler client : clients) {
      client.sendMessage(message);
    }
  }
}
