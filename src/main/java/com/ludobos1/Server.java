package com.ludobos1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server{
  private final int PORT = 8888;
  private static ExecutorService threadPool = Executors.newCachedThreadPool();

  public static void main(String[] args){
    Server server = new Server();
    try {
      server.StartServer();
    } catch (IOException e) {
      System.out.println("Error starting server");
    }
  }

  public void StartServer() throws IOException {
    System.out.println("Server started");
    ServerSocket serverSocket = new ServerSocket(PORT);
    acceptConnection(serverSocket);
  }

  public void acceptConnection(ServerSocket serverSocket) throws IOException {
    while (true) {
      Socket clientSocket = serverSocket.accept();
      threadPool.execute(new ClientHandler(clientSocket));
    }
  }
}
