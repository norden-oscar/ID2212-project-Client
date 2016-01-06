package net.ankstek.id2212_project_client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adapts NewsEntry objects onto views for lists
 */
public final class GameListEntryAdapter extends ArrayAdapter<GameListEntry> {

    private final int newsItemLayoutResource;

    public GameListEntryAdapter(final Context context, final int newsItemLayoutResource) {
        super(context, 0);
        this.newsItemLayoutResource = newsItemLayoutResource;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        // We need to get the best view (re-used if possible) and then
        // retrieve its corresponding ViewHolder, which optimizes lookup efficiency
        final View view = getWorkingView(convertView);
        final ViewHolder viewHolder = getViewHolder(view);
        final GameListEntry entry = getItem(position);

        // Setting the title view is straightforward
        viewHolder.player1View.setText(entry.getPlayer1());
        viewHolder.player2View.setText(entry.getPlayer2());
        viewHolder.gameNumberView.setText("" + entry.getGameNumber());
        viewHolder.stateView.setText(entry.getStatus());

        return view;
    }

    private View getWorkingView(final View convertView) {
        // The workingView is basically just the convertView re-used if possible
        // or inflated new if not possible
        View workingView = null;

        if(null == convertView) {
            final Context context = getContext();
            final LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

            workingView = inflater.inflate(newsItemLayoutResource, null);
        } else {
            workingView = convertView;
        }

        return workingView;
    }

    private ViewHolder getViewHolder(final View workingView) {
        // The viewHolder allows us to avoid re-looking up view references
        // Since views are recycled, these references will never change
        final Object tag = workingView.getTag();
        ViewHolder viewHolder = null;


        if(null == tag || !(tag instanceof ViewHolder)) {
            viewHolder = new ViewHolder();

            viewHolder.player1View = (TextView) workingView.findViewById(R.id.txtPlayer1);
            viewHolder.player2View = (TextView) workingView.findViewById(R.id.txtPlayer2);
            viewHolder.gameNumberView = (TextView) workingView.findViewById(R.id.txtGameNumber);
            viewHolder.stateView = (TextView) workingView.findViewById(R.id.txtStatus);

            workingView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) tag;
        }

        return viewHolder;
    }

    /**
     * ViewHolder allows us to avoid re-looking up view references
     * Since views are recycled, these references will never change
     */
    private static class ViewHolder {
        public TextView player1View;
        public TextView player2View;
        public TextView gameNumberView;
        public TextView stateView;
    }


}
