package de.tobiasbielefeld.solitaire.handler;

import android.os.Handler;
import android.os.Message;

import static de.tobiasbielefeld.solitaire.SharedData.animate;
import static de.tobiasbielefeld.solitaire.SharedData.handlerRecordListUndo;
import static de.tobiasbielefeld.solitaire.SharedData.recordList;


/**
 * Created by tobias on 03.10.17.
 */

public class HandlerRecordListUndo extends Handler {


    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (!animate.cardIsAnimating()) {
            if (recordList.hasMoreToUndo()){
                recordList.undoMore();
                handlerRecordListUndo.sendEmptyMessageDelayed(0,100);
            }

        } else {
            handlerRecordListUndo.sendEmptyMessageDelayed(0,100);
        }




    }
}
