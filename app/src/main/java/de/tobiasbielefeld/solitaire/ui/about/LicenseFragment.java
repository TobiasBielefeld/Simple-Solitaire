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

package de.tobiasbielefeld.solitaire.ui.about;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.tobiasbielefeld.solitaire.R;

/**
 * Just loads the GPL license from the html file
 */

public class LicenseFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_about_tab2, container, false);

        TextView textViewGPLLicense = (TextView) view.findViewById(R.id.aboutLicenseText);          //my app license

        //show the beautiful gpl license from the license.html in the assets folder
        try {
            InputStream is = getActivity().getAssets().open("license.html");
            textViewGPLLicense.setText(Html.fromHtml(new String(getStringFromInputStream(is))));
        } catch (IOException ignored) {}

        return view;
    }

    @Override
    public void onClick(View v) {
       //nothing
    }

    /*
     * Solution from StackOverflow to read a html file, found here:
     * https://stackoverflow.com/questions/2436385/android-getting-from-a-uri-to-an-inputstream-to-a-byte-array
     */
    private byte[] getStringFromInputStream(InputStream is) {

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