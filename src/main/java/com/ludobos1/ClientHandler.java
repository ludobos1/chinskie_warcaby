package com.ludobos1;

import com.ludobos1.message.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Obsługuje komunikację z jednym klientem w grze.
 * Zarządza połączeniem klienta, przesyłaniem wiadomości oraz dołączaniem i opuszczaniem sesji gry.
 */
@Component
public class ClientHandler implements Runnable {
  private final Socket clientSocket;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private static final List<ClientHandler> clients = new ArrayList<>();
  private static final List<GameSession> gameSessions = new ArrayList<>();
  private static final List<String> sessions = new ArrayList<>();
  private static final Map<ClientHandler, GameSession> gameSessionMap = new HashMap<>();
  private BoardService boardService;

  /**
   * Tworzy nowy obiekt {@code ClientHandler} z podanym gniazdem klienta.
   *
   * @param socket gniazdo klienta
   */
  @Autowired
  public ClientHandler(Socket socket, BoardService boardService) {
    this.boardService = boardService;
    this.clientSocket = socket;
    try {
      in = new ObjectInputStream(clientSocket.getInputStream());
      out = new ObjectOutputStream(clientSocket.getOutputStream());
      out.flush();
    }catch (IOException ex) {
      System.out.println(ex.getMessage());
    }
  }

  /**
   * Obsługuje komunikację z klientem.
   * Oczekuje na wiadomości od klienta, przetwarza je i reaguje na różne typy wiadomości.
   */
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

  /**
   * Przetwarza wiadomości od klienta i wykonuje odpowiednie akcje w zależności od typu wiadomości.
   *
   * @param message wiadomość od klienta
   */
     private void handleMessage(Message message) {
        switch (message.getType()) {
            case MOVE:
              System.out.println("Odebrano ruch od klienta: " + message.getContent());
              GameSession session = gameSessionMap.get(this);
              String[] messageContent = message.getContent().split(",");
              String pieceId = messageContent[0];
              int x = Integer.parseInt(messageContent[1]);
              int y = Integer.parseInt(messageContent[2]);
              if (session != null) {
                System.out.println("Wykonuję ruch: " + pieceId + " " + x + " " + y);
                if (session.board.movePiece(pieceId, x, y)) {
                  if (!pieceId.equals("pass")) {
                    if (session.board.ifWon(pieceId.charAt(0))) {
                      String messText;
                      if (session.board.isGameOver()) {
                        messText = pieceId.charAt(0) + ",1";
                      } else {
                        messText = pieceId.charAt(0) + ",0";
                      }
                      session.broadcastMessage(new WinnerMessage(messText));
                    }
                  }
                  String pieces = session.board.getAllPiecesInfo();
                  System.out.println(pieces);
                  char activePlayer = session.board.getActivePlayer();
                  System.out.println(activePlayer);
                  System.out.println("broadcastuję message pieces: " + pieces + "activePlayer: " + activePlayer);
                  session.broadcastMessage(new UpdateMessage(pieces, activePlayer));
                  session.broadcastMessage(message);
                } else {
                  this.sendMessage(new ErrorMessage("1"));
                }
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
          case SAVE:
            System.out.println("otrzymano save message");
            GameSession sess = gameSessionMap.get(this);
            boardService.saveBoardState(sess.board);
            System.out.println("zapisano");
            break;
            default:
                System.out.println("Nieznany typ wiadomości: " + message.getType());
        }
    }

  /**
   * Wysyła wiadomość do klienta.
   *
   * @param message wiadomość do wysłania
   */
    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Błąd podczas wysyłania wiadomości: " + e.getMessage());
        }
    }

  /**
   * Wysyła stan planszy do klienta.
   *
   * @param sessionIndex indeks sesji w liście sesji
   * @param yourId identyfikator gracza
   */
    public void sendBoardState(int sessionIndex, String yourId) {
      String pieces = gameSessions.get(sessionIndex).board.getAllPiecesInfo();
      String boardSize = gameSessions.get(sessionIndex).board.toString();
      String variant = gameSessions.get(sessionIndex).board.getVariant();
      String playerNum = String.valueOf(gameSessions.get(sessionIndex).board.getPlayerNum());
      char startingPlayer = gameSessions.get(sessionIndex).board.getActivePlayer();
      Message updateMessage = new UpdateMessage(pieces, boardSize, variant, playerNum, startingPlayer, yourId);
      System.out.println("wysyłam update: " + updateMessage.getContent());
      this.sendMessage(updateMessage);
    }

  /**
   * Zamyka połączenie z klientem.
   */
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
