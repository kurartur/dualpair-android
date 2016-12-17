package lt.dualpair.android.ui.match;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Location;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.ImageSwipe;

public class OpponentUserView {

    private Context context;

    @Bind(R.id.photos) ImageSwipe photosView;

    @Bind(R.id.name_surname) TextView name;
    @Bind(R.id.age) TextView age;
    @Bind(R.id.location) TextView location;
    @Bind(R.id.sociotypes) TextView sociotypes;
    @Bind(R.id.description) TextView description;

    public OpponentUserView(final Context context, final View view) {
        this.context = context;
        ButterKnife.bind(this, view);
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
        photosView.setPhotos(user.getPhotos());
    }

}
