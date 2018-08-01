package lt.dualpair.android.ui.main;

import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.FullUserSociotype;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserForView;
import lt.dualpair.android.data.local.entity.UserLocation;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.local.entity.UserPurposeOfBeing;
import lt.dualpair.android.data.repository.ResponseRepository;
import lt.dualpair.android.data.repository.UserPrincipalRepository;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.ImageSwipe;
import lt.dualpair.android.ui.UserFriendlyErrorConsumer;
import lt.dualpair.android.utils.LabelUtils;
import lt.dualpair.android.utils.LocationUtil;

public class ReviewFragment extends BaseFragment {

    private static final String TAG = ReviewFragment.class.getName();

    @Bind(R.id.review) View reviewLayout;
    @Bind(R.id.name) TextView name;
    @Bind(R.id.age) TextView age;
    @Bind(R.id.city) TextView city;
    @Bind(R.id.distance) TextView distance;
    @Bind(R.id.photos) ImageSwipe photosView;
    @Bind(R.id.sociotypes)  TextView sociotypes;
    @Bind(R.id.description) TextView description;
    @Bind(R.id.purposes_of_being) TextView purposesOfBeing;
    @Bind(R.id.relationship_status) TextView relationshipStatus;

    private ReviewViewModel viewModel;

    private UserLocation lastPrincipalLocation;
    private UserLocation lastReviewedUserLocation;

    private Long userId;

    private CompositeDisposable disposable = new CompositeDisposable();

    private OnResponseGivenCallback onResponseGivenCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.review_layout, container, false);
        ButterKnife.bind(this, view);

        ButterKnife.findById(view, R.id.yes_button).setOnClickListener(v -> {
            disposable.add(
                viewModel.respondWithYes(userId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> onResponseGivenCallback.onResponseGiven(), new UserFriendlyErrorConsumer(this))
            );
        });
        ButterKnife.findById(view, R.id.no_button).setOnClickListener(v -> {
            disposable.add(
                viewModel.respondWithNo(userId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> onResponseGivenCallback.onResponseGiven(), new UserFriendlyErrorConsumer(this))
            );
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ReviewViewModelFactory(getActivity().getApplication())).get(ReviewViewModel.class);
        subscribeUi();
    }

    private void subscribeUi() {
        viewModel.getLastStoredLocation().observe(this, new Observer<UserLocation>() {
            @Override
            public void onChanged(@Nullable UserLocation userLocation) {
                lastPrincipalLocation = userLocation;
                setLocation(userLocation, lastReviewedUserLocation);
            }
        });
    }

    public void renderReview(UserForView userForView) {
        User opponentUser = userForView.getUser();

        userId = opponentUser.getId();

        reviewLayout.setVisibility(View.VISIBLE);

        lastReviewedUserLocation = userForView.getLastLocation();
        setLocation(lastPrincipalLocation, lastReviewedUserLocation);

        setData(
                opponentUser,
                userForView.getSociotypes(),
                userForView.getUser().getDescription(),
                userForView.getPhotos(),
                userForView.getUser().getRelationshipStatus(),
                userForView.getPurposesOfBeing()
        );
    }

    public void setData(User user,
                        List<FullUserSociotype> userSociotypes,
                        String description,
                        List<UserPhoto> photos,
                        lt.dualpair.android.data.local.entity.RelationshipStatus relationshipStatus,
                        List<UserPurposeOfBeing> purposesOfBeing) {
        name.setText(user.getName());
        age.setText(getString(R.string.review_age, user.getAge()));
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (FullUserSociotype sociotype : userSociotypes) {
            sb.append(prefix);
            prefix = ", ";
            String code = sociotype.getSociotype().getCode1();
            int titleId = getResources().getIdentifier(code.toLowerCase() + "_title", "string", getContext().getPackageName());
            sb.append(getContext().getString(titleId) + " (" + sociotype.getSociotype().getCode1() + ")");
        }
        sociotypes.setText(sb);
        this.description.setText(description);
        photosView.setPhotos(photos);
        setRelationshipStatus(relationshipStatus);
        setPurposesOfBeing(purposesOfBeing);
    }

    public void setLocation(UserLocation principalLocation, UserLocation opponentLocation) {
        if (opponentLocation != null) {
            city.setText(getString(R.string.review_city, opponentLocation.getCity()));
        }
        if (principalLocation != null && opponentLocation != null) {
            Double distance = LocationUtil.calculateDistance(
                    principalLocation.getLatitude(),
                    principalLocation.getLongitude(),
                    opponentLocation.getLatitude(),
                    opponentLocation.getLongitude()
            );
            this.distance.setText(getString(R.string.review_distance, distance.intValue() / 1000));
        } else {
            this.distance.setText("");
        }
    }

    private void setPurposesOfBeing(List<UserPurposeOfBeing> purposesOfBeing) {
        this.purposesOfBeing.setVisibility(View.GONE);
        if (!purposesOfBeing.isEmpty()) {
            this.purposesOfBeing.setText(getResources().getString(R.string.i_am_here_to, getPurposesText(purposesOfBeing)));
            this.purposesOfBeing.setVisibility(View.VISIBLE);
        }
    }

    private void setRelationshipStatus(lt.dualpair.android.data.local.entity.RelationshipStatus relationshipStatus) {
        this.relationshipStatus.setVisibility(View.GONE);
        if (relationshipStatus != lt.dualpair.android.data.local.entity.RelationshipStatus.NONE) {
            this.relationshipStatus.setText(LabelUtils.getRelationshipStatusLabel(getContext(), relationshipStatus));
            this.relationshipStatus.setVisibility(View.VISIBLE);
        }
    }

    private String getPurposesText(List<UserPurposeOfBeing> purposesOfBeing) {
        String text = "";
        String prefix = "";
        for (UserPurposeOfBeing purposeOfBeing : purposesOfBeing) {
            text += prefix + LabelUtils.getPurposeOfBeingLabel(getContext(), purposeOfBeing.getPurpose());
            prefix = ", ";
        }
        return text.toLowerCase();
    }

    public void setOnResponseGivenCallback(OnResponseGivenCallback onResponseGivenCallback) {
        this.onResponseGivenCallback = onResponseGivenCallback;
    }

    public static ReviewFragment newInstance(OnResponseGivenCallback onResponseGiven) {
        ReviewFragment fragment = new ReviewFragment();
        fragment.setOnResponseGivenCallback(onResponseGiven);
        return fragment;
    }

    public interface OnResponseGivenCallback {
        void onResponseGiven();
    }

    private static class ReviewViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

        private Application application;

        public ReviewViewModelFactory(@NonNull Application application) {
            super(application);
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ReviewViewModel.class)) {
                UserPrincipalRepository userPrincipalRepository = new UserPrincipalRepository(application);
                ResponseRepository responseRepository = new ResponseRepository(application);
                return (T) new ReviewViewModel(userPrincipalRepository, responseRepository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
