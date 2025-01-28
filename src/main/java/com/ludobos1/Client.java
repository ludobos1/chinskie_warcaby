package com.ludobos1;

<<<<<<< HEAD
import com.ludobos1.message.BotMessage;
import com.ludobos1.message.CreateMessage;
import com.ludobos1.message.JoinMessage;
import com.ludobos1.message.Message;
import com.ludobos1.message.MoveMessage;
=======
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.ludobos1.message.*;
>>>>>>> Kuba-branch
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasa {@code Client} implementuje klienta aplikacji umożliwiającej grę wieloosobową.
 * Klient łączy się z serwerem, umożliwia tworzenie i dołączanie do gier oraz zarządza interfejsem użytkownika.
 * <p>
 * Używa JavaFX do wyświetlania GUI oraz gniazd sieciowych do komunikacji z serwerem.
 */
public class Client extends Application {
  private static final String SERVER_ADDRESS = "localhost";
  private static final int SERVER_PORT = 1234;
  private boolean running;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private Socket socket;
  private boolean isInGameSession = false;
  private String[] sessions = new String[0];
  private Map<String, String> savedBoards = new HashMap<>();
  private BorderPane menuRoot = new BorderPane();
  private Stage primaryStage;
  private int[][] boardTiles;
  private List<Piece> pieces = new ArrayList<>();
  private Map<Circle, Piece> piecesMap = new HashMap<>();
  private Board board;
  private BiMap<Integer, Circle> fields = HashBiMap.create();
  private String myId;
  private String activePlayer;
  private Circle selectedCircle;
  private Color myColor;
  private List<Circle> possibleMoves = new ArrayList<>();

  /**
   * Punkt wejścia aplikacji JavaFX.
   *
   * @param primaryStage główne okno aplikacji.
   */
  @Override
  public void start(Stage primaryStage) {
    Client client = new Client();
    client.primaryStage = primaryStage;
    client.startClient();
    client.menu();
  }


  /**
   * Główna metoda aplikacji.
   *
   * @param args argumenty przekazane wierszem poleceń.
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * Wyświetla menu główne aplikacji.
   */
  public void menu() {
    Scene scene = new Scene(menuRoot, 800, 800);
    Button createButton = new Button("Create");
    Button loadButton = new Button("Load");
    primaryStage.setOnCloseRequest(event -> {
      System.out.println("Zamykam aplikację...");
      closeConnection();
      running = false;
      Platform.exit();
    });
    createButton.setOnAction(event -> createGame());
    loadButton.setOnAction(actionEvent -> loadGameStage());
    HBox hbox = new HBox(createButton, loadButton);
    menuRoot.setTop(hbox);

    primaryStage.setTitle("Menu");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void loadGameStage() {
    Stage stage = new Stage();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setTitle("Load Game");
    VBox vBox = new VBox();
    if (!savedBoards.isEmpty()) {
      Label loadLabel = new Label("Pick game to load");
      vBox.getChildren().add(loadLabel);
      for (String key : savedBoards.keySet()) {
        Label label = new Label(savedBoards.get(key));
        Button button = new Button("Load");
        button.setOnAction(actionEvent -> {
          Message loadMessage = new LoadMessage(key);
          System.out.println("wysyłam loadMessage: " + loadMessage.getContent());
          sendMessage(loadMessage);
        });
        HBox hBox = new HBox();
        hBox.getChildren().addAll(label, button);
        vBox.getChildren().add(hBox);
      }
    } else {
      Label loadLabel = new Label("no saved games");
      vBox.getChildren().add(loadLabel);
    }
    stage.setScene(new Scene(vBox, 800, 800));
    stage.show();
  }

  /**
   * Uruchamia logikę gry, tworząc scenę gry i inicjalizując jej elementy.
   */
  public void game() {
    Platform.runLater(() -> {
      GridPane gp = new GridPane();
      gp.setHgap(7);
      gp.setVgap(7);
      for (int[] boardTile : boardTiles) {
        Circle circle = new Circle(15);
        circle.setFill(Color.GREY);
        circle.setStroke(Color.BLACK);
        int coords = boardTile[0] + 100 * boardTile[1];
        circle.setOnMouseClicked(mouseEvent -> handleCircleClick(mouseEvent, circle));
        fields.put(coords, circle);
        gp.add(circle, boardTile[0], boardTile[1]*2);
      }
      Button pass = new Button("Pass");
      Button botButton = new Button("Add Bot");
      botButton.setOnAction(actionEvent ->{
          sendMessage(new BotMessage());
      });
      pass.setOnAction(actionEvent -> {
        if (activePlayer.equals(myId)){
          removePossibleMoves();
          if (selectedCircle!=null) {
            selectedCircle.setStroke(Color.BLACK);
            selectedCircle.setRadius(selectedCircle.getRadius() + 2);
            selectedCircle.setStrokeWidth(1);
            selectedCircle = null;
          }
          Message moveMessage = new MoveMessage ("pass", 0, 0);
          System.out.println("sending move message" + moveMessage.getContent());
          sendMessage(moveMessage);
        }
      });
      gp.add(botButton, 1,0);
      gp.add(pass,0,0);
      Button save = new Button("Save");
      save.setOnAction(actionEvent -> {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Blokuje inne okna
        popupStage.setTitle("Save");
        Label label = new Label("Enter name of save");
        TextField nazwa =  new TextField();
        Button saveButton = new Button("Save");
        saveButton.setOnAction(actionEvent1 -> {
          if (!nazwa.getText().isEmpty()) {
            sendMessage(new SaveMessage(nazwa.getText()));
            popupStage.close();
          }
        });
        VBox vbox = new VBox();
        vbox.getChildren().addAll(label, nazwa, saveButton);
        popupStage.setScene(new Scene(vbox, 800, 800));
        popupStage.show();
      });
      gp.add(save,2,0);
      Scene gameScene = new Scene(gp, 1000, 1000);
      primaryStage.setScene(gameScene);
    });
  }


  /**
   * Inicjalizuje połączenie klienta z serwerem i uruchamia wątek odbierania wiadomości.
   */
  public void startClient() {
    try {
      connectClient();
      new Thread(this::recieveMessages).start();
    } catch (IOException e) { System.out.println(e.getMessage()); }
  }

  /**
   * Łączy klienta z serwerem za pomocą gniazda sieciowego.
   *
   * @throws IOException jeśli wystąpi problem z połączeniem.
   */
  private void connectClient() throws IOException {
      socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
      out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      in = new ObjectInputStream(socket.getInputStream());
      running = true;
      System.out.println("Polaczono z serwerem");
  }

  /**
   * Obsługuje odbieranie wiadomości od serwera w osobnym wątku.
   */
  private void recieveMessages() {
    while (running) {
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
  }

  /**
   * Tworzy nową grę i wysyła odpowiednią wiadomość do serwera.
   */
  private void createGame(){
    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL); // Blokuje inne okna
    popupStage.setTitle("Stwórz grę");

    ComboBox<String> variants = new ComboBox<>();
    variants.getItems().addAll("klasyczna", "przyśpieszona");
    variants.setValue("klasyczna");

    ComboBox<String> playerNumber = new ComboBox<>();
    playerNumber.getItems().addAll("2", "3", "4", "6");
    playerNumber.setValue("2");

    TextField textField = new TextField();
    textField.setPromptText("Wpisz nazwę pokoju");

    Button submitButton = new Button("Zatwierdź");

    // Dodajemy handler dla przycisku zatwierdzenia
    submitButton.setOnAction(submitEvent -> {
      int playerNum = Integer.parseInt(playerNumber.getValue());
      String selectedOption = variants.getValue();
      String gameName = textField.getText();
      if (!gameName.isEmpty()) {
        int variant;
        if (selectedOption.equals("klasyczna")) {
          variant = 1;
        } else {
          variant = 2;
        }
        Message message = new CreateMessage(playerNum, variant, gameName);
        sendMessage(message);
        popupStage.close();
      }
    });

    VBox vbox = new VBox(10);
    vbox.getChildren().addAll(playerNumber, variants, textField, submitButton);

    Scene scene = new Scene(vbox, 300, 200);
    popupStage.setScene(scene);

    isInGameSession = true;

    popupStage.show();
  }

  /**
   * Obsługuje kliknięcie w kółko na planszy gry.
   *
   * @param event  zdarzenie kliknięcia myszą.
   * @param circle kółko, które zostało kliknięte.
   */
  private void handleCircleClick(MouseEvent event, Circle circle) {
    if (activePlayer.equals(myId) && circle.getFill().equals(myColor)) {
      if (circle.getStroke().equals(Color.DARKBLUE)) {
        circle.setStroke(Color.BLACK);
        selectedCircle = null;
        removePossibleMoves();
        System.out.println("Pionek odznaczony");
      } else {
        if (selectedCircle != null) {
          selectedCircle.setStroke(Color.BLACK);
          removePossibleMoves();
        }
        circle.setStroke(Color.DARKBLUE);
        selectedCircle = circle;
        System.out.println("Pionek zaznaczony");
        Piece piece = piecesMap.get(circle);
        for (int[] tile : boardTiles) {
          System.out.println("sprawdzam legalność dla pieceId: " + piece.getPieceId()+" x = " + tile[0]+" y = " + tile[1] + " wariant: " + Integer.parseInt(board.getVariant()));
          if (board.isLegal(piece.getPieceId(),tile[0], tile[1], Integer.parseInt(board.getVariant()))){
            System.out.println("legalne pole: " + tile[0]+ " "+ tile[1]);
            Circle circle1 = fields.get(tile[0]+100*tile[1]);
            circle1.setFill(Color.LIGHTCORAL);
            possibleMoves.add(circle1);
          }else {
            System.out.println("nielegalne pole: " + tile[0] + " " + tile[1]);
          }
        }
      }
    } else if (activePlayer.equals(myId) && possibleMoves.contains(circle)) {
      removePossibleMoves();
      Piece piece = piecesMap.get(selectedCircle);
      selectedCircle.setStroke(Color.BLACK);
      selectedCircle.setRadius(selectedCircle.getRadius()+2);
      selectedCircle.setStrokeWidth(1);
      selectedCircle = null;
      int x = fields.inverse().get(circle) % 100;
      int y = (fields.inverse().get(circle) - x) / 100;
      Message moveMessage = new MoveMessage (piece.getPieceId(), x, y);
      System.out.println("sending move message" + moveMessage.getContent());
      sendMessage(moveMessage);
    } else {
      System.out.println("Nie możesz kliknąć, bo nie jesteś aktywnym graczem lub to nie twój pionek");
    }
  }

  /**
   * Usuwa możliwe ruchy na planszy z tablicy możliwych ruchów.
   */
  private void removePossibleMoves(){
    for (Circle circle : possibleMoves) {
      circle.setFill(Color.GREY);
    }
    possibleMoves.clear();
  }

  /**
   * Zamyka połączenie z serwerem i zwalnia zasoby.
   */
  private void closeConnection() {
    try{
      if (socket != null) {
        socket.close();
      }
      if (out != null) {
        out.close();
      }
      if (in != null) {
        in.close();
      }
    } catch (IOException e) { System.out.println(e.getMessage()); }
  }

  /**
   * Obsługuje wiadomość odebraną od serwera.
   *
   * @param message wiadomość odebrana od serwera.
   */
  private void handleMessage(Message message){
    switch (message.getType()) {
      case SESSIONS:
        System.out.println("recieved  sessions: " + message.getContent());
        if(message.getContent().charAt(0)!='/') {
          String[] split = message.getContent().split("/");
          sessions = split[0].split(",");
          if (split.length > 1) {
            String[] savedBoardsAndIds = split[1].split("!");
            for (String savedBoardsAndId : savedBoardsAndIds) {
              String[] idAndTitle = savedBoardsAndId.split(",");
              savedBoards.put(idAndTitle[0], idAndTitle[1]);
            }
          }
        } else {
          String savedBoardsAndIdsUnsplit = message.getContent().substring(1);
          String[] savedBoardsAndIds = savedBoardsAndIdsUnsplit.split("!");
          for (String savedBoardsAndId : savedBoardsAndIds) {
            String[] idAndTitle = savedBoardsAndId.split(",");
            savedBoards.put(idAndTitle[0], idAndTitle[1]);
          }
        }
        if(!isInGameSession) {
          writeSessions(sessions);
        }
        break;
      /*case MOVE:
        String[] messageContent = message.getContent().split(",");
        String pieceId = messageContent[0];
        int x1 = Integer.parseInt(messageContent[1]);
        int y1 = Integer.parseInt(messageContent[2]);
        board.movePiece(pieceId, x1, y1);
        break;*/
      case UPDATE:
        System.out.println("Recieved update");
        String[] updateSplit = message.getContent().split("/");
        if (updateSplit.length > 2) {
          String[] tiles = updateSplit[2].split(",");
          boardTiles = new int[tiles.length/2][2];
          try {
          for (int i = 0; i < tiles.length/2; i++) {
              boardTiles[i][0] = Integer.parseInt(tiles[2 * i]);
              boardTiles[i][1] = Integer.parseInt(tiles[2 * i + 1]);
          }
          board = new BoardBuilder().setVariant(Integer.parseInt(updateSplit[3])).setPlayerNum(Integer.parseInt(updateSplit[4])).build();
          board.initializeGame();
          myId = updateSplit[5];
          switch (myId) {
            case "A":
              myColor = Color.RED;
              break;
            case "B":
              myColor = Color.MISTYROSE;
              break;
            case "C":
              myColor = Color.GREEN;
              break;
            case "D":
              myColor = Color.BROWN;
              break;
            case "E":
              myColor = Color.BLUE;
              break;
            case "F":
              myColor = Color.YELLOW;
              break;
          }
          } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
          }
          if (boardTiles != null) {
            game();
          }
        }
        activePlayer = updateSplit[0];
        String[] piecesInfo = updateSplit[1].split(",");
        for (Circle circle : fields.values()) {
          Platform.runLater(()-> {
            circle.setFill(Color.GREY);
            circle.setRadius(15);
            circle.setStrokeWidth(1);
          });
        }
        piecesMap.clear();
        pieces.clear();
        for (int i = 0; i < piecesInfo.length/3; i++) {
          try {
            int x = Integer.parseInt(piecesInfo[3 * i]);
            int y = Integer.parseInt(piecesInfo[3 * i + 1]);
            Piece piece = new Piece(piecesInfo[3 * i + 2], x, y);
            pieces.add(piece);
            int coords = x + 100 * y;
            Platform.runLater(()->{
              piecesMap.put(fields.get(coords), piece);
              switch(piece.getPlayerId()){
                case 'A':
                  fields.get(coords).setFill(Color.RED);
                  break;
                case 'B':
                  fields.get(coords).setFill(Color.MISTYROSE);
                  break;
                case 'C':
                  fields.get(coords).setFill(Color.GREEN);
                  break;
                case 'D':
                  fields.get(coords).setFill(Color.BROWN);
                  break;
                case 'E':
                  fields.get(coords).setFill(Color.BLUE);
                  break;
                case 'F':
                  fields.get(coords).setFill(Color.YELLOW);
                  break;
              }
              if(activePlayer.equals(myId)) {
                for (Circle circle : fields.values()) {
                  if (circle.getFill().equals(myColor)) {
                    circle.setRadius(13);
                    circle.setStrokeWidth(5);
                  }
                }
              }
            });
          } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
          }
        }
        for (Piece piece : pieces) {
          System.out.println(piece.getX() + " " + piece.getY() + " " + piece.getPieceId());
        }
        board.setPieces(pieces);
        break;
      case ERROR:
        switch (message.getContent()) {
          case "0":
            Platform.runLater(()->{
              Alert alert = new Alert(Alert.AlertType.INFORMATION);
              alert.setTitle("Error joining session");
              alert.setHeaderText(null);
              alert.setContentText("Nie można dołączyć - sesja pełna");
              alert.showAndWait();
            });
            break;
          case "1":
            System.out.println("nielegalny ruch");
            break;
            case "2":
            System.out.println("Bot już istnieje");
            break;
          default:
            System.out.println("nieznany błąd");
        }
        break;
      case Winner:
        Platform.runLater(() -> {
          String[] mess = message.getContent().split(",");
          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Winner");
          alert.setHeaderText(null);
          String color;
          switch (mess[0]) {
            case "A":
              color = "red";
              break;
            case "B":
              color = "mistyrose";
              break;
            case "C":
              color = "green";
              break;
            case "D":
              color = "brown";
              break;
            case "E":
              color = "blue";
              break;
            case "F":
              color = "yellow";
              break;
            default:
              color = "błąd";
          }
          alert.setContentText("gracz " + color + " zakończył grę");
          alert.showAndWait();
          if (mess[1].equals("1")){
            System.out.println("Zamykam aplikację...");
            closeConnection();
            running = false;
            Platform.exit();
          }
        });
        break;
      default:
        System.out.println("Unrecognized message type: "+message.getType().name());
    }
  }

  /**
   * Wysyła wiadomość do serwera.
   *
   * @param message wiadomość do wysłania.
   */
  private void sendMessage(Message message) {
    try {
      out.writeObject(message);
      out.flush();
    } catch(IOException e) { System.out.println("Error sending message"); }
  }

  /**
   * Wyświetla dostępne sesje gier w interfejsie użytkownika.
   *
   * @param sessions lista dostępnych sesji gier.
   */
  private void writeSessions(String[] sessions) {
    Platform.runLater(() -> {
      if (sessions.length > 0) {
        VBox vBox = new VBox();
        Label label = new Label("Dostepne gry:");
        vBox.getChildren().add(label);
        int index = 0;
        for (String session : sessions) {
          Button sessionButton = new Button(index + ". " + session);
          int sessionIndex = index;
          sessionButton.setOnAction(event -> {
            Message message = new JoinMessage(sessionIndex);
            sendMessage(message);
            isInGameSession = true;
          });
          vBox.getChildren().add(sessionButton);
          index++;
        }
        menuRoot.setCenter(vBox);
      } else {
        Label label = new Label("Brak dostępnych gier.");
        menuRoot.setCenter(label);
      }
    });
  }
}
