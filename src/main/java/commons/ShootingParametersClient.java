package commons;

import model.ShootMode;
import model.map.Directions;
import model.weapons.WeaponName;

import java.io.Serializable;
import java.util.*;

public class ShootingParametersClient implements Serializable {

    private WeaponName name;
    private ArrayList<String> targetPlayers;
    private ArrayList<ShootMode> shootModes;
    private ArrayList<Integer> squaresCoordinates;
    private Directions direction;
    private boolean makeDamageBeforeMove;
    private ArrayList<Integer> adrenalineMoveIndexes;

    public ShootingParametersClient() {
        this.shootModes = new ArrayList<>();
        this.targetPlayers = new ArrayList<>();
        this.squaresCoordinates = new ArrayList<>();
        this.adrenalineMoveIndexes = new ArrayList<>();
    }


    public void setShootModes(ShootMode shootMode) {
        this.shootModes.add(shootMode);
    }

    public void setTargetPlayers(String nick) {
        this.targetPlayers.add(nick);
    }

    public void setSquaresCoordinates(int x, int y) {
        this.squaresCoordinates.add(x);
        this.squaresCoordinates.add(y);
    }

    public void setDirection(Directions direction) {
        this.direction = direction;
    }

    public void setMakeDamageBeforeMove(Boolean makeDamageBeforeMove) {
        if (makeDamageBeforeMove != null)
             this.makeDamageBeforeMove = (boolean) makeDamageBeforeMove;
    }

    public Directions getDirection() {
        return direction;
    }

    public List<ShootMode> getShootModes() {
        return shootModes;
    }

    public List<Integer> getSquaresCoordinates() {
        return squaresCoordinates;
    }

    public List<String> getTargetPlayers() {
        return targetPlayers;
    }

    public boolean getMakeDamageBeforeMove() {
        return makeDamageBeforeMove;
    }

    public WeaponName getName() {
        return name;
    }

    public void setName(WeaponName name) {
        this.name = name;
    }

    public void setAdrenalineMoveIndexes(int x, int y) {
        this.adrenalineMoveIndexes.add(x);
        this.adrenalineMoveIndexes.add(y);
    }

    public ArrayList<Integer> getAdrenalineMoveIndexes() {
        return adrenalineMoveIndexes;
    }
}
