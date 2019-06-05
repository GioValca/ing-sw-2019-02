package client.clientController;

//this is the remote object that the client shares with the server in order to help the server push information whenever needed
//info such as Model updates, turn status or other stuff.

import client.GUI.FirstPage;
import commons.InterfaceClientControllerRMI;
import javafx.application.Platform;
import model.Color;
import model.Match;
import model.player.Player;
import model.powerup.PowerUp;
import model.powerup.PowerUpName;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ReceiverClientControllerRMI extends UnicastRemoteObject implements InterfaceClientControllerRMI, ReceiverClientController {

    private Match match;
    private String nickname;
    private FirstPage firstPage;

    public ReceiverClientControllerRMI(Match match, String nickname, FirstPage fp) throws RemoteException{
        this.match = match;
        this.nickname = nickname;
        this.firstPage = fp;
    }

    //here are implemented all the methods that the server can call remotely to the client

    public void ping() throws RemoteException{
        return;
    }

    public void updateConnectedPlayers(ArrayList<Player> connectedPlayers) throws RemoteException{
        match.setPlayers(connectedPlayers);
        for (int i=0;i<match.getPlayers().size();i++){
            System.out.println(match.getPlayers().get(i).getNickname());
        }
        Platform.runLater(() -> firstPage.refreshPlayersInLobby());// Update on JavaFX Application Threa
    }

    public PowerUp askForPowerUpAsAmmo() {
        //TODO per johnny, scegli se chiamare un popup
        return new PowerUp(Color.RED, PowerUpName.TELEPORTER);
    }

    public String getNickname() throws RemoteException{
        return this.nickname;
    }

    public void startGame(){
        //TODO per johnny questo è il metodo che fa cambiare la vista alla GUI in automatico per iniziare la partita
    }

}