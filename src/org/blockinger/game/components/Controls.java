/*
 * Copyright 2013 Simon Willeke
 * contact: hamstercount@hotmail.com
 */

/*
    This file is part of Blockinger.

    Blockinger is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Blockinger is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Blockinger.  If not, see <http://www.gnu.org/licenses/>.

    Diese Datei ist Teil von Blockinger.

    Blockinger ist Freie Software: Sie k�nnen es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Option) jeder sp�teren
    ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.

    Blockinger wird in der Hoffnung, dass es n�tzlich sein wird, aber
    OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
    Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License f�r weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */

package org.blockinger.game.components;

import org.blockinger.game.R;
import org.blockinger.game.activities.GameActivity;
import org.blockinger.game.pieces.*;


import android.content.Context;
import android.os.Vibrator;
import android.preference.PreferenceManager;

public class Controls extends Component {

	// Constants
	
	// Stuff
	private Board board;
	//private boolean initialized;
	private Vibrator v;
	private int vibrationOffset;
	private long shortVibeTime;
	private int[] lineThresholds;
	
	// Player Controls
	private boolean playerSoftDrop;
	private boolean clearPlayerSoftDrop;
	private boolean playerHardDrop;
	private boolean leftMove;
	private boolean rightMove;
	private boolean continuousSoftDrop;
	private boolean continuousLeftMove;
	private boolean continuousRightMove;
	private boolean clearLeftMove;
	private boolean clearRightMove;
	private boolean leftRotation;
	private boolean rightRotation;
	private boolean buttonVibrationEnabled;
	private boolean eventVibrationEnabled;
	
	public Controls(GameActivity ga) {
		super(ga);
		
		lineThresholds = host.getResources().getIntArray(R.array.line_thresholds);
		shortVibeTime = 0;
		
		v = (Vibrator) host.getSystemService(Context.VIBRATOR_SERVICE);
		
		buttonVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_vibration_button", false);
		eventVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(host).getBoolean("pref_vibration_events", false);
		try {
			vibrationOffset = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(host).getString("pref_vibDurOffset", "0"));
		} catch(NumberFormatException e) {
			vibrationOffset = 0;
		}
		playerSoftDrop = false;
		leftMove = false;
		rightMove = false;
		leftRotation = false;
		rightRotation = false;
		clearLeftMove = false;
		clearRightMove = false;
		clearPlayerSoftDrop = false;
		continuousSoftDrop = false;
		continuousLeftMove = false;
		continuousRightMove = false;
	}
	
	public void vibrateWall() {
		if (!eventVibrationEnabled || v == null)
			return;
		v.vibrate(host.game.getMoveInterval() + vibrationOffset);
	}
	
	public void cancelVibration() {
		if ((!eventVibrationEnabled && !buttonVibrationEnabled) || v == null)
			return;
		v.cancel();
	}
	
	public void vibrateBottom() {
		if (!eventVibrationEnabled || v == null)
			return;
		v.cancel();
		v.vibrate(new long[] {0, 5 + vibrationOffset, 30 + vibrationOffset, 20 + vibrationOffset}, -1);
	}
	
	public void vibrateShort() {
		if (!buttonVibrationEnabled || v == null)
			return;
		//v.cancel();
		if((host.game.getTime() - shortVibeTime) > (host.getResources().getInteger(R.integer.shortVibeInterval) + vibrationOffset)) {
			shortVibeTime = host.game.getTime();
			v.vibrate(5 + vibrationOffset);
		}
	}


	public void rotateLeftPressed() {
		leftRotation = true;
		host.game.action();
		vibrateShort();
    	//Thread.yield();
	}

	public void rotateLeftReleased() {
    	//Thread.yield();
	}

	public void rotateRightPressed() {
		rightRotation = true;
		host.game.action();
		vibrateShort();
	    //Thread.yield();
	}

	public void rotateRightReleased() {
    	//Thread.yield();
	}

	public void downButtonReleased() {
		clearPlayerSoftDrop = true;
		vibrateShort();
	    //Thread.yield();
	}

	public void downButtonPressed() {
		host.game.action();
		playerSoftDrop = true;
		clearPlayerSoftDrop = false;
		vibrateShort();
		host.game.setNextPlayerDropTime(host.game.getTime());
		//nextPlayerDropTime = host.game.getTime();
    	//Thread.yield();
	}

	public void dropButtonReleased() {
    	//Thread.yield();
	}

	public void dropButtonPressed() {
		if(!host.game.getActivePiece().isActive())
			return;
		host.game.action();
		playerHardDrop = true;
		if(buttonVibrationEnabled & !eventVibrationEnabled)
			vibrateShort();
	}

	public void leftButtonReleased() {
		clearLeftMove = true;
		cancelVibration();
    	//Thread.yield();
	}

	public void leftButtonPressed() {
		host.game.action();
		clearLeftMove = false;
		leftMove = true;
		rightMove = false;
		host.game.setNextPlayerMoveTime(host.game.getTime());
		//nextPlayerMoveTime = host.game.getTime();
		//vibrateShort(); wird schon unten gemacht (weil in jedem tick)
		
    	//Thread.yield();
	}

	public void rightButtonReleased() {
		clearRightMove = true;
		cancelVibration();
    	//Thread.yield();
	}

	public void rightButtonPressed() {
		host.game.action();
		clearRightMove = false;
		rightMove = true;
		leftMove = false;
		host.game.setNextPlayerMoveTime(host.game.getTime());
		//nextPlayerMoveTime = host.game.getTime();
		//vibrateShort(); wird schon unten gemacht (weil in jedem tick)
		
    	//Thread.yield();
	}

	public void cycle(long tempTime) {
		long gameTime = host.game.getTime();
		Piece active = host.game.getActivePiece();
		Board board = host.game.getBoard();
		int maxLevel = host.game.getMaxLevel();
		
		if(playerHardDrop) {
			board.interruptClearAnimation();
			int hardDropDistance = active.hardDrop(false, board);
			vibrateBottom();
			host.game.clearLines(true, hardDropDistance);
			host.game.pieceTransition(eventVibrationEnabled);
			board.invalidate();
			playerHardDrop = false;
			
			if((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(),maxLevel - 1)]))
				host.game.nextLevel();
			host.game.setNextDropTime(gameTime + host.game.getAutoDropInterval());
			host.game.setNextPlayerDropTime(gameTime);
			//nextDropTime = gameTime + host.game.getAutoDropInterval();
			//nextPlayerDropTime = gameTime;

		// Initial Soft Drop
		} else if(playerSoftDrop) {
			playerSoftDrop = false;
			continuousSoftDrop = true;
			if(!active.drop(board)) {
				// piece finished
				vibrateBottom();
				host.game.clearLines(false, 0);
				host.game.pieceTransition(eventVibrationEnabled);
				board.invalidate();
			} else {
				//vibrateShort(); DAS WUERDE HIER BEI JEDEM TICK VIBRIEREN UND DAS MUSS DOCH NICHT SEIN. BEIM BESTEN WILLEN NICHT MEIN LIEBER FREUND.
			}
			if((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(),maxLevel - 1)]))
				host.game.nextLevel();
			host.game.setNextDropTime(host.game.getNextPlayerDropTime() + host.game.getAutoDropInterval());
			host.game.setNextPlayerDropTime(host.game.getNextPlayerDropTime() + 2*host.game.getSoftDropInterval());
			//nextDropTime = nextPlayerDropTime + host.game.getAutoDropInterval();
			//nextPlayerDropTime = nextPlayerDropTime + 2*host.game.getSoftDropInterval(); // initial interval is doubled!
			
		// Continuous Soft Drop
		} else if(continuousSoftDrop) {
			if(gameTime >= host.game.getNextPlayerDropTime()) {
				if(!active.drop(board)) {
					// piece finished
					vibrateBottom();
					host.game.clearLines(false, 0);
					host.game.pieceTransition(eventVibrationEnabled);
					board.invalidate();
				} else {
					//vibrateShort(); DAS WUERDE HIER BEI JEDEM TICK VIBRIEREN UND DAS MUSS DOCH NICHT SEIN. BEIM BESTEN WILLEN NICHT MEIN LIEBER FREUND.
				}
				if((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(),maxLevel - 1)]))
					host.game.nextLevel();
				host.game.setNextDropTime(host.game.getNextPlayerDropTime() + host.game.getAutoDropInterval());
				host.game.setNextPlayerDropTime(host.game.getNextPlayerDropTime() + host.game.getSoftDropInterval());
				//nextDropTime = nextPlayerDropTime + host.game.getAutoDropInterval();
				//nextPlayerDropTime = nextPlayerDropTime + host.game.getSoftDropInterval();
				
			// Autodrop if faster than playerDrop
			} else if(gameTime >= host.game.getNextDropTime()) {
				if(!active.drop(board)) {
					// piece finished
					vibrateBottom();
					host.game.clearLines(false, 0);
					host.game.pieceTransition(eventVibrationEnabled);
					board.invalidate();
				}
				if((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(),maxLevel - 1)]))
					host.game.nextLevel();
				host.game.setNextDropTime(host.game.getNextDropTime() + host.game.getAutoDropInterval());
				host.game.setNextPlayerDropTime(host.game.getNextDropTime() + host.game.getSoftDropInterval());
				//nextDropTime = nextDropTime + host.game.getAutoDropInterval();
				//nextPlayerDropTime = nextDropTime + host.game.getSoftDropInterval();
			}

			if(clearPlayerSoftDrop) {
				continuousSoftDrop = false;
				clearPlayerSoftDrop = false;
			}
			
		// Autodrop if no playerDrop
		} else if(gameTime >= host.game.getNextDropTime()) {
			if(!active.drop(board)) {
				// piece finished
				vibrateBottom();
				host.game.clearLines(false, 0);
				host.game.pieceTransition(eventVibrationEnabled);
				board.invalidate();
			}
			if((host.game.getLevel() < maxLevel) && (host.game.getClearedLines() > lineThresholds[Math.min(host.game.getLevel(),maxLevel - 1)]))
				host.game.nextLevel();
			host.game.setNextDropTime(host.game.getNextDropTime() + host.game.getAutoDropInterval());
			host.game.setNextPlayerDropTime(host.game.getNextDropTime());
			//nextDropTime = nextDropTime + host.game.getAutoDropInterval();
			//nextPlayerDropTime = nextDropTime;
			
		} else
			host.game.setNextPlayerDropTime(gameTime);
			//nextPlayerDropTime = gameTime;

		
		// Reset Move Time
		if((!leftMove && !rightMove) && (!continuousLeftMove && !continuousRightMove))
			host.game.setNextPlayerMoveTime(gameTime);
			//nextPlayerMoveTime = gameTime;
		
		// Left Move
		if(leftMove) {
			continuousLeftMove = true;
			leftMove = false;
			if(active.moveLeft(board)) {
				vibrateShort(); // ES SOLL BEI JEDEM TICK VIBRIEREN
				host.display.invalidatePhantom();
			} else
				vibrateWall();
			host.game.setNextPlayerMoveTime(host.game.getNextPlayerMoveTime() + 2*host.game.getMoveInterval());
			//nextPlayerMoveTime = nextPlayerMoveTime + 2*host.game.getMoveInterval(); // first interval is doubled!
			
		} else if(continuousLeftMove) {
			if(gameTime >= host.game.getNextPlayerMoveTime()) {
				if(active.moveLeft(board)) {
					vibrateShort(); // ES SOLL BEI JEDEM TICK VIBRIEREN
					host.display.invalidatePhantom();
				} else
					vibrateWall();
				host.game.setNextPlayerMoveTime(host.game.getNextPlayerMoveTime() + host.game.getMoveInterval());
			}

			if(clearLeftMove) {
				continuousLeftMove = false;
				clearLeftMove = false;
			}
		}
		
		// Right Move
		if(rightMove) {
			continuousRightMove = true;
			rightMove = false;
			if(active.moveRight(board)) {
				vibrateShort(); // ES SOLL BEI JEDEM TICK VIBRIEREN
				host.display.invalidatePhantom();
			} else
				vibrateWall();
			host.game.setNextPlayerMoveTime(host.game.getNextPlayerMoveTime() + 2*host.game.getMoveInterval()); // first interval is doubled!
			
		} else if(continuousRightMove) {
			if(gameTime >= host.game.getNextPlayerMoveTime()) {
				if(active.moveRight(board)) {
					vibrateShort(); // ES SOLL BEI JEDEM TICK VIBRIEREN
					host.display.invalidatePhantom();
				} else
					vibrateWall();
				host.game.setNextPlayerMoveTime(host.game.getNextPlayerMoveTime() + host.game.getMoveInterval());
			}

			if(clearRightMove) {
				continuousRightMove = false;
				clearRightMove = false;
			}
		}
		
		// Left Rotation
		if(leftRotation) {
			leftRotation = false;
			active.turnLeft(board);
			host.display.invalidatePhantom();
		}
		
		// Right Rotation
		if(rightRotation) {
			rightRotation = false;
			active.turnRight(board);
			host.display.invalidatePhantom();
		}
	}

	public void setBoard(Board instance2) {
		this.board = instance2;
	}

	public Board getBoard() {
		return this.board;
	}
	
	/**
	 * unused!
	 */
	@Override
	public void reconnect(GameActivity cont) {
		super.reconnect(cont);
		v = (Vibrator) cont.getSystemService(Context.VIBRATOR_SERVICE);
		
		buttonVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(cont).getBoolean("pref_vibration_button", false);
		eventVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(cont).getBoolean("pref_vibration_events", false);
		try {
			vibrationOffset = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(cont).getString("pref_vibDurOffset", "0"));
		} catch(NumberFormatException e) {
			vibrationOffset = 0;
		}
	}

	/**
	 * unused!
	 */
	@Override
	public void disconnect() {
		super.disconnect();
		v = null;
	}
	
}