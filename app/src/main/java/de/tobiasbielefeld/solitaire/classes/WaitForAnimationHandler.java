package de.tobiasbielefeld.solitaire.classes;

import android.os.Handler;
import android.os.Message;

import de.tobiasbielefeld.solitaire.ui.GameManager;

import static de.tobiasbielefeld.solitaire.SharedData.animate;
import static de.tobiasbielefeld.solitaire.SharedData.stopUiUpdates;

/**
 * This handler just waits until all card animations are over, then executes a method.
 */

public class WaitForAnimationHandler {

    private static final int TIME_DELTA = 100;
    private MessageCallBack messageCallBack;
    private GameManager gm;

    private CustomHandler handler;

    public WaitForAnimationHandler(GameManager gm, MessageCallBack callback){
        this.gm = gm;
        handler = new CustomHandler(this);
        messageCallBack = callback;
    }

    public void sendDelayed() {
        if (!stopUiUpdates) {
            handler.sendEmptyMessageDelayed(0, TIME_DELTA);
        }
    }

    public void sendNow(){
        if (!stopUiUpdates) {
            handler.sendEmptyMessage(0);
        }
    }

    public void forceSendNow(){
        handler.sendEmptyMessage(0);
    }

    private static class CustomHandler extends Handler {
        WaitForAnimationHandler base;

        CustomHandler(WaitForAnimationHandler base){
            this.base = base;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (animate.cardIsAnimating() || base.gm.isActivityPaused() || base.messageCallBack.additionalHaltCondition()){
                sendEmptyMessageDelayed(0,TIME_DELTA);
            } else {
                base.messageCallBack.doAfterAnimation();
            }
        }
    }

    public interface MessageCallBack {
        void doAfterAnimation();

        boolean additionalHaltCondition();
    }
}
