package lt.dualpair.android.ui.match;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.resource.UserAccount;

public class MatchListRecyclerAdapter extends RecyclerView.Adapter<MatchListRecyclerAdapter.MatchViewHolder> {

    private static final String FACEBOOK_DOMAIN = "https://www.facebook.com";

    final private List<Match> matchList = new ArrayList<>();

    public void prepend(Match match) {
        ArrayList<Match> tmpMatches = new ArrayList<>(matchList);
        matchList.clear();
        matchList.add(match);
        matchList.addAll(tmpMatches);
    }

    public void append(Match match) {
        matchList.add(match);
    }

    public void clear() {
        matchList.clear();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void setupFacebookButton(final Context context, View button, User user) {
        final UserAccount account = user.getFacebookAccount();
        if (account == null) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = FACEBOOK_DOMAIN + "/" + account.getAccountId();
                    Uri uri = Uri.parse(url);
                    try {
                        ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo("com.facebook.katana", 0);
                        if (applicationInfo.enabled) {
                            // http://stackoverflow.com/a/24547437/1048340
                            uri = Uri.parse("fb://facewebmodal/f?href=" + url);
                        }
                    } catch (PackageManager.NameNotFoundException ignored) {
                    }
                    context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            });
        }
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_grid_item, parent, false);
        MatchViewHolder holder = new MatchViewHolder(parent.getContext(), v);
        ButterKnife.bind(holder, v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MatchViewHolder holder, int position) {
        final Match match = matchList.get(position);
        loadPhoto(holder.context, match.getOpponent().getUser(), holder.picture);
        final User opponent = match.getOpponent().getUser();
        holder.name.setText(opponent.getName());
        setupFacebookButton(holder.context, holder.facebookButton, opponent);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.context.startActivity(MatchActivity.createIntent(holder.context, match.getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    private void loadPhoto(Context context, User user, ImageView picture) {
        Picasso.with(context)
                .load(user.getPhotos().get(0).getSourceUrl())
                .error(R.drawable.image_not_found)
                .into(picture, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        Log.e("MatchListPhoto", "Error while loading photo");
                    }
                });
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {

        public Context context;

        @Bind(R.id.picture) public ImageView picture;
        @Bind(R.id.name) public TextView name;
        @Bind(R.id.facebook_button) public View facebookButton;

        public MatchViewHolder(Context ctx, View itemView) {
            super(itemView);
            context = ctx;
        }
    }
}
