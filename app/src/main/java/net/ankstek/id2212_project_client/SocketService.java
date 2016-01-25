package net.ankstek.id2212_project_client;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Oscar on 2015-12-29.
 */
public class SocketService extends Service {
    private final IBinder myBinder = new MyLocalBinder();
    private Socket server;
    private PrintWriter out;
    private BufferedReader in;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return myBinder;
    }

    public void createSocket(String hostname, int port) {
        System.out.println("IPADRESS" + hostname);
        System.out.println("-------------------------------------------------------");

        new ConnectSocketTask().execute(hostname, port);
    }

    public String getMessage(){
        String line = null;
        while (line == null) {
            try {
                line = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return line;
    }

    public String readMessage(){

        String line = null;
        while (line == null) {
            try {
                System.out.println("Socket: reading message!");
                line = in.readLine();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Socket: message received!");

        return line;
    }

    public String sendMessage(String message) {

        System.out.println("Socket: sending message!");
        out.println(message);
        out.flush();
        System.out.println("Socket: message sent!");

        return readMessage();
    }

    public class MyLocalBinder extends Binder {
        SocketService getService() {
            return SocketService.this;
        }
    }

    private class ConnectSocketTask extends AsyncTask<Object, Void, Void> {


        @Override
        protected Void doInBackground(Object... params) {
            System.out.println("Connecting to Server");
            String hostname = (String) params[0];
            int port = (int) params[1];
            try {
                server = new Socket(hostname, port);
                out = new PrintWriter(server.getOutputStream());
                in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                System.out.println("Connecting to Server: Connected");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Void aVoid = null;
            return aVoid;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
