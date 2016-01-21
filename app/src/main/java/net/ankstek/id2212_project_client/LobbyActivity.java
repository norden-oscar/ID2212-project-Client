package net.ankstek.id2212_project_client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LobbyActivity extends AppCompatActivity {

    private ListView gameListView;
    private GameListEntryAdapter gameListEntryAdapter;
    String ipaddr;
    String URLfindgame;
    String username;
    String password;
    String URLfetchGames;
    int port;

    int PORT_MIN = 3000;
    int PORT_MAX = 3999;

    SocketService socketService;
    boolean isBound = false;

    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            SocketService.MyLocalBinder binder = (SocketService.MyLocalBinder) service;
            socketService = binder.getService();
            isBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        gameListEntryAdapter = new GameListEntryAdapter(this, R.layout.game_list_entry);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ipaddr = extras.getString("IP_ADRESS");
            username = extras.getString("USERNAME");
            password = extras.getString("PASSWORD");
        }

        URLfindgame = "http://" + ipaddr + ":8080/ID2212-project-Server/findgame";
        URLfetchGames = "http://" + ipaddr + ":8080/ID2212-project-Server/fap";

        // Setup the list view
        gameListView = (ListView) findViewById(R.id.listView);
        gameListView.setAdapter(gameListEntryAdapter);

        // Populate the list, through the adapter
        getGameEntries();

        Intent socketServiceIntent = new Intent(this,SocketService.class);
        startService(socketServiceIntent);
        bindService(socketServiceIntent, myConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(myConnection);
    }

    private void getGameEntries() {
        new HttpTask().execute(URLfetchGames);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lobby, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            refreshGameList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshGameList(){
        getGameEntries();
        gameListEntryAdapter.notifyDataSetChanged();
    }

    public void onClickProfile(View v){

        Intent profileIntent = new Intent(getApplicationContext(), ProfileActivity.class);
        profileIntent.putExtra("IP_ADRESS",ipaddr);
        profileIntent.putExtra("USERNAME",username);
        profileIntent.putExtra("PASSWORD",password);
        startActivity(profileIntent);

    }

    public void onClickFindGameButton(View v){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            System.out.println("2===============================");
            System.out.println(URLfindgame + "?userName=" + username);
            new HttpTask().execute(URLfindgame + "?userName=" + username);
        } else {

            Toast.makeText(LobbyActivity.this, "No network connection available.", Toast.LENGTH_LONG).show();
        }
    }

    private class HttpTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            String finishedURL = urls[0];

            System.out.println("3===============================");
            System.out.println(finishedURL);

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

            if(response == null){
                response = "ERROR";
            }
            if(response.equals("")){
                response = "EMPTY_LIST";
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                port = Integer.parseInt(result.trim());
                System.out.println("Game port = " + port);

                if (port >= PORT_MIN && port <= PORT_MAX) {
                    while (!isBound){
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    socketService.createSocket(ipaddr, port);
                    Intent gameIntent = new Intent(getApplicationContext(), GameActivity.class);
                    gameIntent.putExtra("IP_ADRESS", ipaddr);
                    gameIntent.putExtra("PORT", port);
                    gameIntent.putExtra("USERNAME", username);
                    gameIntent.putExtra("PASSWORD", password);
                    startActivity(gameIntent);
                }
            }
            catch (NumberFormatException e){
                System.out.println("Wasn't a port lol");
            }
            if (result.contains("ERROR")){
                gameListEntryAdapter.add(new GameListEntry("SUM TING WONG", "HO LEE FUK", 0000, "RIP"));
            }
            else if (result.contains("EMPTY_LIST")){
                gameListEntryAdapter.add(new GameListEntry("No games found", "", 404, ""));
            }
            else {
                String[] matches = result.trim().split(" ");

                if(!(matches.length < 4)) {
                    gameListEntryAdapter.clear();

                    for (int i = 0; i < matches.length; i = i + 4) {
                        gameListEntryAdapter.add(new GameListEntry(matches[i], matches[i + 1], Integer.parseInt(matches[i + 2]), matches[i + 3]));
                    }
                }
                else {
                    gameListEntryAdapter.clear();
                    gameListEntryAdapter.add(new GameListEntry("No games found", "", 404, ""));
                }
            }
        }
    }
}
