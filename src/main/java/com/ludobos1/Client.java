package com.ludobos1;

import com.ludobos1.message.CreateMessage;
import com.ludobos1.message.JoinMessage;
import com.ludobos1.message.Message;
import com.ludobos1.message.MoveMessage;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Client {
  private static final String SERVER_ADDRESS = "localhost";
  private static final int SERVER_PORT = 8888;
  private boolean running;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private Scanner scanner;
  private Socket socket;
  private boolean isInGameSession = false;
  private String[] sessions = new String[0];

  public static void main(String[] args) {
    Client client = new Client();
    client.startClient();
  }
  public void startClient() {
    try {
      connectClient();
      new Thread(this::recieveMessages).start();
      userInput();
    } catch (IOException e) { System.out.println(e.getMessage()); }
  }
  private void connectClient() throws IOException {
      socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
      out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      in = new ObjectInputStream(socket.getInputStream());
      scanner = new Scanner(System.in);
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
  private void userInput() {
    while (running) {
      if (!isInGameSession) {
        if (sessions.length > 0) {
          System.out.println("Dostepne gry:");
          int index = 0;
          for (String session : sessions) {
            System.out.println(index + ". " + session);
            index++;
          }
        } else {
          System.out.println("Brak dostępnych gier");
        }
        System.out.println("Wpisz '(x1,x2,y1,y2) name' gdzie x i y to rozmiary planszy a name to nazwa sesji aby" +
                " stworzyc nowa gre lub podaj numer istniejacej gry z listy aby dolaczyc");
        String input = scanner.nextLine();
        String[] tok = input.split(" ");
        if (tok.length==1) {
          try {
            int x = Integer.parseInt(tok[0]);
            if (x >= sessions.length || x < 0) {
              System.out.println("zla dana");
              continue;
            } else {
              Message joinMessage = new JoinMessage(x);
              sendMessage(joinMessage);
              isInGameSession = true;
              continue;
            }
          } catch(NumberFormatException e) {
            System.out.println("zla dana");
            continue;
          }
        }
        String name = tok[1];
        String trim = tok[0].substring(1, tok[0].length() - 1);
        String[] tokens = trim.split(",");
        if (tokens.length != 4) {
          System.out.println("zla liczba argumentow");
          continue;
        }
        try {
        Message createMessage = new CreateMessage(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]),
                Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), name);
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
      case MOVE:
        System.out.print("Recieved opps move");
        // Dodać handler dla move
        break;
      case SESSIONS:
        sessions = message.getContent().split(",");
        break;
    }
  }
  private void sendMessage(Message message) {
    try {
      out.writeObject(message);
      out.flush();
    } catch(IOException e) { System.out.println("Error sending message"); }
  }
}
