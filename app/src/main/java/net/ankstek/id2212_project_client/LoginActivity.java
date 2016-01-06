package net.ankstek.id2212_project_client;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class LoginActivity extends AppCompatActivity {

    String URLlogin = "http://10.0.2.2:8080/ID2212-project-Server/login";
    String URLregister = "http://10.0.2.2:8080/ID2212-project-Server/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView errorMsg = (TextView) findViewById(R.id.textMsg);
        errorMsg.setVisibility(View.INVISIBLE);

    }

    public void onClickLoginButton(View v){

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        TextView errorMsg = (TextView) findViewById(R.id.textMsg);
        EditText usr = (EditText) findViewById(R.id.username);
        EditText psw = (EditText) findViewById(R.id.password);

        String userStr = usr.getText().toString();
        String passStr = psw.getText().toString();

        if (networkInfo != null && networkInfo.isConnected()) {
            new HttpTask().execute(URLlogin, userStr, passStr);
        } else {
            errorMsg.setVisibility(View.VISIBLE);
            errorMsg.setText("No network connection available.");
        }
    }

    public void onClickRegisterButton(View v){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        TextView errorMsg = (TextView) findViewById(R.id.textMsg);
        EditText usr = (EditText) findViewById(R.id.username);
        EditText psw = (EditText) findViewById(R.id.password);

        String userStr = usr.getText().toString();
        String passStr = psw.getText().toString();

        if (networkInfo != null && networkInfo.isConnected()) {
            new HttpTask().execute(URLregister, userStr, passStr);
        } else {
            errorMsg.setVisibility(View.VISIBLE);
            errorMsg.setText("No network connection available.");
        }
    }

    private class HttpTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String finishedURL = urls[0] + "?userName=" + urls[1] + "&passWord=" + urls[2];
            String response;

            try {
                URL httpURL = new URL(finishedURL);
                HttpURLConnection urlConnection = (HttpURLConnection) httpURL.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    total.append(line);
                }
                response = total.toString();
                urlConnection.disconnect();

            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                return "URL error!";
            } catch (IOException e){
                e.printStackTrace();
                return "Connection error!";
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result.equals("OK")){
                Intent lobbyIntent = new Intent(getApplicationContext(), LobbyActivity.class);
                startActivity(lobbyIntent);
            }

            TextView errorMsg = (TextView) findViewById(R.id.textMsg);
            errorMsg.setText(result);
            errorMsg.setVisibility(View.VISIBLE);



        }
    }

}

