package de.tobiasbielefeld.solitaire.classes;

import android.os.Handler;
import android.os.Message;

import static de.tobiasbielefeld.solitaire.SharedData.animate;
import static de.tobiasbielefeld.solitaire.SharedData.currentGame;
import static de.tobiasbielefeld.solitaire.SharedData.gameLogic;

/**
 * This handler just waits until all card animations are over, then executes a method.
 */

public class CustomHandler extends Handler {

    private static final int TIME_DELTA = 100;
    MessageCallBack messageCallBack;

    public CustomHandler(MessageCallBack callback){
        messageCallBack = callback;
    }

    public void sendDelayed(){
        sendEmptyMessageDelayed(0, TIME_DELTA);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (animate.cardIsAnimating() || messageCallBack.additionalStopCondition()){
            sendEmptyMessageDelayed(0,TIME_DELTA);
        } else {
            messageCallBack.sendMessage();
        }
    }

    public interface MessageCallBack {
        void sendMessage();

        boolean additionalStopCondition();
    }


}
