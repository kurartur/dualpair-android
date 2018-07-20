package lt.dualpair.android.ui.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.FullUserSociotype;
import lt.dualpair.android.data.local.entity.RelationshipStatus;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.local.entity.UserPurposeOfBeing;
import lt.dualpair.android.ui.AboutActivity;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.CustomActionBarFragment;
import lt.dualpair.android.ui.accounts.AccountType;
import lt.dualpair.android.ui.accounts.AccountTypeAdapter;
import lt.dualpair.android.ui.accounts.EditAccountsActivity;
import lt.dualpair.android.ui.socionics.SetSociotypeActivity;
import lt.dualpair.android.ui.socionics.ViewSociotypeActivity;
import lt.dualpair.android.ui.splash.SplashActivity;
import lt.dualpair.android.ui.user.EditPhotosActivity;
import lt.dualpair.android.ui.user.EditUserActivity;
import lt.dualpair.android.utils.DrawableUtils;
import lt.dualpair.android.utils.LabelUtils;
import lt.dualpair.android.utils.ToastUtils;

public class ProfileFragment extends BaseFragment implements CustomActionBarFragment {

    private static final String LOG_TAG = "ProfileFragment";

    @Bind(R.id.main_picture) ImageView mainPicture;
    @Bind(R.id.name) TextView name;
    @Bind(R.id.age) TextView age;
    @Bind(R.id.description) TextView description;
    @Bind(R.id.relationship_status) TextView relationshipStatus;
    @Bind(R.id.purposes_of_being) TextView purposeOfBeing;

    @Bind(R.id.first_sociotype_info) ImageView firstSociotypeInfo;
    @Bind(R.id.first_sociotype_code) TextView firstSociotypeCode;
    @Bind(R.id.first_sociotype_title) TextView firstSociotypeTitle;
    //@Bind(R.id.second_sociotype_code) TextView secondSociotypeCode;
    //@Bind(R.id.second_sociotype_title) TextView secondSociotypeTitle;

    @Bind(R.id.photos) RecyclerView photosView;
    @Bind(R.id.edit_photos) ImageView editPhotos;

    @Bind(R.id.accounts) GridView accountsGridView;

    private ProfileViewModel viewModel;

    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @OnClick(R.id.user_layout) void onUserLayoutClick(View v) {
        startActivity(EditUserActivity.createIntent(getActivity()));
    }

    @OnClick(R.id.photos_section) void onPhotosSectionClick(View v) {
        startActivity(EditPhotosActivity.createIntent(getActivity()));
    }

    @OnClick(R.id.sociotypes_header) void onSociotypesHeaderClick(View v) {
        startActivity(SetSociotypeActivity.createIntent(getActivity(), false));
    }

    @OnClick(R.id.accounts_header) void onAccountsHeaderClick(View v) {
        startActivity(EditAccountsActivity.createIntent(getActivity()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ProfileViewModel.Factory(getActivity().getApplication())).get(ProfileViewModel.class);
        subscribeUi();
        refresh(false);
    }

    private void refresh(boolean force) {
        disposable.add(
                viewModel.refresh(force)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {}, throwable -> ToastUtils.show(getActivity(), throwable.getMessage())));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }

    private void subscribeUi() {
        viewModel.getUserLive().observe(this, this::renderUser);
        viewModel.getUserSociotypesLive().observe(this, this::renderSociotypes);
        viewModel.getUserAccountsLive().observe(this, this::renderAccounts);
        viewModel.getUserPhotosLive().observe(this, photos -> {
           renderPhotos(photos);
           renderMainPhoto(photos.get(0));
        });
        viewModel.getPurposesOfBeingLive().observe(this, this::renderPurposesOfBeing);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        DrawableUtils.setActionBarIconColorFilter(getContext(), menu.findItem(R.id.invite_menu_item).getIcon());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.invite_menu_item:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    String strShareMessage = getString(R.string.recommendation_text) + "\n\n";
                    strShareMessage = strShareMessage + "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName();
                    i.putExtra(Intent.EXTRA_TEXT, strShareMessage);
                    startActivity(Intent.createChooser(i, getString(R.string.share_via)));
                } catch(Exception e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    ToastUtils.show(getContext(), getString(R.string.unexpected_error));
                }
                break;
            case R.id.about_menu_item:
                startActivity(AboutActivity.createIntent(this.getActivity()));
                break;
            case R.id.logout_menu_item:
                viewModel.logout()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                            viewModel.getUserLive().removeObservers(ProfileFragment.this);
                            viewModel.getUserSociotypesLive().removeObservers(ProfileFragment.this);
                            viewModel.getUserAccountsLive().removeObservers(ProfileFragment.this);
                            viewModel.getUserPhotosLive().removeObservers(ProfileFragment.this);
                            viewModel.getPurposesOfBeingLive().removeObservers(ProfileFragment.this);
                        })
                        .subscribe(() -> {
                            Intent newIntent = SplashActivity.createIntent(getActivity());
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(newIntent);
                        });
                break;
        }
        return false;
    }

    private void renderUser(User user) {
        name.setText(user.getName());
        age.setText(user.getAge().toString());
        renderDescription(user);
        renderRelationshipStatus(user);
        ((ScrollView)getView()).fullScroll(ScrollView.FOCUS_UP);
    }

    private void renderMainPhoto(UserPhoto userPhoto) {
        Picasso.with(getActivity())
                .load(userPhoto.getSourceLink())
                .error(R.drawable.image_not_found)
                .into(mainPicture);
    }

    private void renderDescription(User user) {
        if (TextUtils.isEmpty(user.getDescription())) {
            description.setText(getResources().getString(R.string.add_description));
            description.setTextColor(getNotProvidedFieldsColor());
        } else {
            description.setText(user.getDescription());
            description.setTextColor(getNormalTextColor());
        }
    }

    private void renderRelationshipStatus(User user) {
        if (user.getRelationshipStatus() == RelationshipStatus.NONE) {
            relationshipStatus.setText(getResources().getString(R.string.provide_relationship_status));
            relationshipStatus.setTextColor(getNotProvidedFieldsColor());
        } else {
            relationshipStatus.setText(LabelUtils.getRelationshipStatusLabel(getContext(), user.getRelationshipStatus()));
            relationshipStatus.setTextColor(getNormalTextColor());
        }
    }

    private void renderPurposesOfBeing(List<UserPurposeOfBeing> purposesOfBeing) {
        if (purposesOfBeing.isEmpty()) {
            purposeOfBeing.setText(getResources().getString(R.string.provide_purpose_of_beings));
            purposeOfBeing.setTextColor(getNotProvidedFieldsColor());
        } else {
            String purposeOfBeingText = "";
            String prefix = "";
            for (UserPurposeOfBeing purposeOfBeing : purposesOfBeing) {
                purposeOfBeingText += prefix + LabelUtils.getPurposeOfBeingLabel(getContext(), purposeOfBeing.getPurpose());
                prefix = ", ";
            }
            purposeOfBeing.setText(purposeOfBeingText);
            purposeOfBeing.setTextColor(getNormalTextColor());
        }
    }

    private int getNotProvidedFieldsColor() {
        return ContextCompat.getColor(getContext(), android.R.color.darker_gray);
    }

    private int getNormalTextColor() {
        return ContextCompat.getColor(getContext(), R.color.colorPrimaryText);
    }

    private void renderAccounts(List<UserAccount> userAccounts) {
        AccountGridAdapter accountGridAdapter = new AccountGridAdapter(getActivity(), userAccounts, new AccountTypeAdapter.OnAccountTypeClickListener() {
            @Override
            public void onClick(AccountType accountType) {
                startActivity(EditAccountsActivity.createIntent(getActivity(), accountType));
            }
        });
        accountsGridView.setAdapter(accountGridAdapter);
    }

    private void renderPhotos(List<UserPhoto> photos) {
        UserPhotosRecyclerAdapter userPhotosRecyclerAdapter = new UserPhotosRecyclerAdapter(photos, new UserPhotosRecyclerAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(UserPhoto photo) {
                onPhotosSectionClick(null);
            }
        });
        photosView.setAdapter(userPhotosRecyclerAdapter);
    }

    private void renderSociotypes(List<FullUserSociotype> sociotypes) {
        if (sociotypes.isEmpty()) {
            firstSociotypeInfo.setVisibility(View.INVISIBLE);
            firstSociotypeCode.setText(":(");
            firstSociotypeTitle.setText(getResources().getString(R.string.no_sociotypes));
        } else {
            firstSociotypeInfo.setVisibility(View.VISIBLE);
            Sociotype firstSociotype = sociotypes.get(0).getSociotype();
            firstSociotypeInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(ViewSociotypeActivity.createIntent(getActivity(), firstSociotype));
                }
            });
            firstSociotypeCode.setText(firstSociotype.getCode1() + " (" + firstSociotype.getCode2() + ")");
            firstSociotypeTitle.setText(getResources().getString(getResources().getIdentifier(firstSociotype.getCode1().toLowerCase() + "_title", "string", getActivity().getPackageName())));
        }
    }

    @Override
    public String getActionBarTitle() {
        return getResources().getString(R.string.profile);
    }

    @Override
    public View getActionBarView() {
        return null;
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }
}
