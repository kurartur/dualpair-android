package lt.dualpair.android.ui.match;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Location;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.main.UserPhotosAdapter;

public class OpponentUserView {

    private Context context;

    ImageView[] dotImages;

    @Bind(R.id.photo_pager) ViewPager photoPager;
    @Bind(R.id.photo_dots) LinearLayout photoDots;
    @Bind(R.id.name_surname) TextView name;
    @Bind(R.id.age) TextView age;
    @Bind(R.id.location) TextView location;
    @Bind(R.id.sociotypes) TextView sociotypes;
    @Bind(R.id.description) TextView description;

    public OpponentUserView(final Context context, final View view) {
        this.context = context;

        ButterKnife.bind(this, view);

        photoPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotImages.length; i++) {
                    dotImages[i].setImageDrawable(context.getResources().getDrawable(R.drawable.non_selected_item_dot));
                }
                dotImages[position].setImageDrawable(context.getResources().getDrawable(R.drawable.selected_item_dot));
            }
        });
    }

    public void render(User user) {
        name.setText(user.getName());
        age.setText(Integer.toString(user.getAge()));
        Location firstLocation = user.getFirstLocation();
        if (firstLocation != null) {
            location.setText(firstLocation.getCity());
        } else {
            location.setText("Unknown");
        }
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (Sociotype sociotype : user.getSociotypes()) {
            sb.append(prefix);
            prefix = ", ";
            sb.append(sociotype.getCode1());
        }
        sociotypes.setText(sb);
        description.setText(user.getDescription());
        initPhotos(user.getPhotos());
    }

    private void initPhotos(List<Photo> photos) {
        photoPager.setAdapter(new UserPhotosAdapter(context, photos));
        photoDots.removeAllViews();
        dotImages = new ImageView[photos.size()];
        for (int i = 0; i < photos.size(); i++) {
            dotImages[i] = new ImageView(context);
            if (i == 0) {
                dotImages[i].setImageDrawable(context.getResources().getDrawable(R.drawable.selected_item_dot));
            } else {
                dotImages[i].setImageDrawable(context.getResources().getDrawable(R.drawable.non_selected_item_dot));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(7, 0, 7, 0);
            photoDots.addView(dotImages[i], params);
        }
    }

}
