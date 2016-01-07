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

    String ipaddr;
    String URLlogin;
    String URLregister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView errorMsg = (TextView) findViewById(R.id.textMsg);
        errorMsg.setVisibility(View.INVISIBLE);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ipaddr = extras.getString("IP_ADRESS");
        }
        URLlogin = "http://" + ipaddr + ":8080/ID2212-project-Server/login";
        URLregister = "http://" + ipaddr + ":8080/ID2212-project-Server/register";

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

    private class HttpTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... urls) {

            String finishedURL = urls[0] + "?userName=" + urls[1] + "&passWord=" + urls[2];
            String[] response = new String[3];
            response[1] = urls[1];
            response[2] = urls[2];

            try {
                URL httpURL = new URL(finishedURL);
                HttpURLConnection urlConnection = (HttpURLConnection) httpURL.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    total.append(line);
                }
                response[0] = total.toString();
                urlConnection.disconnect();

            }
            catch (MalformedURLException e) {
                e.printStackTrace();
                response[0] = "URL error!";
            } catch (IOException e){
                e.printStackTrace();
                response[0] = "Connection error!";
            }

            return response;
        }

        @Override
        protected void onPostExecute(String... result) {

            if (result[0].equals("OK")){
                Intent lobbyIntent = new Intent(getApplicationContext(), LobbyActivity.class);
                lobbyIntent.putExtra("IP_ADRESS",ipaddr);
                lobbyIntent.putExtra("USERNAME",result[1]);
                lobbyIntent.putExtra("PASSWORD",result[2]);

                startActivity(lobbyIntent);
            }
            if (result[0].equals("USER_ALREADY_EXISTS")){
                TextView errorMsg = (TextView) findViewById(R.id.textMsg);
                errorMsg.setText("Username is already taken!");
                errorMsg.setVisibility(View.VISIBLE);
            }
            if (result[0].equals("COULD_NOT_REGISTER")){
                TextView errorMsg = (TextView) findViewById(R.id.textMsg);
                errorMsg.setText("Database error!");
                errorMsg.setVisibility(View.VISIBLE);
            }
            if (result[0].equals("WRONG_PASSWORD")){
                TextView errorMsg = (TextView) findViewById(R.id.textMsg);
                errorMsg.setText("Invalid password!");
                errorMsg.setVisibility(View.VISIBLE);
            }
            if (result[0].equals("NO_SUCH_USER_EXISTS")){
                TextView errorMsg = (TextView) findViewById(R.id.textMsg);
                errorMsg.setText("No such user exists!");
                errorMsg.setVisibility(View.VISIBLE);
            }
            TextView errorMsg = (TextView) findViewById(R.id.textMsg);
            errorMsg.setText(result[0]);
            errorMsg.setVisibility(View.VISIBLE);
        }
    }
}

