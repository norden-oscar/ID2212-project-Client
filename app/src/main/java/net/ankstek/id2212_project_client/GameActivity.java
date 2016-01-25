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
import android.widget.Button;
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
    int[] buttonList = new int[9];

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

        buttonList[0] = R.id.btnGame0;
        buttonList[1] = R.id.btnGame1;
        buttonList[2] = R.id.btnGame2;
        buttonList[3] = R.id.btnGame3;
        buttonList[4] = R.id.btnGame4;
        buttonList[5] = R.id.btnGame5;
        buttonList[6] = R.id.btnGame6;
        buttonList[7] = R.id.btnGame7;
        buttonList[8] = R.id.btnGame8;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ipaddr = extras.getString("IP_ADRESS");
            port = extras.getInt("PORT");
            username = extras.getString("USERNAME");
            password = extras.getString("PASSWORD");
        }

        for(int i = 0; i < buttonIsTaken.length; i++) {
            buttonIsTaken[i] = false;
        }

        TextView console = (TextView) findViewById(R.id.textConsole);
        console.setText("Please wait for opponent");
        TextView round = (TextView) findViewById(R.id.textRoundNumber);
        round.setText("" + 1);

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
            TextView opponent = (TextView) findViewById(R.id.textEnemyName);

            System.out.println("Parsing result: " + result);

            if(result.equals("WAIT")){
                console.setText("Waiting...");
                new WaitTask().execute();
            }
            else {
                opponent.setText(result);
                new WaitTask().execute();
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
            TextView round = (TextView) findViewById(R.id.textRoundNumber);
            TextView opponent = (TextView) findViewById(R.id.textEnemyName);

            if(result.equals("WAIT")){
                console.setText("Waiting...");
                myTurn = false;
                new WaitTask().execute();
            }
            else if (result.equals("BEGIN")) {
                console.setText("Play!");
                myTurn = true;
            }
            else if (result.equals("DRAW")) {
                console.setText("Draw!");
                for(int i = 0; i < buttonIsTaken.length; i++) {
                    buttonIsTaken[i] = false;
                }
                new WaitTask().execute();
            }
            else if(result.substring(0,1).equals("X") || result.substring(0,1).equals("O")){
                String[] msg = result.trim().split("\\|");

                for (String str : msg){
                    System.out.println("message: " + str);
                }

                Button btn = (Button) findViewById(buttonList[ Integer.parseInt(msg[1]) ]);
                btn.setText(result.substring(0,1));
                buttonIsTaken[Integer.parseInt(msg[1])] = true;
                myTurn = true;
            }
            else if(result.equals("WON ROUND")){
                console.setText("You won the round!");
                round.setText("" + (roundCount + 1));
                for(int i = 0; i < buttonIsTaken.length; i++) {
                    buttonIsTaken[i] = false;
                }
                new WaitTask().execute();
            }
            else if(result.equals("LOST ROUND")){
                console.setText("You lost the round!");
                round.setText("" + (roundCount + 1));
                for(int i = 0; i < buttonIsTaken.length; i++) {
                    buttonIsTaken[i] = false;
                }
                new WaitTask().execute();
            }
            else if(result.equals("WON GAME")){
                console.setText("Hey you're pretty good at this, you won!");
                for(int i = 0; i < buttonIsTaken.length; i++) {
                    buttonIsTaken[i] = true;
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent lobbyIntent = new Intent(getApplicationContext(), LobbyActivity.class);
                lobbyIntent.putExtra("IP_ADRESS",ipaddr);
                lobbyIntent.putExtra("USERNAME",username);
                lobbyIntent.putExtra("PASSWORD", password);
                startActivity(lobbyIntent);
            }
            else if(result.equals("LOST GAME")){
                console.setText("You lost the game? Wow, you're bad.");
                for(int i = 0; i < buttonIsTaken.length; i++) {
                    buttonIsTaken[i] = true;
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent lobbyIntent = new Intent(getApplicationContext(), LobbyActivity.class);
                lobbyIntent.putExtra("IP_ADRESS",ipaddr);
                lobbyIntent.putExtra("USERNAME", username);
                lobbyIntent.putExtra("PASSWORD",password);
                startActivity(lobbyIntent);

            }
            else {
                opponent.setText(result);
                new WaitTask().execute();
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
                console.setText("Waiting...");
                myTurn = false;
                new WaitTask().execute();
            }
            else if(result.equals("BEGIN")) {
                console.setText("Play!");
                myTurn = true;
            }
            else if (result.equals("DRAW")) {
                console.setText("Draw!");
                for(int i = 0; i < buttonIsTaken.length; i++) {
                    buttonIsTaken[i] = false;
                }
                new WaitTask().execute();
            }
            else if(result.equals("WON ROUND")){
                console.setText("You won the round!");
                round.setText("" + (roundCount + 1));
                for(int i = 0; i < buttonIsTaken.length; i++) {
                    buttonIsTaken[i] = false;
                }
                new WaitTask().execute();
            }
            else if(result.equals("LOST ROUND")){
                console.setText("You lost the round!");
                round.setText("" + (roundCount + 1));
                for(int i = 0; i < buttonIsTaken.length; i++) {
                    buttonIsTaken[i] = false;
                }
                new WaitTask().execute();
            }
            else if(result.equals("WON GAME")){
                console.setText("Hey you're pretty good at this, you won!");
                for(int i = 0; i < buttonIsTaken.length; i++) {
                    buttonIsTaken[i] = true;
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
            else if(result.equals("LOST GAME")){
                console.setText("You lost the game? Wow, you're bad.");
                for(int i = 0; i < buttonIsTaken.length; i++) {
                    buttonIsTaken[i] = true;
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
            else if (result.equals("DRAW")) {
                console.setText("Draw!");
                for(int i = 0; i < buttonIsTaken.length; i++) {
                    buttonIsTaken[i] = false;
                }
                new WaitTask().execute();
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
                case R.id.btnForfeit:
                    if(!buttonIsTaken[0]){
                        new PlayTask().execute("forfeit");
                    }
                    break;
            }
            myTurn = false;
        }
    }
}
