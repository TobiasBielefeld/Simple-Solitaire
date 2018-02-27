package de.tobiasbielefeld.solitaire.handler;

import android.os.Handler;
import android.os.Message;

import de.tobiasbielefeld.solitaire.helper.Sounds;

import static de.tobiasbielefeld.solitaire.SharedData.animate;
import static de.tobiasbielefeld.solitaire.SharedData.currentGame;
import static de.tobiasbielefeld.solitaire.SharedData.handlerDealCards;
import static de.tobiasbielefeld.solitaire.SharedData.handlerTestAfterMove;
import static de.tobiasbielefeld.solitaire.SharedData.prefs;
import static de.tobiasbielefeld.solitaire.SharedData.sounds;


/**
 * Helper for re-dealing cards. cards are first moved back to the stack, then a new game is dealt.
 */

public class HandlerDealCards extends Handler {

    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (!animate.cardIsAnimating()) {
            prefs.setDealingCards(false);
            currentGame.dealNewGame();
            sounds.playSound(Sounds.names.DEAL_CARDS);
            handlerTestAfterMove.sendEmptyMessageDelayed(0,100);
        } else {
            handlerDealCards.sendEmptyMessageDelayed(0,100);
        }
    }
}
