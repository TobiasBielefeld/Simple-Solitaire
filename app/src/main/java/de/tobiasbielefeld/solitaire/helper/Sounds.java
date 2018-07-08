package de.tobiasbielefeld.solitaire.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

/**
 * Created by tobias on 25.07.17.
 */

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.SharedData;

import static de.tobiasbielefeld.solitaire.SharedData.*;

public class Sounds {

    public enum names {CARD_RETURN, CARD_SET, HINT, DEAL_CARDS, SHOW_AUTOCOMPLETE}

    private SoundPool sp;// = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    private int[] soundList = new int[9];

    public Sounds(Context context){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        } else {
            createOldSoundPool();
        }

        loadSounds(context);
    }

    public void playSound(names name) {

        if (prefs.getSavedSoundEnabled() && !SharedData.stopUiUpdates) {
            switch (name){
                case CARD_RETURN:
                    sp.play(soundList[0], 1, 1, 0, 0, 1);
                    break;
                case CARD_SET:
                    sp.play(soundList[1], 1, 1, 0, 0, 1);
                    break;
                case HINT:
                    sp.play(soundList[2], 1, 1, 0, 0, 1);
                    break;
                case DEAL_CARDS:
                    sp.play(soundList[3], 1, 1, 0, 0, 1);
                    break;
                case SHOW_AUTOCOMPLETE:
                    sp.play(soundList[4], 1, 1, 0, 0, 1);
                    break;
            }
        }
    }

    public void playWinSound() {
        if (prefs.getSavedSoundEnabled()) {
            switch (prefs.getSavedWinSound()) {
                case "0":
                    sp.play(soundList[5], 1, 1, 0, 0, 1);
                    break;
                case "1":
                    sp.play(soundList[6], 1, 1, 0, 0, 1);
                    break;
                case "2":
                    sp.play(soundList[7], 1, 1, 0, 0, 1);
                    break;
                case "3":
                    sp.play(soundList[8], 1, 1, 0, 0, 1);
                    break;
            }
        }
    }

    private void loadSounds(Context context){
        soundList[0] = sp.load(context, R.raw.card_return, 1);
        soundList[1] = sp.load(context, R.raw.card_set, 1);
        soundList[2] = sp.load(context, R.raw.hint, 1);
        soundList[3] = sp.load(context, R.raw.deal_cards, 1);
        soundList[4] = sp.load(context, R.raw.show_autocomplete, 1);

        soundList[5] = sp.load(context, R.raw.win_1, 1);
        soundList[6] = sp.load(context, R.raw.win_2, 1);
        soundList[7] = sp.load(context, R.raw.win_3, 1);
        soundList[8] = sp.load(context, R.raw.win_4, 1);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        sp = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    protected void createOldSoundPool() {
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    }

}
