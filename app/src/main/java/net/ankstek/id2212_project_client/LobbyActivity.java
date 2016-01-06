package net.ankstek.id2212_project_client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ListView;

import java.util.List;

public class LobbyActivity extends AppCompatActivity {

    private ListView gameListView;
    final GameListEntryAdapter gameListEntryAdapter = new GameListEntryAdapter(this, R.layout.game_list_entry);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // Setup the list view
        gameListView = (ListView) findViewById(R.id.listView);
        gameListView.setAdapter(gameListEntryAdapter);

        // Populate the list, through the adapter
        for (final GameListEntry entry : getGameEntries()) {
            gameListEntryAdapter.add(entry);
        }


    }


    private List<GameListEntry> getGameEntries() {

    //TODO: fetch game list from server

    return null;
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
