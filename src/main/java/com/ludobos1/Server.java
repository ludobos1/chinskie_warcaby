package com.ludobos1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Reprezentuje serwer, który nasłuchuje połączenia od klientów i przekazuje je do odpowiednich wątków obsługujących.
 */
public class Server{
  private final int PORT = 1234;
  private static ExecutorService threadPool = Executors.newCachedThreadPool();

  /**
   * Główna metoda uruchamiająca serwer.
   *
   * @param args argumenty wiersza poleceń (nieużywane)
   */
  public static void main(String[] args){
    Server server = new Server();
    try {
      server.StartServer();
    } catch (IOException e) {
      System.out.println("Error starting server");
    }
  }

  /**
   * Inicjalizuje i uruchamia serwer.
   * Tworzy gniazdo serwera na danym porcie i zaczyna akceptować połączenia od klientów.
   *
   * @throws IOException jeśli wystąpił błąd podczas tworzenia gniazda lub akceptowania połączeń
   */
  public void StartServer() throws IOException {
    System.out.println("Server started");
    ServerSocket serverSocket = new ServerSocket(PORT);
    acceptConnection(serverSocket);
  }

  /**
   * Nasłuchuje połączeń przychodzących i uruchamia obsługę klienta w osobnym wątku.
   *
   * @param serverSocket gniazdo serwera do akceptowania połączeń
   * @throws IOException jeśli wystąpił błąd przy akceptowaniu połączenia
   */
  public void acceptConnection(ServerSocket serverSocket) throws IOException {
    while (true) {
      Socket clientSocket = serverSocket.accept();
      threadPool.execute(new ClientHandler(clientSocket));
    }
  }
}
