package com.ludobos1;

import com.ludobos1.message.ErrorMessage;
import com.ludobos1.message.Message;
import com.ludobos1.message.SessionsMessage;
import com.ludobos1.message.UpdateMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
  private final Socket clientSocket;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private static final List<ClientHandler> clients = new ArrayList<>();
  private static final List<GameSession> gameSessions = new ArrayList<>();
  private static final List<String> sessions = new ArrayList<>();
  private static final Map<ClientHandler, GameSession> gameSessionMap = new HashMap<>();

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
              GameSession session = gameSessionMap.get(this);
              if (session != null) {
                String pieces = session.board.getAllPiecesInfo();
                char activePlayer = session.board.getActivePlayer();
                session.broadcastMessage(new UpdateMessage(pieces, activePlayer));
              } else {
                System.out.println("Klient nie należy do żadnej sesji!");
              }
                break;
            case JOIN:
                System.out.println("Klient dołączył do gry.");
                int index = Integer.parseInt(message.getContent());
                if (gameSessions.get(index).board.getPlayerNum() > gameSessions.get(index).getPlayers().size()) {
                  gameSessions.get(index).joinClient(this);
                  gameSessionMap.put(this, gameSessions.get(index));
                  String yourId = gameSessions.get(index).board.getPlayerId(gameSessions.get(index).getPlayers().size() - 1);
                  sendBoardState(index, yourId);
                } else {
                  Message errorMessage = new ErrorMessage("0");
                  sendMessage(errorMessage);
                }
                break;
            case CREATE:
                System.out.println("Klient dodał sesje.");
                String content = message.getContent();
                String[] split = content.split(",");
            // jakie dane sa w parseint split 0 split 1?? //
                Board board = new BoardBuilder().setVariant(Integer.parseInt(split[1])).setPlayerNum(Integer.parseInt(split[0])).build();
                board.initializeGame();
                String yourId = board.getPlayerId(0);
                GameSession gameSession = new GameSession(board, split[2]);
                gameSession.joinClient(this);
                gameSessions.add(gameSession);
                gameSessionMap.put(this, gameSession);
                sessions.add(gameSession.getName());
                sendBoardState(gameSessions.indexOf(gameSession), yourId);
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

    public void sendBoardState(int sessionIndex, String yourId) {
      String pieces = gameSessions.get(sessionIndex).board.getAllPiecesInfo();
      String boardSize = gameSessions.get(sessionIndex).board.toString();
      String variant = gameSessions.get(sessionIndex).board.getVariant();
      String playerNum = String.valueOf(gameSessions.get(sessionIndex).board.getPlayerNum());
      char startingPlayer = gameSessions.get(sessionIndex).board.getActivePlayer();
      Message updateMessage = new UpdateMessage(pieces, boardSize, variant, playerNum, startingPlayer, yourId);
      this.sendMessage(updateMessage);
    }

    private void disconnect() {
        try {
            synchronized (clients) {
                clients.remove(this);
                System.out.println("Klient rozłączony. Liczba klientów: " + clients.size());
            }
            GameSession gameSession = gameSessionMap.get(this);
            if (gameSession != null) {
              gameSessionMap.remove(this);
              gameSession.leaveClient(this);
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
