/*
 * Copyright (C) 2016  Tobias Bielefeld
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you want to contact me, send me an e-mail at tobias.bielefeld@gmail.com
 */

package de.tobiasbielefeld.solitaire.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.helper.BackgroundMusic.status.*;


/**
 * Manages the background music. Will be played in the whole application.
 */

public class BackgroundMusic extends AsyncTask<Context,Void,Void> {

    public enum status {stopped,paused,playing}

    private MediaPlayer player;
    private String currentlyPlaying = "";
    private int currentVolume = 0;
    private status currentStatus = stopped;

    @Override
    public Void doInBackground(Context... params) {

        if (!prefs.getSavedSoundEnabled()){
            stopPlaying();
            return null;
        }

        String soundToPlay = prefs.getSavedBackgroundMusic();
        int volumeToApply = prefs.getSavedBackgroundVolume();

        if (volumeToApply!=currentVolume){
            changeVolume();
            currentVolume = volumeToApply;
        }

        if (currentStatus == stopped) {
            start(params[0],soundToPlay);
        } else if (!soundToPlay.equals(currentlyPlaying)){
            stopPlaying();
            start(params[0],soundToPlay);
        } else if (currentStatus == paused) {
            continuePlaying();
        }

        return null;
    }

    public void changeVolume(){
        if (player!=null){
            int currentVolume = prefs.getSavedBackgroundVolume();
            float log1 = currentVolume == 100 ? 0 : (float)(Math.log(100-currentVolume)/Math.log(100));
            float volume = 1f-log1;

            player.setVolume(volume,volume);
        }
    }

    public void start(Context context, String soundToPlay){

        if (soundToPlay.equals("0")){
            stopPlaying();
            return;
        }

        int soundID = 0;
        currentlyPlaying = soundToPlay;

        switch (soundToPlay){
            case "1":
                soundID = R.raw.background_music_1;
                break;
            case "2":
                soundID = R.raw.background_music_2;
                break;
            case "3":
                soundID = R.raw.background_music_3;
                break;
            case "4":
                soundID = R.raw.background_music_4;
                break;
        }

        if (player!=null){
            player.release();
            player = null;
        }

        player = MediaPlayer.create(context, soundID);
        player.setLooping(true); // Set looping
        changeVolume();
        continuePlaying();

    }

    public void pausePlaying(){
        if (player!=null && player.isPlaying()) {
            player.pause();
        }

        currentStatus = paused;
    }

    private void stopPlaying(){
        if (player!=null && player.isPlaying()) {
            player.stop();
        }

        currentStatus = stopped;
    }

    private void continuePlaying(){
        if (player!=null) {
            player.start();
        }

        currentStatus = playing;
    }

}