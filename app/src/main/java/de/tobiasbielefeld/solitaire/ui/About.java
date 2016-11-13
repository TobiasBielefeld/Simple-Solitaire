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

package de.tobiasbielefeld.solitaire.ui;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.tobiasbielefeld.solitaire.BuildConfig;
import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.savedData;

/**
 * About activity, shows my information in a tab view with a changelog
 */

@SuppressLint("SimpleDateFormat")
public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                                                         //initialize stuff
        setContentView(R.layout.activity_about);
        ActionBar actionBar = getSupportActionBar();
        TabHost host = (TabHost) findViewById(R.id.aboutTabHost);
        TextView aboutTextViewBuild = (TextView) findViewById(R.id.aboutTextViewBuild);
        TextView aboutTextViewVersion = (TextView) findViewById(R.id.aboutTextViewVersion);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");                           //generate a new date for build date
        String strDt = simpleDate.format(BuildConfig.TIMESTAMP);                                    //get the build date as a string
        TextView aboutTextViewLicense = (TextView) findViewById(R.id.aboutTextViewLicense);         //cc0 license of pictures text
        TextView aboutLicenseText = (TextView) findViewById(R.id.aboutLicenseText);
        TextView aboutTextViewGitHubLink = (TextView) findViewById(R.id.aboutTextViewGitHubLink);

        if (actionBar != null)                                                                      //set a nice back arrow in the actionBar
            actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedData.getBoolean(getString(R.string.pref_key_hide_status_bar), false))              //if fullscreen was saved, set it
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setOrientation();                                                                           //orientation according to preference


        if (host != null) {                                                                         //initialize the host tab views
            host.setup();
            //Tab 1 is the About info
            TabHost.TabSpec spec = host.newTabSpec("Tab One");
            spec.setContent(R.id.tab1);
            spec.setIndicator(getString(R.string.about_tab_1));
            host.addTab(spec);

            //Tab 2 is the changelog info
            spec = host.newTabSpec("Tab Two");
            spec.setContent(R.id.tab2);
            spec.setIndicator(getString(R.string.about_tab_2));
            host.addTab(spec);

            //Tab 3 is the changelog info
            spec = host.newTabSpec("Tab Three");
            spec.setContent(R.id.tab3);
            spec.setIndicator(getString(R.string.about_tab_3));
            host.addTab(spec);
        }

        aboutTextViewVersion.setText(String.format(Locale.getDefault(), "%s: %s", getString(R.string.app_version), BuildConfig.VERSION_NAME));
        aboutTextViewBuild.setText(String.format(Locale.getDefault(), "%s: %s", getString(R.string.about_build_date), strDt));
        aboutTextViewLicense.setMovementMethod(LinkMovementMethod.getInstance());                   //make links clickable
        aboutTextViewGitHubLink.setMovementMethod(LinkMovementMethod.getInstance());

        try {                                                                                       //show the gpl license from the license.html in the assets folder
            InputStream is = getAssets().open("license.html");
            aboutLicenseText.setText(Html.fromHtml(new String(getStringFromInputStream(is))));
        } catch (IOException ignored) {}

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                                           //only menu item is the back button in the action bar
        finish();                                                                                   //so finish this activity
        return true;
    }

    private byte[] getStringFromInputStream(InputStream is) {                                       //Solution from StackOverflow, found here: https://stackoverflow.com/questions/2436385/android-getting-from-a-uri-to-an-inputstream-to-a-byte-array
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        byte[] bReturn = new byte[0];
        String line;

        try {
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = br.readLine()) != null) {
                sb.append(line).append(" ");
            }
            String sContent = sb.toString();
            bReturn = sContent.getBytes();
        }
        catch (IOException ignored) {

        } finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException ignored) { }
            }
        }
        return bReturn;
    }

    private void setOrientation() {
        switch (savedData.getString("pref_key_orientation","1")){
            case "1": //follow system settings
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                break;
            case "2": //portrait
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case "3": //landscape
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case "4": //landscape upside down
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
        }
    }
}