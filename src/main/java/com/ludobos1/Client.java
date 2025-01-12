package com.ludobos1;

import com.ludobos1.message.CreateMessage;
import com.ludobos1.message.JoinMessage;
import com.ludobos1.message.Message;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;


public class Client extends Application {
  private static final String SERVER_ADDRESS = "localhost";
  private static final int SERVER_PORT = 1234;
  private boolean running;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private Socket socket;
  private boolean isInGameSession = false;
  private String[] sessions = new String[0];
  private BorderPane menuRoot = new BorderPane();
  private Stage primaryStage;
  private int[][] boardTiles;
  private List<Piece> pieces = new ArrayList<>();
  private Board board;
  private Map<Integer, Circle> fields = new HashMap<>();

  @Override
  public void start(Stage primaryStage) {
    Client client = new Client();
    client.primaryStage = primaryStage;
    client.startClient();
    client.menu();
  }

  public static void main(String[] args) {
    launch(args);
  }

  public void menu() {
    Scene scene = new Scene(menuRoot, 800, 800);
    Button createButton = new Button("Create");
    primaryStage.setOnCloseRequest(event -> {
      System.out.println("Zamykam aplikację...");
      closeConnection();
      running = false;
      Platform.exit();
    });
    createButton.setOnAction(event -> createGame());
    menuRoot.setTop(createButton);

    primaryStage.setTitle("Menu");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public void game() {
    Platform.runLater(() -> {
      GridPane gp = new GridPane();
      gp.setHgap(7);
      gp.setVgap(7);
      for (int[] boardTile : boardTiles) {
        Circle circle = new Circle(15);
        circle.setFill(Color.GREY);
        circle.setStroke(Color.BLACK);
        int coords = boardTile[0] + 10 * boardTile[1];
        fields.put(coords, circle);
        gp.add(circle, boardTile[0], boardTile[1]*2);
      }
      Scene gameScene = new Scene(gp, 1000, 1000);

      primaryStage.setScene(gameScene);
    });
  }

  public void startClient() {
    try {
      connectClient();
      new Thread(this::recieveMessages).start();
      //userInput();
    } catch (IOException e) { System.out.println(e.getMessage()); }
  }
  private void connectClient() throws IOException {
      socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
      out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      in = new ObjectInputStream(socket.getInputStream());
      running = true;
      System.out.println("Polaczono z serwerem");
  }
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

  private void createGame(){
    Stage popupStage = new Stage();
    popupStage.initModality(Modality.APPLICATION_MODAL); // Blokuje inne okna
    popupStage.setTitle("Stwórz grę");

    ComboBox<String> variants = new ComboBox<>();
    variants.getItems().addAll("wariant 1", "wariant 2");
    variants.setValue("wariant 1");

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
        if (selectedOption.equals("wariant 1")) {
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
  private void handleMessage(Message message){
    switch (message.getType()) {
      case SESSIONS:
        sessions = message.getContent().split(",");
        if(!isInGameSession) {
          writeSessions(sessions);
        }
        break;
      case UPDATE:
        System.out.println("Recieved update");
        String[] updateSplit = message.getContent().split("/");
        if (updateSplit.length > 1) {
          String[] tiles = updateSplit[1].split(",");
          boardTiles = new int[tiles.length/2][2];
          try {
          for (int i = 0; i < tiles.length/2; i++) {
              boardTiles[i][0] = Integer.parseInt(tiles[2 * i]);
              boardTiles[i][1] = Integer.parseInt(tiles[2 * i + 1]);
          }
          board = new BoardBuilder().setVariant(Integer.parseInt(updateSplit[2])).setPlayerNum(Integer.parseInt(updateSplit[3])).build();
          board.initializeGame();
          } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
          }
          if (boardTiles != null) {
            game();
          }
        }
        String[] piecesInfo = updateSplit[0].split(",");
        for (int i = 0; i < piecesInfo.length/3; i++) {
          try {
            int x = Integer.parseInt(piecesInfo[3 * i]);
            int y = Integer.parseInt(piecesInfo[3 * i + 1]);
            Piece piece = new Piece(piecesInfo[3 * i + 2], x, y);
            pieces.add(piece);
            int coords = x + 10 * y;
            Platform.runLater(()->{
              switch(piece.getPlayerId()){
                case 'A':
                  fields.get(coords).setFill(Color.RED);
                  break;
                case 'B':
                  fields.get(coords).setFill(Color.BLACK);
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
            });
          } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
          }
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
        }
        break;
      default:
        System.out.println("Unrecognized message type: "+message.getType().name());
    }
  }
  private void sendMessage(Message message) {
    try {
      out.writeObject(message);
      out.flush();
    } catch(IOException e) { System.out.println("Error sending message"); }
  }
  private void writeSessions(String[] sessions) {
    Platform.runLater(() -> {
      if (sessions.length > 0) {
        VBox vBox = new VBox();
        TextArea textArea = new TextArea("Dostepne gry:");
        vBox.getChildren().add(textArea);
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
        TextArea textArea = new TextArea("Brak dostępnych gier.");
        menuRoot.setCenter(textArea);
      }
    });
  }
}

/*private void userInput() {
    while (running) {
      if (!isInGameSession) {
        writeSessions(sessions);
        System.out.println("Wpisz 'x name' gdzie x rozmiar boku planszy a name to nazwa sesji aby" +
                " stworzyc nowa gre lub podaj numer istniejacej gry z listy aby dolaczyc. 'exit' - wyjscie");
        String input = scanner.nextLine();
        String[] tok = input.split(" ");
        if (tok[0].equals("exit")) {
          System.out.println("Closing client");
          running = false;
          closeConnection();
          break;
        }
        if (tok.length==1) {
          try {
            int x = Integer.parseInt(tok[0]);
            if (x >= sessions.length || x < 0) {
              System.out.println("zla dana");
            } else {
              Message joinMessage = new JoinMessage(x);
              sendMessage(joinMessage);
              isInGameSession = true;
            }
            continue;
          } catch(NumberFormatException e) {
            System.out.println("zla dana");
            continue;
          }
        }
        String name = tok[1];
        String number = tok[0];
        try {
        Message createMessage = new CreateMessage(Integer.parseInt(number), name);
        sendMessage(createMessage);
        isInGameSession = true;
        } catch (NumberFormatException e) {
          System.out.println("zle dane");
        }
        continue;
      }
      System.out.println("Podaj ruch wpisujac: '(a1,a2) (b1,b2)' lub 'exit' aby wyjsc");
      String input = scanner.nextLine();
      if (input.equals("exit")) {
        System.out.println("Closing client");
        running = false;
        closeConnection();
        break;
      }
      String[] splitted = input.split(" ");
      if (splitted.length != 2) {
        System.out.println("zle dane");
        continue;
      }

      String fromTrim = splitted[0].substring(1, splitted[0].length()-1);
      String toTrim = splitted[1].substring(1, splitted[1].length()-1);
      String[] from = fromTrim.split(",");
      String[] to = toTrim.split(",");
      if (to.length != 2 || from.length != 2) {
        System.out.println("zle dane");
        continue;
      }
      try {
        Message moveMessage = new MoveMessage(Integer.parseInt(from[0]), Integer.parseInt(from[1]),
                Integer.parseInt(to[0]), Integer.parseInt(to[1]));
        sendMessage(moveMessage);
      } catch (NumberFormatException e) {
        System.out.println("zle dane");
      }
    }
  }*/
