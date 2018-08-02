package lt.dualpair.android.ui.user;


import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.FullUserSociotype;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.local.entity.UserPurposeOfBeing;
import lt.dualpair.android.ui.ImageSwipe;
import lt.dualpair.android.utils.LabelUtils;
import lt.dualpair.android.utils.LocationUtil;
import lt.dualpair.android.utils.SocialUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class OpponentUserViewHolder {

    private Context context;

    @Bind(R.id.name) TextView name;
    @Bind(R.id.age) TextView age;
    @Bind(R.id.city) TextView city;
    @Bind(R.id.distance) TextView distance;
    @Bind(R.id.photos)      ImageSwipe photosView;
    @Bind(R.id.sociotypes)  TextView sociotypes;
    @Bind(R.id.description) TextView description;
    @Bind(R.id.purposes_of_being) TextView purposesOfBeing;
    @Bind(R.id.relationship_status) TextView relationshipStatus;
    @Bind(R.id.social_buttons) RecyclerView socialButtons;
    @Bind(R.id.divider) View divider;

    public OpponentUserViewHolder(Context context, View view) {
        this.context = context;
        ButterKnife.bind(this, view);
    }

    private Resources getResources() {
        return context.getResources();
    }

    public void setData(User user,
                        List<FullUserSociotype> userSociotypes,
                        String description,
                        List<UserPhoto> photos,
                        lt.dualpair.android.data.local.entity.RelationshipStatus relationshipStatus,
                        List<UserPurposeOfBeing> purposesOfBeing,
                        List<UserAccount> userAccounts) {
        name.setText(user.getName());
        age.setText(context.getString(R.string.review_age, user.getAge()));
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (FullUserSociotype sociotype : userSociotypes) {
            sb.append(prefix);
            prefix = ", ";
            String code = sociotype.getSociotype().getCode1();
            int titleId = getResources().getIdentifier(code.toLowerCase() + "_title", "string", context.getPackageName());
            sb.append(context.getString(titleId) + " (" + sociotype.getSociotype().getCode1() + ")");
        }
        sociotypes.setText(sb);
        if (description != null && description.length() > 0) {
            this.description.setText(description);
            this.divider.setVisibility(VISIBLE);
        } else {
            this.divider.setVisibility(GONE);
        }
        photosView.setPhotos(photos);
        setRelationshipStatus(relationshipStatus);
        setPurposesOfBeing(purposesOfBeing, user.getGender());

        socialButtons.setAdapter(new SocialButtonsRecyclerAdapter(userAccounts,
                userAccount -> SocialUtils.openUserAccount(context, userAccount)));
    }

    public void setLocation(UserLocation principalLocation, UserLocation opponentLocation) {
        if (opponentLocation != null && opponentLocation.getCity() != null) {
            city.setText(context.getString(R.string.review_city, opponentLocation.getCity()));
        } else {
            city.setText("");
        }
        if (principalLocation != null && opponentLocation != null && opponentLocation.getCity() != null) {
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

    private void setPurposesOfBeing(List<UserPurposeOfBeing> purposesOfBeing, String gender) {
        this.purposesOfBeing.setVisibility(GONE);
        if (!purposesOfBeing.isEmpty()) {
            this.purposesOfBeing.setText(getResources().getString(R.string.is_here_to, LabelUtils.getHeOrShe(context, gender), getPurposesText(purposesOfBeing)));
            this.purposesOfBeing.setVisibility(VISIBLE);
        }
    }

    private void setRelationshipStatus(lt.dualpair.android.data.local.entity.RelationshipStatus relationshipStatus) {
        this.relationshipStatus.setVisibility(GONE);
        if (relationshipStatus != lt.dualpair.android.data.local.entity.RelationshipStatus.NONE) {
            this.relationshipStatus.setText(LabelUtils.getRelationshipStatusLabel(context, relationshipStatus));
            this.relationshipStatus.setVisibility(VISIBLE);
        }
    }

    private String getPurposesText(List<UserPurposeOfBeing> purposesOfBeing) {
        String text = "";
        String prefix = "";
        for (UserPurposeOfBeing purposeOfBeing : purposesOfBeing) {
            text += prefix + LabelUtils.getPurposeOfBeingLabel(context, purposeOfBeing.getPurpose());
            prefix = ", ";
        }
        return text.toLowerCase();
    }

}
