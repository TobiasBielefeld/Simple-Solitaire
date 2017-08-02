package de.tobiasbielefeld.solitaire.classes;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;


/**
 * Created by tobias on 02.08.17.
 */

public class BackgroundSound extends AsyncTask<Context,Void,Void> {


    private MediaPlayer player;
    private Context context;

    private String currentlyPlaying = "";

    @Override
    public Void doInBackground(Context... params) {
        String soundToPlay = savedSharedData.getString(PREF_KEY_BACKGROUND_SOUND, DEFAULT_BACKGROUND_SOUND);
        context = params[0];

        if (player == null){
            start(soundToPlay);
        } else  if (!soundToPlay.equals(currentlyPlaying)){
            player.stop();
            start(soundToPlay);
        } else {
            player.start();
        }

        return null;
    }

    public void start(String soundToPlay){

        int soundID = 0;
        currentlyPlaying = soundToPlay;

        switch (soundToPlay){
            case "0":
                //do not play any background music
                return;
            case "1":
                soundID = R.raw.background_music_1;
                break;
            case "2":
                soundID = R.raw.background_music_2;
                break;
            case "3"://TODO CHANGE !!!
                soundID = R.raw.background_music_2;
                break;
        }

        player = MediaPlayer.create(context, soundID);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);
        player.start();
    }

    public void pause(){

        if (player!=null && player.isPlaying()) {
            player.pause();
        }
    }


}