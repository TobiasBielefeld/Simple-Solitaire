package de.tobiasbielefeld.solitaire.handler;

import android.os.Handler;
import android.os.Message;

import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.gameLogic;

/**
 * load the game data in a handler which waits a bit, so the initial card deal looks smoother
 */

public class LoadGameHandler extends Handler {

    GameManager gm;

    public LoadGameHandler(GameManager gm){
        this.gm = gm;
    }

    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        gameLogic.load();
        gm.hasLoaded = true;
    }
}
