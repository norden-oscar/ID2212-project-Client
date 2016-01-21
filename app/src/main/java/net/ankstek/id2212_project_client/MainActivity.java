package net.ankstek.id2212_project_client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void btnStartOnClick(View v){
        EditText ip = (EditText) findViewById(R.id.textIP);
        String ipaddr = ip.getText().toString();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.putExtra("IP_ADRESS",ipaddr);
        startActivity(loginIntent);
    }

    public void btnExitOnClick(View v){
        finish();
        System.exit(0);
    }
}
