package net.ankstek.id2212_project_client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    String ipaddr;
    int port;
    String username;
    String password;
    int roundCount = 0;
    Boolean myTurn = false;
    Boolean[] buttonIsTaken = new Boolean[9];
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
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent socketServiceIntent = new Intent(this, SocketService.class);
        bindService(socketServiceIntent, myConnection, Context.BIND_AUTO_CREATE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ipaddr = extras.getString("IP_ADRESS");
            port = extras.getInt("PORT");
            username = extras.getString("USERNAME");
            password = extras.getString("PASSWORD");
        }

        for(Boolean bool : buttonIsTaken) {
            bool = false;
        }


        TextView console = (TextView) findViewById(R.id.textConsole);
        console.setText("Please wait for opponent");
        new HelloTask().execute(username);
    }

    private class HelloTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... str) {

            System.out.println("Str hello = " + str[0]);
            while (!isBound){
                System.out.println("Waiting for socket to bind...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            System.out.println("Sending hello!");
            return socketService.sendMessage(str[0]);
        }

        @Override
        protected void onPostExecute (String result) {

            TextView console = (TextView) findViewById(R.id.textConsole);
            TextView round = (TextView) findViewById(R.id.textRoundNumber);

            System.out.println("Parsing result: " + result);

            if(result.equals("WAIT")){
                console.setText("Opponents turn!");
                round.setText("" + (roundCount + 1));
            }
            if(result.equals("BEGIN")) {
                console.setText("Your turn!");
                round.setText("" + (roundCount + 1));
                myTurn = true;
            }
        }
    }

    private class WaitTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return socketService.getMessage();
        }

        @Override
        protected void onPostExecute (String result) {

            TextView console = (TextView) findViewById(R.id.textConsole);

            if(result.equals("WAIT")){
                console.setText("Opponents turn!");
            }
            if(result.equals("BEGIN")) {
                console.setText("Your turn!");
                myTurn = true;
            }
        }
    }

    private class PlayTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return socketService.sendMessage(urls[0]);
        }

        @Override
        protected void onPostExecute (String result) {

            TextView console = (TextView) findViewById(R.id.textConsole);
            TextView round = (TextView) findViewById(R.id.textRoundNumber);

            if(result.equals("WAIT")){
                console.setText("Opponents turn!");
            }
            if(result.equals("BEGIN")) {
                console.setText("Your turn!");
                myTurn = true;
            }
            if(result.equals("WON ROUND")){
                console.setText("You won the round!");
                round.setText("" + (roundCount + 1));
                for(Boolean bool : buttonIsTaken){
                    bool = false;
                }
                new WaitTask().execute();
            }
            if(result.equals("LOST ROUND")){
                console.setText("You lost the round!");
                round.setText("" + (roundCount + 1));
                for(Boolean bool : buttonIsTaken){
                    bool = false;
                }
                new WaitTask().execute();
            }
            if(result.equals("WON GAME")){
                console.setText("Hey you're pretty good at this, you won!");
                for(Boolean bool : buttonIsTaken){
                    bool = true;
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent lobbyIntent = new Intent(getApplicationContext(), LobbyActivity.class);
                lobbyIntent.putExtra("IP_ADRESS",ipaddr);
                lobbyIntent.putExtra("USERNAME",username);
                lobbyIntent.putExtra("PASSWORD",password);
                startActivity(lobbyIntent);
            }
            if(result.equals("LOST GAME")){
                console.setText("You lost the game? Wow, you're bad.");
                for(Boolean bool : buttonIsTaken){
                    bool = true;
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent lobbyIntent = new Intent(getApplicationContext(), LobbyActivity.class);
                lobbyIntent.putExtra("IP_ADRESS",ipaddr);
                lobbyIntent.putExtra("USERNAME",username);
                lobbyIntent.putExtra("PASSWORD",password);
                startActivity(lobbyIntent);

            }
        }
    }



    public void onClickChooseTile(View v) {
        if (myTurn) {
            switch (v.getId()) {
                case R.id.btnGame0:
                    if(!buttonIsTaken[0]){
                        new PlayTask().execute("" + 0);
                    }
                    break;
                case R.id.btnGame1:
                    if(!buttonIsTaken[1]){
                        new PlayTask().execute("" + 1);
                    }
                    break;
                case R.id.btnGame2:
                    if(!buttonIsTaken[2]){
                        new PlayTask().execute("" + 2);
                    }
                    break;
                case R.id.btnGame3:
                    if(!buttonIsTaken[3]){
                        new PlayTask().execute("" + 3);
                    }
                    break;
                case R.id.btnGame4:
                    if(!buttonIsTaken[4]){
                        new PlayTask().execute("" + 4);
                    }
                    break;
                case R.id.btnGame5:
                    if(!buttonIsTaken[5]){
                        new PlayTask().execute("" + 5);
                    }
                    break;
                case R.id.btnGame6:
                    if(!buttonIsTaken[6]){
                        new PlayTask().execute("" + 6);
                    }
                    break;
                case R.id.btnGame7:
                    if(!buttonIsTaken[7]){
                        new PlayTask().execute("" + 7);
                    }
                    break;
                case R.id.btnGame8:
                    if(!buttonIsTaken[8]){
                        new PlayTask().execute("" + 8);
                    }
                    break;
            }
            myTurn = false;
        }
    }
}
