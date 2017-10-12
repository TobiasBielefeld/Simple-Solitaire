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

package de.tobiasbielefeld.solitaire.handler;

import android.os.Handler;
import android.os.Message;


import static de.tobiasbielefeld.solitaire.SharedData.activityCounter;
import static de.tobiasbielefeld.solitaire.SharedData.backgroundSound;

/**
 * Check here if the application is closed. If the activityCounter reaches zero, no activity
 * is in the foreground so stop the background music. But try stopping some milliseconds delayed,
 * because otherwise the music would stop/restart between the activities
 */

public class HandlerStopBackgroundMusic extends Handler {


    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (activityCounter==0){
            backgroundSound.pausePlaying();
        }
    }
}