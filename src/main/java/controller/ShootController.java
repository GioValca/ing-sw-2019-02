package controller;

import model.Color;
import model.Match;
import exception.*;
import model.map.Directions;
import model.map.Square;
import model.player.Player;
import model.weapons.Effect;
import java.util.*;

public class ShootController extends ActionController {

	//attributes

	private Match matchTemp;
	private Match match;
	private MoveController moveController;


	//getter methods

	public ShootController(Match match, MoveController moveController) {
		this.match = match;
		this.moveController = moveController;
	}

	public Player getCurrPlayer() {
		return match.getCurrentPlayer();
	}

	public Match getMatch() {
		//implementation of the abstract method inheritated from the father
		return match;
	}


	//check methods

	private boolean visibilityBetweenPlayers(Player player1, Player player2) {
		//this method returns true if player2 can be seen by player1

		return match.getMap().getVisibileRooms(player1.getPosition()).contains(player2.getPosition().getColor());

	}

	public void payAmmo(List<Color> cost) throws NotEnoughAmmoException {
		//this method makes the player pay ammo for the optional effects

		if (cost.isEmpty())
			return;

		int r = 0;
		int b = 0;
		int y = 0;

		for (Color color : cost) {
			switch (color) {
				case RED:
					r++;
					break;
				case BLUE:
					b++;
					break;
				case YELLOW:
					y++;
					break;
				default:
					break;
			}
		}

		if (getCurrPlayer().getAmmo().getRedAmmo() - r < 0 || getCurrPlayer().getAmmo().getBlueAmmo() - b < 0 || getCurrPlayer().getAmmo().getYellowAmmo() - y < 0) {
			throw new NotEnoughAmmoException("It seems you don't have enough ammo");
		} else {
			getCurrPlayer().removeAmmo(r, b, y);
		}
	}

	private void checkCorrectVisibility(Effect eff, Player player1, Player player2) throws NotAllowedTargetException {
		//this method checks if the visibility required by the weapon is respected
		if (eff.getMoveTarget() != 0 || eff.getMoveYourself() != 0)
			return;
		if (eff.needVisibleTarget() != visibilityBetweenPlayers(player1, player2))
			throw new NotAllowedTargetException();
	}

	private void checkAllowedDistance(Effect eff, Player player1, Player player2) throws NotAllowedTargetException {
		//this method checks if the distance required by the weapon is respected
		if (moveController.minDistBetweenSquares(player1.getPosition(), player2.getPosition()) < eff.getMinShootDistance())
			throw new NotAllowedTargetException();

	}

	private void checkExactDistance(Effect eff, Player player1, Player player2) throws NotAllowedTargetException {
		//this method checks if the distance required by the weapon is the same that separate the two players, ONLY FOR DAMAGE EFFECT
		int k;

		if (eff.getDamage() != 0) {

			if (eff.getMinShootDistance() == -1) {
				k = 0;
			} else {
				k = eff.getMinShootDistance();
			}

			if (moveController.minDistBetweenSquares(player1.getPosition(), player2.getPosition()) != k)
				throw new NotAllowedTargetException();

		}
	}

	private void checkMaximumDistance(Effect eff, Player player, Square square, int maxDistance) throws NotAllowedMoveException {
		//this method checks if the distance between the player and the square is smaller then the set parameter
		if (eff.getDamage() != 0 || eff.getMark() != 0)
			return;
		if (moveController.minDistBetweenSquares(player.getPosition(), square) > maxDistance)
			throw new NotAllowedMoveException();

	}

	private void checkSameDirectionAllowed(Effect eff, Player player1, Square square, Directions direction) throws NotAllowedTargetException {
		//this method checks if the square and the position of the player are on the same line (walls cannot be passed)
		if(direction!=null) {
			if (match.getMap().getAllowedSquaresInDirection(direction, player1.getPosition()).contains(square)) {
				return;
			} else {
				throw new NotAllowedTargetException();
			}
		}

		ArrayList<Directions> cardinalDirections = new ArrayList<>();
		cardinalDirections.add(Directions.UP);
		cardinalDirections.add(Directions.DOWN);
		cardinalDirections.add(Directions.LEFT);
		cardinalDirections.add(Directions.RIGHT);

		for (Directions dir: cardinalDirections) {
			if (! match.getMap().getAllowedSquaresInDirection(dir, player1.getPosition()).contains(square))  //condition verified if the two squares are not on the same line
				throw new NotAllowedTargetException();
		}
	}

	private void checkSameDirectionThroughWalls(Effect eff, Player player1, Square square, Directions direction) throws NotAllowedTargetException {
		//this method checks if the square and the position of the player are on the same line (don't care about the walls)
		if(direction!=null) {
			if (match.getMap().getAllSquaresInDirection(direction, player1.getPosition()).contains(square)) {
				return;
			} else {
				throw new NotAllowedTargetException();
			}
		}

		ArrayList<Directions> cardinalDirections = new ArrayList<>();
		cardinalDirections.add(Directions.UP);
		cardinalDirections.add(Directions.DOWN);
		cardinalDirections.add(Directions.LEFT);
		cardinalDirections.add(Directions.RIGHT);

		for (Directions dir: cardinalDirections) {
			if (! match.getMap().getAllSquaresInDirection(dir, player1.getPosition()).contains(square))  //condition verified if the two squares are not on the same line
				throw new NotAllowedTargetException();
		}
	}

	private void checkDifferentRoom(Square s1, Square s2) throws NotAllowedTargetException {
		if (s1.getColor() == s2.getColor())
			throw new NotAllowedTargetException();
	}


	//shoot methods

	public void shootLockRifle (ShootingParametersInput input) throws NotAllowedTargetException, NotEnoughAmmoException, NotAllowedShootingModeException {
		//this method is valid only for LOCK RIFLE

		//the first cycle check if the effects can be applied
		for (ShootMode mode : input.getShootModes()) {
			for (Effect eff : input.getWeapon().getMode(mode)) {
				try {
					checkCorrectVisibility(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
					if (mode.equals(ShootMode.OPTIONAL1))
						payAmmo(input.getWeapon().getModeCost(mode));
				} catch (NotAllowedTargetException e) {
				throw new NotAllowedTargetException();
				} catch (NotEnoughAmmoException e) {
					throw new NotEnoughAmmoException("poverooo!");   //TODO
				}
			}
		}

		//execution cycle
		for (ShootMode mode : input.getShootModes()) {
			for (Effect eff : input.getWeapon().getMode(mode)) {
				try {
					eff.executeEffect(match, moveController, input);
				} catch (Exception e) {

				}
			}
		}
	}

	public void shootElectroScythe (ShootingParametersInput input) throws NotAllowedTargetException, NotEnoughAmmoException, NotAllowedShootingModeException {
		//this method is valid only for ELECTRO SCYTHE

		Effect eff;
		input.getTargets().clear();
		ShootMode mode = input.getShootModes().get(0);
		eff = input.getWeapon().getMode(mode).get(0);   //take the effect

		for (Player player: match.getPlayers()) {
			if(player.getId() != getCurrPlayer().getId()) {
				try {
					if (mode.equals(ShootMode.ALTERNATE))
						payAmmo(input.getWeapon().getModeCost(mode));
					checkCorrectVisibility(eff, getCurrPlayer(), player);
					checkExactDistance(eff, getCurrPlayer(), player);
					input.getTargets().add(player);
				} catch (NotAllowedTargetException e) {
					throw new NotAllowedTargetException();
				} catch (NotEnoughAmmoException e) {
					throw new NotEnoughAmmoException("poverooo!");   //TODO
				}
			}
		}

		try {
			eff.executeEffect(match, moveController, input);
		} catch (Exception e){
			throw new NotAllowedTargetException();
		}

	}

	public void shootMachineGun (ShootingParametersInput input) throws NotAllowedTargetException, NotEnoughAmmoException, NotAllowedShootingModeException {
		//this method is valid only for MACHINE GUN

		for (ShootMode mode : input.getShootModes()) {
			try {
				if (mode.equals(ShootMode.OPTIONAL1) || mode.equals(ShootMode.OPTIONAL2))
					payAmmo(input.getWeapon().getModeCost(mode));
			} catch (NotEnoughAmmoException e) {
				throw new NotEnoughAmmoException("poverooo!");   //TODO
			}
			for (Effect eff : input.getWeapon().getMode(mode)) {
				if (eff.getSameTarget()<input.getTargets().size()) {	//check if the user has set more than one target
					try {
						checkCorrectVisibility(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
					} catch (NotAllowedTargetException e) {
						throw new NotAllowedTargetException();
					}
				}
			}
		}

		for (ShootMode mode : input.getShootModes()) {
			for (Effect eff : input.getWeapon().getMode(mode)) {
				if (eff.getSameTarget()<input.getTargets().size()) {    //check if the user has set more than one target
					try {
						eff.executeEffect(match, moveController, input);
					} catch (Exception e) {

					}
				}
			}
		}

	}

	public void shootTHOR (ShootingParametersInput input) throws NotAllowedTargetException, NotEnoughAmmoException, NotAllowedShootingModeException {
		//this method is valid only for T.H.O.R.
		Effect eff;
		input.getTargets().add(0, getCurrPlayer());

		if (!input.getShootModes().get(0).equals(ShootMode.BASIC))
			throw new NotAllowedShootingModeException();

		if (input.getShootModes().size()==2 && !input.getShootModes().get(1).equals(ShootMode.OPTIONAL1))
			throw new NotAllowedShootingModeException();

		if (input.getShootModes().size()==3 && !input.getShootModes().get(2).equals(ShootMode.OPTIONAL2))
			throw new NotAllowedShootingModeException();

		for (ShootMode mode : input.getShootModes()) {
			eff = input.getWeapon().getMode(mode).get(0);
			try {
				checkCorrectVisibility(eff, input.getTargets().get(eff.getSameTarget()-1), input.getTargets().get(eff.getSameTarget()));
				if (mode.equals(ShootMode.OPTIONAL1) || mode.equals(ShootMode.OPTIONAL2))
					payAmmo(input.getWeapon().getModeCost(mode));
			} catch (NotAllowedTargetException e) {
				throw new NotAllowedTargetException();
			} catch (NotEnoughAmmoException e) {
				throw new NotEnoughAmmoException("poverooo!");   //TODO
			}
		}


		for (ShootMode mode : input.getShootModes()) {
			eff = input.getWeapon().getMode(mode).get(0);
			try {
				eff.executeEffect(match, moveController, input);
			} catch (Exception e) {

			}
		}

	}

	public void shootPlasmaGun (ShootingParametersInput input) throws NotAllowedTargetException, NotEnoughAmmoException, NotAllowedShootingModeException, NotAllowedMoveException {
		//this method is valid only for Plasma Gun
		Effect eff;
		Square squareTemp = getCurrPlayer().getPosition();

		if (input.getShootModes().get(0) == ShootMode.OPTIONAL1) {
			eff = input.getWeapon().getOptionalModeOne().get(0);
			checkMaximumDistance(eff, getCurrPlayer(), input.getSquares().get(0), eff.getMoveYourself());
			eff.executeEffect(match, moveController, input);
		}

		for (ShootMode mode : input.getShootModes()) {
			eff = input.getWeapon().getMode(mode).get(0);
			try {
				checkCorrectVisibility(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
				checkMaximumDistance(eff, getCurrPlayer(), input.getSquares().get(0), eff.getMoveYourself());
				if (mode.equals(ShootMode.OPTIONAL2))
					payAmmo(input.getWeapon().getModeCost(mode));
			} catch (NotAllowedTargetException e) {
				getCurrPlayer().setPosition(squareTemp);
				throw new NotAllowedTargetException();
			} catch (NotEnoughAmmoException e) {
				getCurrPlayer().setPosition(squareTemp);
				throw new NotEnoughAmmoException("poverooo!");   //TODO
			} catch (NotAllowedMoveException e) {
				getCurrPlayer().setPosition(squareTemp);
				throw new NotAllowedMoveException();
			}
		}

		for (ShootMode mode : input.getShootModes()) {
			eff = input.getWeapon().getMode(mode).get(0);
			try {
				eff.executeEffect(match, moveController, input);
			} catch (Exception e) {

			}
		}

		/*
		for (ShootMode mode : input.getShootModes()) {
			switch (mode){

				case BASIC:
					eff = input.getWeapon().getBasicMode().get(0);
					try {
						checkCorrectVisibility(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
						eff.executeEffect(match, moveController, input);
					} catch (Exception e) {
						throw new NotAllowedTargetException();
					}
					break;

				case OPTIONAL1:
					eff = input.getWeapon().getOptionalModeOne().get(0);
					try{
						eff.executeEffect(match, moveController, input);
					} catch (Exception e) {
						//TODO manage the NotAllowedMoveException, ask Edo
						System.out.println("not ok!");
					}
					break;

				case OPTIONAL2:
					eff = input.getWeapon().getOptionalModeTwo().get(0);
					try {
						payAmmo(input.getWeapon().getCostOpt2());
						try {
							checkCorrectVisibility(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
							eff.executeEffect(match, moveController, input);
						} catch (Exception e) {
							throw new NotAllowedTargetException();
						}
					} catch (Exception e) {
						//TODO write the catch part, prolly calling the view with a pop-up, maybe re-throw the exception
					}
					break;

				case ALTERNATE:
					throw new NotAllowedShootingModeException();
			}
		}
		 */

	}

	public void shootWhisper (ShootingParametersInput input) throws NotAllowedTargetException {
		//this method is valid only for Whisper

		try {
			for (Effect eff : input.getWeapon().getBasicMode()) {
				checkCorrectVisibility(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
				checkAllowedDistance(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
				eff.executeEffect(match, moveController, input);
			}
		} catch(Exception e) {
			throw new NotAllowedTargetException();
		}

	}

	public void shootTractorBeam (ShootingParametersInput input) throws NotAllowedTargetException, NotEnoughAmmoException, NotAllowedShootingModeException, NotAllowedMoveException{
		//this method is valid only for Tractor Beam
		ShootMode mode = input.getShootModes().get(0);
		Square squareTemp = input.getTargets().get(0).getPosition();

		if (mode.equals(ShootMode.ALTERNATE)) {
			try {
				payAmmo(input.getWeapon().getModeCost(mode));
			} catch (NotEnoughAmmoException e) {
				input.getTargets().get(0).setPosition(squareTemp);
				throw new NotEnoughAmmoException("poverooo!");   //TODO
			}
		}

		for (Effect eff : input.getWeapon().getMode(mode)) {
			try {
				checkCorrectVisibility(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
				checkMaximumDistance(eff, input.getTargets().get(0), input.getSquares().get(0), eff.getMoveTarget());
				if (mode.equals(ShootMode.ALTERNATE)) {
					checkMaximumDistance(eff, input.getTargets().get(0), input.getSquares().get(0), eff.getMoveTarget());
					checkExactDistance(eff, getCurrPlayer(), input.getTargets().get(0));
				}
				eff.executeEffect(match, moveController, input);
			} catch (NotAllowedTargetException e) {
				input.getTargets().get(0).setPosition(squareTemp);
				throw new NotAllowedTargetException();
			}  catch (NotAllowedMoveException e) {
				input.getTargets().get(0).setPosition(squareTemp);
				throw new NotAllowedMoveException();
			}
		}

		/*
		switch (input.getShootModes().get(0)) {

			case BASIC:
				try {
					for (Effect eff: input.getWeapon().getBasicMode()) {
						checkCorrectVisibility(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
						eff.executeEffect(match, moveController, input);
					}
				} catch (Exception e) {
					//TODO
				}
				break;

			case ALTERNATE:
				try {
					Effect eff;
					payAmmo(input.getWeapon().getCostAlternate());
					eff = input.getWeapon().getAlternateMode().get(0);
					eff.executeEffect(match, moveController, input);
					eff = input.getWeapon().getAlternateMode().get(1);
					checkExactDistance(eff, getCurrPlayer(), input.getTargets().get(0));
					checkCorrectVisibility(eff, getCurrPlayer(), input.getTargets().get(0));
					eff.executeEffect(match, moveController, input);
				} catch (Exception e) {
					//TODO
				}
				break;


			case OPTIONAL1:
				throw new NotAllowedShootingModeException();

			case OPTIONAL2:
				throw new NotAllowedShootingModeException();
		}

		 */
	}

	public void shootCannonVortex (ShootingParametersInput input) throws NotAllowedTargetException, NotEnoughAmmoException, NotAllowedMoveException {
		 //this method is valid only for Cannon Vortex

		for (ShootMode mode : input.getShootModes()) {
			if (mode.equals(ShootMode.OPTIONAL1)) {
				try {
					payAmmo(input.getWeapon().getModeCost(mode));
				} catch (NotEnoughAmmoException e) {
					throw new NotEnoughAmmoException("poverooo!");   //TODO
				}
			}
			for (Effect eff : input.getWeapon().getMode(mode)) {
				if (eff.getSameTarget()<input.getTargets().size()) {	//check if the user has set more than one target
					try {
						checkMaximumDistance(eff, input.getTargets().get(eff.getSameTarget()), input.getSquares().get(0), eff.getMoveTarget());
						checkCorrectVisibility(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
						checkAllowedDistance(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
					} catch (NotAllowedTargetException e) {
						throw new NotAllowedTargetException();
					} catch (NotAllowedMoveException e) {
						throw new NotAllowedMoveException();
					}
				}
			}
		}

		for (ShootMode mode : input.getShootModes()) {
			for (Effect eff : input.getWeapon().getMode(mode)) {
				try {
					eff.executeEffect(match, moveController, input);
				} catch (Exception e){

				}
			}
		}

		/*
		for (Effect eff : input.getWeapon().getBasicMode()) {
			try {
				checkMaximumDistance(eff, input.getTargets().get(0), input.getSquares().get(0), eff.getMoveTarget());
				checkCorrectVisibility(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
				checkAllowedDistance(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
				eff.executeEffect(match, moveController, input);
			} catch (Exception e) {
				//TODO
			}
		}

		if (input.getShootModes().size() > 1 && input.getTargets().size() > 1) {
			for (Effect eff : input.getWeapon().getOptionalModeOne()) {
				try {
					checkMaximumDistance(eff, input.getTargets().get(eff.getSameTarget()), input.getSquares().get(0), eff.getMoveTarget());
					checkCorrectVisibility(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
					checkAllowedDistance(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
					eff.executeEffect(match, moveController, input);
				} catch (Exception e) {

					// System.out.println("eccezione");
					break;

					//TODO REMEMBER TO ADD EVERYWHERE BREAK INSTRUCTION WHEN YOU CATCH AN EXCEPTION

				}
			}
		}
		 */

	}

	public void shootFurnace (ShootingParametersInput input) throws NotAllowedTargetException, NotAllowedMoveException {
		//this method is valid only for Furnace

		ShootMode mode = input.getShootModes().get(0);
		Effect eff;
		input.getTargets().clear();



		switch (mode) {

			case BASIC:
				for (Player player : match.getPlayers()) { 				//here the correct targets are set
					if (player.getId() != getCurrPlayer().getId() && player.getPosition().getColor() == input.getSquares().get(0).getColor()) {
						input.getTargets().add(player);
					}
				}

				try {
					checkDifferentRoom(getCurrPlayer().getPosition(), input.getSquares().get(0));
				} catch (NotAllowedTargetException e) {
					throw new NotAllowedTargetException();
				}

				eff = input.getWeapon().getMode(mode).get(0);

				for (Player player : input.getTargets()) {
					checkCorrectVisibility(eff, getCurrPlayer(), player);
				}

				eff.executeEffect(match, moveController, input);

				break;

			case ALTERNATE:
				for (Player player : match.getPlayers()) { 				//here the correct targets are set
					if (player.getId() != getCurrPlayer().getId() && player.getPosition() == input.getSquares().get(0)) {
						input.getTargets().add(player);
					}
				}

				for (Player player : input.getTargets()) {
					for (Effect effect: input.getWeapon().getMode(mode)) {
						try {
							checkExactDistance(effect, getCurrPlayer(), player);
							checkCorrectVisibility(effect, getCurrPlayer(), player);
						} catch (NotAllowedTargetException e){
							throw new NotAllowedTargetException();
						}
					}
				}

				for (Effect effect : input.getWeapon().getMode(mode)) {
					try {
						effect.executeEffect(match, moveController, input);
					} catch (Exception e) {

					}
				}

				break;

		}
	}

	public void shootHeatseeker (ShootingParametersInput input) throws NotAllowedTargetException {
		//this method is valid only for Heatseeker

		try {
			Effect eff = input.getWeapon().getBasicMode().get(0);
			checkCorrectVisibility(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
			eff.executeEffect(match, moveController, input);
		} catch(Exception e) {
			throw new NotAllowedTargetException();
		}

	}

	public void shootHellion (ShootingParametersInput input) throws NotAllowedTargetException, NotEnoughAmmoException {
		//this method in valid only for Hellion
		ShootMode mode = input.getShootModes().get(0);

		if (mode.equals(ShootMode.OPTIONAL1)) {
			try {
				payAmmo(input.getWeapon().getModeCost(mode));
			} catch (NotEnoughAmmoException e) {
				throw new NotEnoughAmmoException("poverooo!");   //TODO
			}
		}

		for (Player player : match.getPlayers()) {  		//add the players in the same square to target list
			if (input.getTargets().get(0).getPosition() == player.getPosition() && player.getId() != getCurrPlayer().getId() && player.getId() != input.getTargets().get(0).getId())
				input.getTargets().add(player);
		}

		for (Effect eff : input.getWeapon().getMode(mode)) {
			for (Player player : input.getTargets()) {
				try {
					checkCorrectVisibility(eff, getCurrPlayer(), player);
					checkAllowedDistance(eff, getCurrPlayer(), player);
				} catch (NotAllowedTargetException e) {
					throw new NotAllowedTargetException();
				}
			}
		}

		for (Effect eff : input.getWeapon().getMode(mode)) {
			try {
				eff.executeEffect(match, moveController, input);
			} catch (NotAllowedMoveException e) {
				e.printStackTrace();
			}
		}

	}

	public void shootFlameThrower (ShootingParametersInput input) throws NotAllowedTargetException, NotEnoughAmmoException {
		//this method is valid only for FlameThrowerù

		ShootMode mode = input.getShootModes().get(0);
		input.getSquares().clear();

		//with the next two if I set the squares in which the user want to make damages
		if (match.getMap().getAllowedSquaresInDirection(input.getDirection(), getCurrPlayer().getPosition()).size()>1) {
			Square sq1 = match.getMap().getAllowedSquaresInDirection(input.getDirection(), getCurrPlayer().getPosition()).get(1);
			input.setSquares(sq1);
		}
		if (match.getMap().getAllowedSquaresInDirection(input.getDirection(), getCurrPlayer().getPosition()).size()>2) {
			Square sq2 = match.getMap().getAllowedSquaresInDirection(input.getDirection(), getCurrPlayer().getPosition()).get(2);
			input.setSquares(sq2);
		}


		switch (mode) {

			case BASIC:
				for (Effect eff : input.getWeapon().getMode(mode)) {
					checkExactDistance(eff, getCurrPlayer(), input.getTargets().get(eff.getSameTarget()));
					if (!input.getSquares().contains(input.getTargets().get(eff.getSameTarget()).getPosition()))
						throw new NotAllowedTargetException();
				}
				for (Effect eff : input.getWeapon().getMode(mode)) {
					try {
						eff.executeEffect(match, moveController, input);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;

			case ALTERNATE:
				try {
					payAmmo(input.getWeapon().getModeCost(mode));
				} catch (NotEnoughAmmoException e) {
					throw new NotEnoughAmmoException("poverooo");  //TODO
				}

				//checks
				input.getTargets().clear();  //clear all the targets in order to rebuild the correct target list (people in first square)
				for (Player player : match.getPlayers()) {
					if (input.getSquares().get(0).equals(player.getPosition()))
						input.setTargets(player);
				}
				input.getTargets().clear();  //clear all the targets in order to rebuild the correct target list (people in second square)
				for (Player player : match.getPlayers()) {
					if (input.getSquares().get(1).equals(player.getPosition()))
						input.setTargets(player);
				}

				//execution code
				input.getTargets().clear();
				for (Player player : match.getPlayers()) {
					if (input.getSquares().get(0).equals(player.getPosition()))
						input.setTargets(player);
				}
				try {
					input.getWeapon().getMode(mode).get(0).executeEffect(match, moveController, input);
				} catch (Exception e) {
					e.printStackTrace();
				}

				input.getTargets().clear();
				for (Player player : match.getPlayers()) {
					if (input.getSquares().get(1).equals(player.getPosition()))
						input.setTargets(player);
				}
				try {
					input.getWeapon().getMode(mode).get(0).executeEffect(match, moveController, input);
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
		}

	}

}
