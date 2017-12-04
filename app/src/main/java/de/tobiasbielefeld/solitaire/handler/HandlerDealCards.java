package de.tobiasbielefeld.solitaire.handler;

import android.os.Handler;
import android.os.Message;

import de.tobiasbielefeld.solitaire.helper.Sounds;

import static de.tobiasbielefeld.solitaire.SharedData.animate;
import static de.tobiasbielefeld.solitaire.SharedData.currentGame;
import static de.tobiasbielefeld.solitaire.SharedData.handlerDealCards;
import static de.tobiasbielefeld.solitaire.SharedData.handlerTestAfterMove;
import static de.tobiasbielefeld.solitaire.SharedData.sounds;


/**
 * Helper for undo movements: The undo is separated in steps: first undo moves with order 0, wait a bit,
 * then moves with order 1 and so on
 */

public class HandlerDealCards extends Handler {

    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (!animate.cardIsAnimating()) {
            currentGame.dealCards();
            sounds.playSound(Sounds.names.DEAL_CARDS);
            handlerTestAfterMove.sendEmptyMessageDelayed(0,100);
        } else {
            handlerDealCards.sendEmptyMessageDelayed(0,100);
        }
    }
}
