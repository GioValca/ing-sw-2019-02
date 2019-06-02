package client.remoteController;

import client.GUI.FirstPage;
import model.map.*;
import model.player.*;
import model.Match;
import model.powerup.*;

public abstract class RemoteController {

    abstract Match getMatch();

    public abstract Map getMap();

    public abstract void buildMap(int mapID) throws Exception;

    //metodi derivanti da classe moveController
    abstract void move(Player player, int iDestination, int jDestination, int maxDistanceAllowed);

    abstract boolean isAllowedMove(Square startingPoint, int iDestination, int jDestination, int maxDistance);

    abstract void moveOneSquare(String movement);

    abstract void moveOneSquare(String movement, Player player);

    //metodi da grab controller
    abstract void grabAmmoCard();

    abstract void grabWeapon(int indexOfWeapon);

    //metodi di powerUpController
    abstract void usePowerUpAsAmmo(int indexOfPowerUp);

    abstract void useTeleporter(PowerUp teleporter, Square destination);

    abstract void useNewton(PowerUp newton, Player affectedPlayer, Square destination);

    abstract void useTagbackGrenade(PowerUp tagbackGrenade, Player user, Player affectedPlayer);

    abstract void useTargetingScope(PowerUp targetingScope, Player affectedPlayer);

    public abstract String checkConnection(String IP);

    public abstract void addPlayer(String nickName);

    public abstract int connectedPlayers();

    public abstract PlayerStatusHandler getPlayerStatus(int idPlayer);

    public abstract boolean getMatchStatus();

    public abstract void setFirstPage(FirstPage firstPage);

}
