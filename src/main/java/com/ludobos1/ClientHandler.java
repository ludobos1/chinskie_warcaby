package com.ludobos1;

import com.ludobos1.message.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

/**
 * Obsługuje komunikację z jednym klientem w grze.
 * Zarządza połączeniem klienta, przesyłaniem wiadomości oraz dołączaniem i opuszczaniem sesji gry.
 */
public class ClientHandler implements Runnable {
  private Socket clientSocket;
  private ObjectInputStream in;
  private ObjectOutputStream out;
  private static final List<ClientHandler> clients = new ArrayList<>();
  private static final List<GameSession> gameSessions = new ArrayList<>();
  private static final List<String> sessions = new ArrayList<>();
  private static final Map<ClientHandler, GameSession> gameSessionMap = new HashMap<>();
  private BoardService boardService;
  private Server server;

  /**
   * Tworzy nowy obiekt {@code ClientHandler} z podanym gniazdem klienta.
   *
   * @param boardService serwis planszy
   * @param server obiekt serwera
   */
  public ClientHandler(BoardService boardService, Server server, Socket clientSocket) {
    this.boardService = boardService;
    this.server = server;
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
                if (!sessions.isEmpty()||!server.getSavesNames().isEmpty()) {
                  Message sessionsMessage = new SessionsMessage(sessions, server.getSavesNames());
                  System.out.println("Wysyłam sessionsmessage: " + sessionsMessage.getContent());
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
            case BOT:
              GameSession s = gameSessionMap.get(this);
              if(s.bot==null){
                BotLogic bot = new BotLogic(s.board);
                s.addBot(bot);
                botMove(s, s.board.getActivePlayer());
              }
              else{
                sendMessage(new ErrorMessage("2"));
              }
              break;
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
                  session.board.addMove(message.getContent());
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
                  botMove(session, activePlayer);
                }
                else {
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
                createSession(board, split[2]);
                break;
          case SAVE:
            System.out.println("otrzymano save message");
            GameSession sess = gameSessionMap.get(this);
            sess.board.setTitle(message.getContent());
            boardService.saveBoardState(sess.board);
            server.setSavedBoards();
            server.setSavesNames();
            System.out.println("zapisano");
            break;
          case LOAD:
            System.out.println("otrzymano load message");
            Optional<Board> board1 = boardService.getBoardState(message.getContent());
            if(board1.isPresent()){
              Board board2 = board1.get();
              board2.initializeGame();
              board2.executeMoves();
              createSession(board2, board2.getTitle());
            }
            break;
          default:
              System.out.println("Nieznany typ wiadomości: " + message.getType());
        }
    }
    public void createSession(Board board, String title){
      String yourId = board.getPlayerId(0);
      GameSession gameSession = new GameSession(board, title);
      gameSession.joinClient(this);
      gameSessions.add(gameSession);
      gameSessionMap.put(this, gameSession);
      sessions.add(gameSession.getName());
      sendBoardState(gameSessions.indexOf(gameSession), yourId);
      Message sessionsMessage = new SessionsMessage(sessions,server.getSavesNames());
      System.out.println("wysyłam sessionsmessage: "+ sessionsMessage.getContent());
      for (ClientHandler client : clients) {
        client.sendMessage(sessionsMessage);
      }
    }

    private void botMove(GameSession session, char activePlayer){
      if(session.bot!=null && activePlayer=='D'){
        String pId=session.bot.randomPiece();
        int btargetX=session.bot.getTargetX();
        int btargetY=session.bot.getTargetY();
        if(session.board.movePiece(pId, btargetX, btargetY))
        {
            if (session.board.ifWon(pId.charAt(0))) {
              String messText;
              if (session.board.isGameOver()) {
                messText = pId.charAt(0) + ",1";
              } else {
                messText = pId.charAt(0) + ",0";
              }
              session.broadcastMessage(new WinnerMessage(messText));
            }
          String pieces = session.board.getAllPiecesInfo();
          System.out.println(pieces);
          activePlayer = session.board.getActivePlayer();
          System.out.println(activePlayer);
          System.out.println("broadcastuję message pieces: " + pieces + "activePlayer: " + activePlayer);
          session.broadcastMessage(new UpdateMessage(pieces, activePlayer));
        }
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
