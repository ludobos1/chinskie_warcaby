package com.ludobos1;

import com.ludobos1.message.Message;
import com.ludobos1.message.MoveMessage;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;


public class Client {
  private static final String SERVER_ADDRESS = "localhost";
  private static final int SERVER_PORT = 8888;
  private boolean running;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private Scanner scanner;
  private Socket socket;

  public static void main(String[] args) {
    Client client = new Client();
    client.startClient();
  }
  public void startClient() {
    try {
      connectClient();
      new Thread(this::recieveMessages);
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
      System.out.print("Connection to server established");
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
      System.out.print("Enter move: '(a1,a2) (b1,b2)' or 'exit' to exit the game");
      String input = scanner.nextLine();
      if (input.equals("exit")) {
        System.out.println("Closing client");
        running = false;
        closeConnection();
        break;
      }
      String[] splitted = input.split(" ");
      if (splitted.length != 2) {
        System.out.println("Invalid input");
        continue;
      }

      String fromTrim = splitted[0].substring(1, splitted[0].length()-1);
      String toTrim = splitted[1].substring(1, splitted[1].length()-1);
      String[] from = fromTrim.split(",");
      String[] to = toTrim.split(",");
      if (to.length != 2 || from.length != 2) {
        System.out.println("Invalid input -  too much move arguments");
        continue;
      }
      Message moveMessage = new MoveMessage(Integer.parseInt(from[0]), Integer.parseInt(from[1]),
              Integer.parseInt(to[0]), Integer.parseInt(to[1]));
      sendMessage(moveMessage);
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
      case UPDATE:
        System.out.print("Recieved opps move");
        // Dodać handler dla update
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
