package server.socketHandler;

import controller.MatchController;
import server.socketHandler.SocketClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// !!  DRAFT  !!


public class AdrenalineSocketServer implements Runnable{
    private MatchController matchController;
    private int port;
    public AdrenalineSocketServer(MatchController controller, int port) {
        matchController = controller;
        this.port = port;
    }
    public void run() {
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage()); // porta non disponibile
            return;
        }

        System.out.println("[SocketServer]: ready to accept connections from clients");


        while (true) {
            try {
                Socket socket = serverSocket.accept();
                executor.submit(new SocketClientHandler(socket));
            } catch(IOException e) {
                break; // entrerei qui se serverSocket venisse chiuso
            }
            finally {
                try {
                    serverSocket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            if(serverSocket.isClosed())
                break;
        }
        executor.shutdown();
    }
    /*
    public static void main(String[] args) {
        AdrenalineSocketServer echoServer = new AdrenalineSocketServer(1337);
        echoServer.startServer();
    }

     */
}