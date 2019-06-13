package client.clientController;

//this is the remote object that the client shares with the server in order to help the server push information whenever needed
//info such as Model updates, turn status or other stuff.

import client.GUI.ChooseMap;
import client.GUI.FirstPage;
import client.GUI.MainPage;
import client.GUI.RespawnPopUp;
import client.remoteController.SenderClientRemoteController;
import commons.InterfaceClientControllerRMI;
import javafx.application.Platform;
import javafx.stage.Stage;
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
    private ChooseMap chooseMap = null;
    private RespawnPopUp respawnPopUp;
    private MainPage mainPage;
    private SenderClientRemoteController senderRemoteController;

    public ReceiverClientControllerRMI(Match match, String nickname, FirstPage fp, SenderClientRemoteController senderClientRemoteController) throws RemoteException{
        this.match = match;
        this.nickname = nickname;
        this.firstPage = fp;
        this.senderRemoteController = senderClientRemoteController;
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
        System.out.println("[SERVER]: Starting a new game");
        mainPage = new MainPage();
        mainPage.setMatch(match);
        mainPage.setRemoteController(senderRemoteController);

        Platform.runLater( () -> {
                    try {
                        firstPage.closePrimaryStage();
                        if(chooseMap != null)
                            chooseMap.closePrimaryStage();
                        mainPage.start(new Stage());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public void updateMatch(Match match){
        setMatch(match);
        if(mainPage != null) {
            Platform.runLater( () -> {
                mainPage.setMatch(match);
                mainPage.refreshPlayersPosition();
                //TODO QUI CHIAMARE METODI DELLA GUI CHE AGGIORNA LA VISTA!
            });
        }
        //questo metodo viene chiamato piu volte.
    }

    private void setMatch(Match m){
        this.match = m;
    }

    public void askMap() throws Exception{
        // Platform.runLater( () -> firstPage.closePrimaryStage());
        chooseMap = new ChooseMap();
        chooseMap.setRemoteController(senderRemoteController);
        Platform.runLater(
                () -> {
                    // Update UI here.
                    try {
                        //todo qui va chiusa la finestra con la lobby (come si fa @johnny)
                        chooseMap.start(new Stage());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
        );

    }

    public void askSpawn() throws RemoteException{
        respawnPopUp = new RespawnPopUp();
        respawnPopUp.setSenderRemoteController(senderRemoteController);
        respawnPopUp.setMatch(match);

        Platform.runLater(
                ()-> {
                    try{
                        respawnPopUp.start(new Stage());
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
        );
    }

    public void waitForMap(){

    }

}
