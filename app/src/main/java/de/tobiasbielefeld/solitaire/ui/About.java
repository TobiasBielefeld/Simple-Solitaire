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
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
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

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * About activity, shows my information in a tab view with a changelog
 */

@SuppressLint("SimpleDateFormat")
public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActionBar actionBar = getSupportActionBar();
        TabHost host = (TabHost) findViewById(R.id.aboutTabHost);

        TextView textViewBuildDate = (TextView) findViewById(R.id.aboutTextViewBuild);              //build date
        TextView textViewAppVersion = (TextView) findViewById(R.id.aboutTextViewVersion);           //app version
        TextView textViewCC0License = (TextView) findViewById(R.id.aboutTextViewLicense);           //cc0 license from the pictures
        TextView textViewGPLLicense = (TextView) findViewById(R.id.aboutLicenseText);               //my app license
        TextView textViewGitHubLink = (TextView) findViewById(R.id.aboutTextViewGitHubLink);        //link for the gitHub repo

        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");                           //generate a new date for build date
        String strDt = simpleDate.format(BuildConfig.TIMESTAMP);                                    //get the build date as a string


        //set a nice back arrow in the actionBar
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        //load preferences
        showOrHideStatusBar(this);
        setOrientation(this);

        //initialize the host tab views
        if (host != null) {
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

        //update the textViews
        textViewAppVersion.setText(String.format(Locale.getDefault(), "%s: %s", getString(R.string.app_version), BuildConfig.VERSION_NAME));
        textViewBuildDate.setText(String.format(Locale.getDefault(), "%s: %s", getString(R.string.about_build_date), strDt));
        textViewCC0License.setMovementMethod(LinkMovementMethod.getInstance());
        textViewGitHubLink.setMovementMethod(LinkMovementMethod.getInstance());

        //show the beautiful gpl license from the license.html in the assets folder
        try {
            InputStream is = getAssets().open("license.html");
            textViewGPLLicense.setText(Html.fromHtml(new String(getStringFromInputStream(is))));
        } catch (IOException ignored) {}

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //only menu item is the back button in the action bar so finish this activity
        finish();
        return true;
    }

    private byte[] getStringFromInputStream(InputStream is) {
        /*
         * Solution from StackOverflow, found here:
         * https://stackoverflow.com/questions/2436385/android-getting-from-a-uri-to-an-inputstream-to-a-byte-array
         */
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
}