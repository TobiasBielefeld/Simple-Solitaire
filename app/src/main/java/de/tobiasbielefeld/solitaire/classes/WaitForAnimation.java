package de.tobiasbielefeld.solitaire.classes;

import android.os.Handler;
import android.os.Message;

import static de.tobiasbielefeld.solitaire.SharedData.animate;

/**
 * This handler just waits until all card animations are over, then executes a method.
 */

public class WaitForAnimation extends Handler {

    private static final int TIME_DELTA = 100;
    MessageCallBack messageCallBack;

    public WaitForAnimation(MessageCallBack callback){
        messageCallBack = callback;
    }

    public void sendDelayed(){
        sendEmptyMessageDelayed(0, TIME_DELTA);
    }

    public void sendNow(){
        sendEmptyMessage(0);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (animate.cardIsAnimating() || messageCallBack.additionalHaltCondition()){
            sendEmptyMessageDelayed(0,TIME_DELTA);
        } else {
            messageCallBack.doAfterAnimation();
        }
    }

    public interface MessageCallBack {
        void doAfterAnimation();

        boolean additionalHaltCondition();
    }


}
