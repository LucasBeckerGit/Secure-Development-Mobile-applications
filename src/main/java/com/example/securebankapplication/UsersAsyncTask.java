package com.example.securebankapplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UsersAsyncTask extends AsyncTask<String,Void,String> {

    private static final String LOG_TAG = UsersAsyncTask.class.getSimpleName();

    private SharedPreferences mPreferences;

    private Activity activity;


    public UsersAsyncTask(SharedPreferences mPreferences, Activity activity) {
        this.mPreferences = mPreferences;
        this.activity = activity;
    }


    @Override
    protected String doInBackground(String... strings) {

        HttpsURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonString = null;

        try {
            URL url = new URL(strings[0]);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.connect();

            // Get the InputStream.
            InputStream inputStream = urlConnection.getInputStream();

            // Create a buffered reader from that input stream.
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Use a StringBuilder to hold the incoming response.
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);

                // Since it's JSON, adding a newline isn't necessary (it won't
                // affect parsing) but it does make debugging a *lot* easier
                // if you print out the completed buffer for debugging.
                builder.append("\n");
            }

            if (builder.length() == 0) {
                // Stream was empty.  Exit without parsing.
                return null;
            }

            jsonString = builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return jsonString;
    }

    @Override
    protected void onPostExecute(String jsonString) {
        super.onPostExecute(jsonString);

        SharedPreferences.Editor preferencesEditor = mPreferences.edit();

        preferencesEditor.putString("Users", jsonString);

        preferencesEditor.apply();

        activity.recreate();
    }




}
