package lt.dualpair.android.ui.user;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.ImageSwipe;

public class OpponentUserView extends LinearLayout {

    @Bind(R.id.photos)      ImageSwipe photosView;
    @Bind(R.id.sociotypes)  TextView sociotypes;
    @Bind(R.id.description) TextView description;
    @Bind(R.id.photos_wrapper) RelativeLayout photosWrapper;

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
    }

    public View setPhotoOverlay(int layoutId) {
        return LayoutInflater.from(getContext()).inflate(layoutId, photosWrapper, true);
    }

}
