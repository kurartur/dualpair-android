package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.resource.Match;
import lt.dualpair.android.resource.User;

public class MatchListAdapter extends BaseAdapter {

    private List<Match> matchList;
    private Activity activity;

    public MatchListAdapter(List<Match> matchList, Activity activity) {
        this.matchList = matchList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return matchList.size();
    }

    @Override
    public Object getItem(int position) {
        return matchList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Match match = (Match)getItem(position);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.match_list_item, parent, false);
        ImageView picture = (ImageView)view.findViewById(R.id.picture);
        TextView name = (TextView)view.findViewById(R.id.name);
        loadPhoto(match.getOpponent().getUser(), picture);
        name.setText(match.getOpponent().getUser().getName());
        return view;
    }

    private void loadPhoto(User user, ImageView picture) {
        Picasso.with(activity)
            .load(user.getPhotos().get(0).getSourceUrl())
            .error(R.drawable.image_not_found)
            .into(picture, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                }
        });
    }
}
