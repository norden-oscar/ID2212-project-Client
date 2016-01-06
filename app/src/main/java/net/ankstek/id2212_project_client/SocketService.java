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

        new ConnectSocketTask().execute(hostname, port);
    }

    public String sendMessage(String message) {

        out.println(message);
        out.flush();
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

    public class MyLocalBinder extends Binder {
        SocketService getService() {
            return SocketService.this;
        }
    }

    private class ConnectSocketTask extends AsyncTask<Object, Void, Void> {


        @Override
        protected Void doInBackground(Object... params) {
            String hostname = (String) params[0];
            int port = (int) params[1];
            try {
                server = new Socket(hostname, port);
                out = new PrintWriter(server.getOutputStream());
                in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
