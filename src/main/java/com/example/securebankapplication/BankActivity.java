package com.example.securebankapplication;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class BankActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static final String LOG_TAG = BankActivity.class.getSimpleName();

    private TextView mTextView;
    private TextView mTextViewScroll;

    private String sharedPrefsFile = "com.example.securebankapplication";
    private SharedPreferences mPreferences;

    private String masterKeyAlias;
    private Context context;

    private String jsonStringUsers = null;
    private String jsonStringAccounts = null;

    private JSONArray jsonArray = null;
    private JSONObject jsonObject = null;

    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        mTextView = (TextView) findViewById(R.id.textView);
        mTextViewScroll = (TextView) findViewById(R.id.textView_scroll);

        mSpinner = (Spinner) findViewById(R.id.spinner);
        mSpinner.setOnItemSelectedListener(this);

        activateEncryptedSharedPreferences();

        jsonStringUsers =  mPreferences.getString("Users", null);
        jsonStringAccounts =  mPreferences.getString("Accounts", null);


        if(jsonStringUsers !=null){
            populateSpinner(jsonStringUsers);
        } else{
            mTextView.setText("You need to click on UPDATE the first time");
        }
    }

    public void activateEncryptedSharedPreferences(){

        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        context = getApplicationContext();

        try {
            mPreferences = EncryptedSharedPreferences.create(
                    sharedPrefsFile,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }


    public void populateSpinner(String jsonString){

        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //String Array
        String[] ArrayStringSpinner = new String[jsonArray.length() + 1];

        ArrayStringSpinner[0] = "Select an user...";

        for(int i = 0; i < jsonArray.length(); i++){

            try {
                jsonObject = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String tempString = null;

            try {
                tempString = "ID : "+jsonObject.getString("id")+", "+jsonObject.getString("name")+", "+jsonObject.getString("lastname");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayStringSpinner[i+1] = tempString;
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ArrayStringSpinner);

        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        if (mSpinner != null) {
            mSpinner.setAdapter(adapter);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String spinnerLabel = parent.getItemAtPosition(position).toString();
        //[{"id":"1","name":"Georgiana","lastname":"Maggio"} for example

        String showAccounts = "";

        if(spinnerLabel.equals("Select an user...")){
            mTextViewScroll.setText("");
        }else{

            try {
                jsonArray = new JSONArray(jsonStringAccounts);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < jsonArray.length(); i++){

                try {
                    jsonObject = jsonArray.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String tempStringAccount = "";

                try {
                    tempStringAccount += "ID : "+jsonObject.getString("id")+System.lineSeparator();
                    tempStringAccount += "Account name : "+jsonObject.getString("accountName")+System.lineSeparator();
                    tempStringAccount += "Amount : "+jsonObject.getString("amount")+System.lineSeparator();
                    tempStringAccount += "Iban : "+jsonObject.getString("iban")+System.lineSeparator();
                    tempStringAccount += "Currency : "+jsonObject.getString("currency")+System.lineSeparator();
                    tempStringAccount += System.lineSeparator();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                showAccounts += tempStringAccount;

            }

            mTextViewScroll.setText(showAccounts);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void startUpdate(View view) {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(isConnected){
            mTextView.setText("Fetching...");
            new AccountsAsyncTask(mPreferences).execute(stringFromJNI().split(",")[0]);
            new UsersAsyncTask(mPreferences,this).execute(stringFromJNI().split(",")[1]);

            //To reset the spinner after activity.recreate()
            mSpinner.setSelection(0);

        } else{
            mTextView.setText("You must be connected to internet to UPDATE");
        }

    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}