package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.PurposeOfBeing;
import lt.dualpair.android.data.resource.RelationshipStatus;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.resource.UserAccount;
import lt.dualpair.android.ui.AboutActivity;
import lt.dualpair.android.ui.accounts.AccountType;
import lt.dualpair.android.ui.accounts.AccountTypeAdapter;
import lt.dualpair.android.ui.accounts.EditAccountsActivity;
import lt.dualpair.android.ui.splash.SplashActivity;
import lt.dualpair.android.ui.user.AddSociotypeActivity;
import lt.dualpair.android.ui.user.EditPhotosActivity;
import lt.dualpair.android.ui.user.EditUserActivity;
import lt.dualpair.android.utils.LabelUtils;
import lt.dualpair.android.utils.ToastUtils;

public class ProfileFragment extends MainTabFragment {

    private static final String LOG_TAG = "ProfileFragment";

    private static final int ADD_SOCIOTYPE_REQ_CODE = 1;
    private static final int EDIT_ACCOUNTS_REQ_CODE = 2;
    private static final int EDIT_PHOTOS_REQ_CODE = 3;
    private static final int EDIT_USER_REQ_CODE = 4;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @OnClick(R.id.user_layout) void onUserLayoutClick(View v) {
        startActivityForResult(EditUserActivity.createIntent(getActivity()), EDIT_USER_REQ_CODE);
    }

    @OnClick(R.id.photos_section) void onPhotosSectionClick(View v) {
        startActivityForResult(EditPhotosActivity.createIntent(getActivity()), EDIT_PHOTOS_REQ_CODE);
    }

    @OnClick(R.id.sociotypes_header) void onSociotypesHeaderClick(View v) {
        startActivityForResult(AddSociotypeActivity.createIntent(getActivity(), true), ADD_SOCIOTYPE_REQ_CODE);
    }

    @OnClick(R.id.accounts_header) void onAccountsHeaderClick(View v) {
        startActivityForResult(EditAccountsActivity.createIntent(getActivity()), EDIT_ACCOUNTS_REQ_CODE);
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
    }

    private void subscribeUi() {
        viewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                render(user);
            }
        });
        viewModel.isLoggedOut().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                Intent newIntent = SplashActivity.createIntent(getActivity());
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newIntent);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_menu_item:
                startActivity(AboutActivity.createIntent(this.getActivity()));
                break;
            case R.id.logout_menu_item:
                viewModel.logout();
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_SOCIOTYPE_REQ_CODE:
            case EDIT_ACCOUNTS_REQ_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.refresh();
                }
                break;
            case EDIT_PHOTOS_REQ_CODE:
            case EDIT_USER_REQ_CODE:
                viewModel.refresh();
                break;
        }
    }

    private void renderUser(User user) {
        name.setText(user.getName());
        age.setText(user.getAge().toString());
        renderDescription(user);
        renderRelationshipStatus(user);
        renderPurposesOfBeing(user);
        renderMainPhoto(user);
        ((ScrollView)getView()).fullScroll(ScrollView.FOCUS_UP);
    }

    private void renderMainPhoto(User user) {
        final Photo photo = user.getPhotos().iterator().next();
        Picasso.with(getActivity())
                .load(photo.getSourceUrl())
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

    private void renderPurposesOfBeing(User user) {
        if (user.getPurposesOfBeing().isEmpty()) {
            purposeOfBeing.setText(getResources().getString(R.string.provide_purpose_of_beings));
            purposeOfBeing.setTextColor(getNotProvidedFieldsColor());
        } else {
            String purposeOfBeingText = "";
            String prefix = "";
            for (PurposeOfBeing purposeOfBeing : user.getPurposesOfBeing()) {
                purposeOfBeingText += prefix + LabelUtils.getPurposeOfBeingLabel(getContext(), purposeOfBeing);
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
                startActivityForResult(EditAccountsActivity.createIntent(getActivity(), accountType), EDIT_ACCOUNTS_REQ_CODE);
            }
        });
        accountsGridView.setAdapter(accountGridAdapter);
    }

    private void renderPhotos(List<Photo> photos) {
        UserPhotosRecyclerAdapter userPhotosRecyclerAdapter = new UserPhotosRecyclerAdapter(photos, new UserPhotosRecyclerAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(Photo photo) {
                onPhotosSectionClick(null);
            }
        });
        photosView.setAdapter(userPhotosRecyclerAdapter);
    }

    private void renderSociotypes(Set<Sociotype> sociotypes) {
        if (sociotypes.isEmpty()) {
            firstSociotypeInfo.setVisibility(View.INVISIBLE);
            firstSociotypeCode.setText(":(");
            firstSociotypeTitle.setText(getResources().getString(R.string.no_sociotypes));
        } else {
            firstSociotypeInfo.setVisibility(View.VISIBLE);
            Sociotype firstSociotype = sociotypes.iterator().next();
            firstSociotypeInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.show(getActivity(), "Info activity...");
                }
            });
            firstSociotypeCode.setText(firstSociotype.getCode1() + " (" + firstSociotype.getCode2() + ")");
            firstSociotypeTitle.setText(getResources().getString(getResources().getIdentifier(firstSociotype.getCode1().toLowerCase() + "_title", "string", getActivity().getPackageName())));
        }
    }

    private void render(User user) {
        renderUser(user);
        renderSociotypes(user.getSociotypes());
        renderPhotos(user.getPhotos());
        renderAccounts(user.getAccounts());
    }

    @Override
    protected String getActionBarTitle() {
        return getResources().getString(R.string.profile);
    }
}
