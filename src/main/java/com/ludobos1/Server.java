package com.ludobos1;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Reprezentuje serwer, który nasłuchuje połączenia od klientów i przekazuje je do odpowiednich wątków obsługujących.
 */
@SpringBootApplication
public class Server{
  private final int PORT = 1234;
  ServerSocket serverSocket;
  private volatile boolean isRunning = true;
  private static final ExecutorService threadPool = Executors.newCachedThreadPool();
  @Autowired
  private BoardService boardService;

  /**
   * Główna metoda uruchamiająca serwer.
   *
   * @param args argumenty wiersza poleceń (nieużywane)
   */
  public static void main(String[] args){
    SpringApplication.run(Server.class, args);
  }

  /**
   * Inicjalizuje i uruchamia serwer.
   * Tworzy gniazdo serwera na danym porcie i zaczyna akceptować połączenia od klientów.
   *
   */
  @PostConstruct
  public void StartServer() {
    try{
      System.out.println("Server started");
      serverSocket = new ServerSocket(PORT);
      acceptConnection();
    } catch (IOException e) {
      System.out.println("Error starting server");
    }
  }

  /**
   * Nasłuchuje połączeń przychodzących i uruchamia obsługę klienta w osobnym wątku.
   *
   */
  public void acceptConnection() {
    try{
      while (isRunning) {
        Socket clientSocket = serverSocket.accept();
        threadPool.execute(new ClientHandler(clientSocket, boardService));
      }
    } catch (IOException e) {
      System.out.println("Error starting server");
    }
  }

  @PreDestroy
  public void stopServer() {
    try {
      System.out.println("Stopping server...");
      isRunning = false;
      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();  // Zamyka serwer
      }
      threadPool.shutdown();  // Zamyka wątki w puli
    } catch (IOException e) {
      System.out.println("Error while closing the server: " + e.getMessage());
    }
  }
}
