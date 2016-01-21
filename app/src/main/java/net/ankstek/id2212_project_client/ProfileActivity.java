package net.ankstek.id2212_project_client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    String ipaddr;
    String URLfetchprofile;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ipaddr = extras.getString("IP_ADRESS");
            username = extras.getString("USERNAME");
            password = extras.getString("PASSWORD");
        }
        URLfetchprofile = "http://" + ipaddr + ":8080/ID2212-project-Server/profile?userName=" + username;

        String response;

        try {
            URL httpURL = new URL(URLfetchprofile);
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
            response = "URL error!";
        } catch (IOException e){
            e.printStackTrace();
            response = "Connection error!";
        }
    }
}
