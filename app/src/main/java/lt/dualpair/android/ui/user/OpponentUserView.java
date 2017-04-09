package lt.dualpair.android.ui.user;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.PurposeOfBeing;
import lt.dualpair.android.data.resource.RelationshipStatus;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.ImageSwipe;
import lt.dualpair.android.utils.LabelUtils;

public class OpponentUserView extends LinearLayout {

    @Bind(R.id.photos)      ImageSwipe photosView;
    @Bind(R.id.sociotypes)  TextView sociotypes;
    @Bind(R.id.description) TextView description;
    @Bind(R.id.photos_wrapper) RelativeLayout photosWrapper;
    @Bind(R.id.purposes_of_being) TextView purposesOfBeing;
    @Bind(R.id.relationship_status) TextView relationshipStatus;

    public OpponentUserView(Context context) {
        super(context);
        initView(context);
    }

    public OpponentUserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public OpponentUserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public OpponentUserView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    protected void initView(Context context) {
        LayoutInflater  mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.opponent_user_view, this, true);
        ButterKnife.bind(this);
    }

    public void setUser(User opponentUser) {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (Sociotype sociotype : opponentUser.getSociotypes()) {
            sb.append(prefix);
            prefix = ", ";
            String code = sociotype.getCode1();
            int titleId = getResources().getIdentifier(code.toLowerCase() + "_title", "string", getContext().getPackageName());
            sb.append(getContext().getString(titleId) + " (" + sociotype.getCode1() + ")");
        }
        sociotypes.setText(sb);
        description.setText(opponentUser.getDescription());
        photosView.setPhotos(opponentUser.getPhotos());
        setRelationshipStatus(opponentUser);
        setPurposesOfBeing(opponentUser);
    }

    private void setPurposesOfBeing(User opponentUser) {
        purposesOfBeing.setVisibility(GONE);
        if (!opponentUser.getPurposesOfBeing().isEmpty()) {
            purposesOfBeing.setText(getResources().getString(R.string.i_am_here_to, getPurposesText(opponentUser.getPurposesOfBeing())));
            purposesOfBeing.setVisibility(VISIBLE);
        }
    }

    private void setRelationshipStatus(User opponentUser) {
        relationshipStatus.setVisibility(GONE);
        if (opponentUser.getRelationshipStatus() != RelationshipStatus.NONE) {
            relationshipStatus.setText(LabelUtils.getRelationshipStatusLabel(getContext(), opponentUser.getRelationshipStatus()));
            relationshipStatus.setVisibility(VISIBLE);
        }
    }

    private String getPurposesText(Set<PurposeOfBeing> purposesOfBeing) {
        String text = "";
        String prefix = "";
        for (PurposeOfBeing purposeOfBeing : purposesOfBeing) {
            text += prefix + LabelUtils.getPurposeOfBeingLabel(getContext(), purposeOfBeing);
            prefix = ", ";
        }
        return text.toLowerCase();
    }

    public View setPhotoOverlay(int layoutId) {
        return LayoutInflater.from(getContext()).inflate(layoutId, photosWrapper, true);
    }

}
