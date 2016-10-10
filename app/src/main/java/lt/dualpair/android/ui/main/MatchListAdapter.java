package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.resource.UserAccount;
import lt.dualpair.android.ui.match.MatchActivity;

public class MatchListAdapter extends BaseAdapter {

    private static final String FACEBOOK_DOMAIN = "https://www.facebook.com";

    final private List<Match> matchList;
    final private Activity activity;

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
        final Match match = (Match)getItem(position);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.match_list_item, parent, false);
        final ImageView picture = (ImageView)view.findViewById(R.id.picture);
        TextView name = (TextView)view.findViewById(R.id.name);
        View facebookButton = view.findViewById(R.id.facebook_button);

        picture.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                loadPhoto(match.getOpponent().getUser(), picture);
                picture.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        final User opponent = match.getOpponent().getUser();
        name.setText(opponent.getName());
        setupFacebookButton(facebookButton, opponent);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(MatchActivity.createIntent(activity, match.getId()));
            }
        });
        return view;
    }

    private void setupFacebookButton(View button, User user) {
        final UserAccount account = user.getFacebookAccount();
        if (account == null) {
            button.setVisibility(View.GONE);
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = FACEBOOK_DOMAIN + "/" + account.getAccountId();
                    Uri uri = Uri.parse(url);
                    try {
                        ApplicationInfo applicationInfo = activity.getPackageManager().getApplicationInfo("com.facebook.katana", 0);
                        if (applicationInfo.enabled) {
                            // http://stackoverflow.com/a/24547437/1048340
                            uri = Uri.parse("fb://facewebmodal/f?href=" + url);
                        }
                    } catch (PackageManager.NameNotFoundException ignored) {
                    }
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                }
            });
        }
    }

    private void loadPhoto(User user, ImageView picture) {
        Picasso.with(activity)
            .load(user.getPhotos().get(0).getSourceUrl())
            .resize(picture.getWidth(), picture.getHeight())
            .error(R.drawable.image_not_found)
            .centerCrop()
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
}
