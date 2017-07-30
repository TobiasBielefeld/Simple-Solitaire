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

import static de.tobiasbielefeld.solitaire.SharedData.*;

public class Sounds {

    public enum names {CARD_FLIP, CARD_FLIP_BACK, CARD_HIT}

    private SoundPool sp;// = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    private int[] soundList = new int[8];
    private Context context;

    public Sounds(Context context){
        this.context = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createNewSoundPool();
        } else {
            createOldSoundPool();
        }

        loadSounds();
    }

    public void playSound(names name) {

        if (savedSharedData.getBoolean(PREF_KEY_SOUND_ENABLED, DEFAULT_SOUND_ENABLED)) {
            switch (name){
                case CARD_FLIP:
                    sp.play(soundList[0], 1, 1, 0, 0, 1);
                    break;
                case CARD_FLIP_BACK:
                    sp.play(soundList[1], 1, 1, 0, 0, 1);
                    break;
                case CARD_HIT:
                    sp.play(soundList[2], 1, 1, 0, 0, 1);
                    break;
            }

        }
    }

    private void loadSounds(){
        soundList[0] = sp.load(context, R.raw.card_flip, 1);
        soundList[1] = sp.load(context, R.raw.card_flip_back, 1);
        //soundList[2] = sp.load(this, R.raw.explosion, 1);
        //soundList[3] = sp.load(this, R.raw.boop, 1);
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
