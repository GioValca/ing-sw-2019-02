package server;

import controller.MatchController;
import model.Match;
import server.RMIHandler.AdrenalineRMIServer;
import server.socketHandler.AdrenalineSocketServer;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdrenalineServer {

    private Match match;
    private MatchController matchController;
    private int socketPort;
    private int rmiPort;
    private ExecutorService executor;

    public AdrenalineServer(int socketPort, int rmiPort){
        try {
            match = new Match();
            matchController = new MatchController(match);
            this.socketPort = socketPort;
            this.rmiPort = rmiPort;
            executor = Executors.newCachedThreadPool();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void launchSocketServer(){
        executor.submit(new AdrenalineSocketServer(matchController, socketPort));
    }

    public void launchRMIServer() throws RemoteException {
        executor.submit(new AdrenalineRMIServer(matchController, rmiPort));
        //TODO EDO qui devo creare l'oggetto connectionHandler che si preoccupa di creare i nuovi match e bindare i controller corretti sul registry.

    }

    public int getSocketPort() {
        return socketPort;
    }

    public int getRmiPort() {
        return rmiPort;
    }

    public static void main(String[] args) { //La porta si può chiedere come parametro di input in args

        AdrenalineServer mainServer = new AdrenalineServer(1337, Integer.parseInt(args[0]));

        try {
            //mainServer.launchSocketServer();

            //System.out.println("Launched SocketServer with IP: " + InetAddress.getLocalHost().getHostAddress() + " on port " + mainServer.getSocketPort() + ", waiting for connections");

            System.out.println("WELCOME TO ADRENALINE MAIN SERVER v1.0.0");
            System.out.println("Developed by:  GioValca, MADSOMMA, RealNGneer");
            System.out.println("All rights reserved, 2019\n\n");


            System.out.println("Launched RMIServer  with IP: " + InetAddress.getLocalHost().getHostAddress() + " on port " + mainServer.getRmiPort() + ", waiting for connection requests");

            mainServer.launchRMIServer();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
