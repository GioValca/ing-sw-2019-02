package controller;

import exception.NotAllowedCallException;
import exception.NotAllowedMoveException;
import exception.NotAllowedTargetException;
import exception.WrongPowerUpException;
import model.Match;
import model.powerup.PowerUp;
import model.map.*;
import model.player.*;
import model.powerup.PowerUpName;

/*
    This Class is used to manage all the possibilities from the PowerUps
    in the basic implementation of PowerUpCard there is no logic

    here i want to give game logic to each card based on its name
 */

public class PowerUpController{
    private Match match;
    private MoveController moveController;

    public PowerUpController(Match m, MoveController moveCtrl){
        this. match = m;
        this.moveController = moveCtrl; //serve per teleporter e per newton
    }

    /*
        this method has the powerup in input.
        the result will be the update of the status of ammo in the player object
        and the update of the powerup card position (returning to deck)

        it can only be used when the player has to pay a cost (picking a weapon)
        //fatto lato johnny, invocare quel metodo. - me pias no
        //fornire metodo lo stesso, devo effettuarei i controlli! (lanciare eccezioni eventualmente)
    */
    public void usePowerUpAsAmmo(PowerUp powerUp) throws NotAllowedCallException{
        for(int i = 0; i < 3; i++) {
            if (match.getCurrentPlayer().getPowerUps()[i] != null) {
                if (match.getCurrentPlayer().getPowerUps()[i].equals(powerUp))
                    match.getCurrentPlayer().transformPowerUpToAmmo(powerUp);
                else
                    throw new NotAllowedCallException("Not a valid powerUp to be converted to ammo");
            }
        }

    }

    /*
        Use of teleporter powerup
    */
    public void useTeleporter(PowerUp teleporter, Square destination) throws NotAllowedMoveException, WrongPowerUpException {
        if(teleporter.getName().equals(PowerUpName.TELEPORTER)) {
                if (destination.isActive()) {
                    match.getCurrentPlayer().setPosition(destination);
                    match.getCurrentPlayer().removePowerUps(teleporter);
                    //aggiunta gestione della rimozione del powerup quando viene utilizzato
                } else throw new NotAllowedMoveException("Not valid destination square, not active!");
        } else throw new WrongPowerUpException("Not valid PowerUp, it's not a teleporter!");
    }

    /*
        Use of newton powerup
    */
    public void useNewton(PowerUp newton, Player affectedPlayer, Square destination) throws NotAllowedMoveException{
        if(newton.getName().equals(PowerUpName.NEWTON)) {
            for (Directions d : affectedPlayer.getPosition().getAllowedMoves()) {
                if (match.getMap().getAllowedSquaresInDirection(d, affectedPlayer.getPosition()).contains(destination)) { //controllo che lo spostamento avvenga in una sola direzione
                    try {
                        moveController.move(affectedPlayer, destination, 2); //effettuo spostamento con max distance 2 (da regolamento)
                        match.getCurrentPlayer().removePowerUps(newton);
                        //aggiunta gestione della rimozione del powerup quando viene utilizzato
                    } catch (NotAllowedMoveException e) {
                        e.printStackTrace();
                    }
                } else
                    throw new NotAllowedMoveException("Not allowed to move in more than one direction with Newton");
            }
            throw new IllegalArgumentException("Not a valid PowerUp, not in your hand");
        }else throw new IllegalArgumentException("Not valid PowerUp");
    }

    /*
        Use of tagback Grenade
        affectedPlayer: player che infligge il danno
        user: player che usa il powerUp
    */
    public void useTagbackGrenade(PowerUp tagbackGrenade, Player user, Player affectedPlayer) throws NotAllowedTargetException {
        if(tagbackGrenade.getName().equals(PowerUpName.TAGBACK_GRENADE)){
            //the visibility check is done when the affected player tries to hit the user
            affectedPlayer.getBoard().updateMarks(1, user.getId(), affectedPlayer.getId());
            user.removePowerUps(tagbackGrenade);
            user.setAskForTagBackGrenade(false);
        }
        else throw new IllegalArgumentException("Not valid PowerUp");
    }

    public void useTargetingScope(PowerUp targetingScope, Player affectedPlayer){
        //TODO implementare, sistemare signature del metodo
    }

    private boolean visibilityBetweenPlayers(Player player1, Player player2) {
        //this method returns true if player2 can be seen by player1
        if (match.getMap().getVisibileRooms(player1.getPosition()).contains(player2.getPosition().getColor())) {
            return true;
        } else {
            return false;
        }

    }
}
