package lt.dualpair.android.ui.user;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.utils.LocationUtil;

public class UserViewActionBarViewHolder {

    public View actionBarView;
    private Context context;

    @Bind(R.id.name)
    public TextView name;
    @Bind(R.id.age)
    public TextView age;
    @Bind(R.id.city)
    public TextView city;
    @Bind(R.id.distance)
    public TextView distance;

    public UserViewActionBarViewHolder(View actionBarView, Context context) {
        this.actionBarView = actionBarView;
        this.context = context;
        ButterKnife.bind(this, actionBarView);
    }

    public void setUserData(User user) {
        name.setText(user.getName());
        age.setText(context.getString(R.string.review_age, user.getAge()));
    }

    public void setLocation(UserLocation principalLocation, UserLocation opponentLocation) {
        if (opponentLocation != null) {
            city.setText(context.getString(R.string.review_city, opponentLocation.getCity()));
        }
        if (principalLocation != null && opponentLocation != null) {
            Double distance = LocationUtil.calculateDistance(
                    principalLocation.getLatitude(),
                    principalLocation.getLongitude(),
                    opponentLocation.getLatitude(),
                    opponentLocation.getLongitude()
            );
            this.distance.setText(context.getString(R.string.review_distance, distance.intValue() / 1000));
        } else {
            this.distance.setText("");
        }
    }

}
