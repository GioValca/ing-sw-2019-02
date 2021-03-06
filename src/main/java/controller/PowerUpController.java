package controller;

import exception.*;
import model.Color;
import model.Match;
import model.powerup.PowerUp;
import model.map.*;
import model.player.*;
import model.powerup.PowerUpName;

/**
 * This Class is used to manage all the possibilities from the PowerUps
 * in the basic implementation of PowerUpCard there is no logic
 *
 * here i want to give game logic to each card
 */
public class PowerUpController{
    private Match match;
    private MoveController moveController;

    /**
     * This constructor wants in input the match to work with and the an instance of a moveController
     * @param m match
     * @param moveCtrl move controller
     */
    public PowerUpController(Match m, MoveController moveCtrl){
        this. match = m;
        this.moveController = moveCtrl; //serve per teleporter e per newton
    }

    /**
     * this method has the powerup in input.
     * the result will be the update of the status of ammo in the player object
     * and the update of the powerup card position (returning to deck)
     *
     * it can only be used when the player has to pay a cost (picking a weapon)
     * @param powerUp powerUp to use as ammo
     */
    public void usePowerUpAsAmmo(PowerUp powerUp) {
        for(int i = 0; i < 3; i++) {
            if (match.getCurrentPlayer().getPowerUps()[i] != null && match.getCurrentPlayer().getPowerUps()[i].equals(powerUp))
                match.getCurrentPlayer().transformPowerUpToAmmo(powerUp);
        }

    }

    /*

    */

    /**
     * Use of teleporter powerup
     * @param teleporter powerUp to use (to be brasated from players hand)
     * @param destination destination of teleporting
     * @throws NotAllowedMoveException never
     * @throws WrongPowerUpException if your're trying to mess with the controller he wont let you use a random powerUp in exchange of a free move
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

    /**
     * Use of newton powerup
     * @param newton powerUp to use (to be brasated from players hand)
     * @param affectedPlayer player to move
     * @param destination destination of newtoning (drag the affected player in this positionz)
     * @throws NotAllowedMoveException if you try to move the player in more than one direction or in more than 2 of distance
     * @throws NotInYourPossessException you cannot use a powerUp that you don't have in your hand
     * @throws WrongPowerUpException if your're trying to mess with the controller he wont let you use a random powerUp in exchange of a free move
     */
    public void useNewton(PowerUp newton, Player affectedPlayer, Square destination) throws NotAllowedMoveException, NotInYourPossessException, WrongPowerUpException {
        if(newton.getName().equals(PowerUpName.NEWTON)) { //control that the powerup is correct
            if (checkSingleDirectionMove(affectedPlayer, destination)) { //controllo che lo spostamento avvenga in una sola direzione
                try {
                    moveController.move(affectedPlayer, destination, 2); //effettuo spostamento con max distance 2 (da regolamento)
                    match.getCurrentPlayer().removePowerUps(newton);
                    return;
                    //aggiunta gestione della rimozione del powerup quando viene utilizzato
                } catch (NotAllowedMoveException e){
                    e.printStackTrace();
                }
            } else throw new NotAllowedMoveException("Not allowed to move in more than one direction with Newton");
        } else throw new WrongPowerUpException("Not valid PowerUp, you want to use another powerUp as a Newton");
    }

    public boolean checkSingleDirectionMove(Player affectedPlayer, Square destination){
        for(Directions d: affectedPlayer.getPosition().getAllowedMoves()){
            if(match.getMap().getAllowedSquaresInDirection(d, affectedPlayer.getPosition()).contains(destination)){
                return true;
            }
        }
        return false;
    }

    /**
     * Use of tagback Grenade
     * @param tagbackGrenade powerUp to use as tagback, kidding, it has to be a tagback!
     * @param user who makes the damage
     * @param affectedPlayer player who gets damage
     * @throws NotInYourPossessException if you are trying to use another thing as a tagback
     */
    public void useTagbackGrenade(PowerUp tagbackGrenade, Player user, Player affectedPlayer) throws NotInYourPossessException {
        if(tagbackGrenade.getName().equals(PowerUpName.TAGBACK_GRENADE)){
            //the visibility check is done when the affected player tries to hit the user
            affectedPlayer.getBoard().updateMarks(1, user.getId(), affectedPlayer.getId());
            user.removePowerUps(tagbackGrenade);
            user.setAskForTagBackGrenade(false);
        } else throw new NotInYourPossessException("Not valid PowerUp");

    }

    /**
     * use of the targeting scope
     * @param targetingScope powerUp to use
     * @param ammoColor color of ammo to pay
     * @param affectedPlayer player affected by powerUp's effect (one more damage)
     * @throws NotInYourPossessException if you are trying to use another thing as a targeting scope
     * @throws NotEnoughAmmoException if you cannot pay for the PowerUp
     */
    public void useTargetingScope(PowerUp targetingScope, Color ammoColor, Player affectedPlayer) throws NotInYourPossessException, NotEnoughAmmoException {
        if(targetingScope.getName().equals(PowerUpName.TARGETING_SCOPE)){
            if ((match.getCurrentPlayer().getAmmo().getSpecificAmmo(ammoColor) - 1) >= 0) {
                match.getCurrentPlayer().removeSingleAmmo(ammoColor);
                affectedPlayer.getBoard().updateLife(1, match.getCurrentPlayer().getId(), affectedPlayer.getId());

                if (affectedPlayer.getBoard().isDead())                                                            //check if the target is dead
                    affectedPlayer.trueDead();

                if(!affectedPlayer.getStatus().getSpecialAbility().equals(AbilityStatus.FRENZY) && !affectedPlayer.getStatus().getSpecialAbility().equals(AbilityStatus.FRENZY_LOWER)) {

                    if (affectedPlayer.getBoard().getTotalNumberOfDamages() > 2)
                        affectedPlayer.getStatus().setSpecialAbilityAdrenalinePick();

                    if (affectedPlayer.getBoard().getTotalNumberOfDamages() > 5)
                        affectedPlayer.getStatus().setSpecialAbilityAdrenalineShoot();

                }

                match.getCurrentPlayer().removePowerUps(targetingScope);
            } else throw new NotEnoughAmmoException("You don't have enough ammo to use Targeting Scope");
        } else throw new NotInYourPossessException("Not valid PowerUp");

    }

}
