package net.ankstek.id2212_project_client;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {

    private ListView gameListView;
    private GameListEntryAdapter gameListEntryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        gameListEntryAdapter = new GameListEntryAdapter(this, R.layout.game_list_entry);

        // Setup the list view
        gameListView = (ListView) findViewById(R.id.listView);
        gameListView.setAdapter(gameListEntryAdapter);


        // Populate the list, through the adapter
        for (final GameListEntry entry : getGameEntries()) {
            gameListEntryAdapter.add(entry);
        }



    }


    private List<GameListEntry> getGameEntries() {

        ArrayList<GameListEntry> gle = new ArrayList<GameListEntry>();

        gle.add(new GameListEntry("ettan", "tvåan", 1337, "Bisi"));
        gle.add(new GameListEntry("trean", "fyran", 8008, "Free"));

        //TODO: fetch game list from server

    return gle;
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
        //TODO: something something fetch data
        gameListEntryAdapter.notifyDataSetChanged();
    }
}
