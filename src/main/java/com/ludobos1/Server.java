package com.ludobos1;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Reprezentuje serwer, który nasłuchuje połączenia od klientów i przekazuje je do odpowiednich wątków obsługujących.
 */
@SpringBootApplication
public class Server{
  private final int PORT = 1234;
  private ServerSocket serverSocket;
  private volatile boolean isRunning = true;
  private final ExecutorService threadPool = Executors.newCachedThreadPool();
  @Autowired
  private BoardService boardService;
  private List<Board> savedBoards;
  private List<String> savesNames = new ArrayList<>();
  @Autowired
  private ApplicationContext applicationContext;


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
      setSavedBoards();
      setSavesNames();
      startExitListener();
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
        threadPool.execute(new ClientHandler(boardService,this, clientSocket));
      }
    } catch (IOException e) {
      System.out.println("Error starting server");
    }
  }


  public void addBoard(Board board) {
    savedBoards.add(board);
    savesNames.add(board.getTitle());
  }

  public void removeBoard(Board board) {
    savedBoards.remove(board);
    savesNames.remove(board.getTitle());
  }

  public List<Board> getSavedBoards() {
    return savedBoards;
  }
  public void setSavedBoards(){
    savedBoards = boardService.getAllBoards();
  }
  public List<String> getSavesNames() {
    return savesNames;
  }
  public void setSavesNames() {
    for (Board board : savedBoards) {
      savesNames.add(board.getId());
      savesNames.add(board.getTitle());
    }
  }

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
    } finally {
      threadPool.shutdown();
      System.out.println("Server stopped.");
    }
  }
  private void startExitListener() {
    Thread exitListenerThread = new Thread(() -> {
      try (Scanner scanner = new Scanner(System.in)) {
        while (isRunning) {
          String input = scanner.nextLine();
          if ("exit".equalsIgnoreCase(input)) {
            stopServer();
          }
        }
      }
    });
    exitListenerThread.setDaemon(true);
    exitListenerThread.start();
  }
}
