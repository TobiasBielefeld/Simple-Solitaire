package de.tobiasbielefeld.solitairelite.handler;

import android.os.Handler;
import android.os.Message;


import static de.tobiasbielefeld.solitairelite.SharedData.animate;
import static de.tobiasbielefeld.solitairelite.SharedData.currentGame;
import static de.tobiasbielefeld.solitairelite.SharedData.handlerDealCards;
import static de.tobiasbielefeld.solitairelite.SharedData.handlerTestAfterMove;
import static de.tobiasbielefeld.solitairelite.SharedData.prefs;


/**
 * Helper for re-dealing cards. cards are first moved back to the stack, then a new game is dealt.
 */

public class HandlerDealCards extends Handler {

    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (!animate.cardIsAnimating()) {
            prefs.setDealingCards(false);
            currentGame.dealNewGame();
            handlerTestAfterMove.sendEmptyMessageDelayed(0,100);
        } else {
            handlerDealCards.sendEmptyMessageDelayed(0,100);
        }
    }
}
