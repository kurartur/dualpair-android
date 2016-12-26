package lt.dualpair.android.ui.main;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.dualpair.android.R;
import lt.dualpair.android.TokenProvider;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.resource.UserAccount;
import lt.dualpair.android.ui.AboutActivity;
import lt.dualpair.android.ui.accounts.AccountType;
import lt.dualpair.android.ui.accounts.AccountTypeAdapter;
import lt.dualpair.android.ui.accounts.EditAccountsActivity;
import lt.dualpair.android.ui.user.AddSociotypeActivity;
import lt.dualpair.android.ui.user.EditPhotosActivity;
import lt.dualpair.android.ui.user.EditUserActivity;
import lt.dualpair.android.utils.ToastUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfileFragment extends MainTabFragment {

    private static final int ADD_SOCIOTYPE_REQ_CODE = 1;
    private static final int EDIT_ACCOUNTS_REQ_CODE = 2;
    private static final int EDIT_PHOTOS_REQ_CODE = 3;
    private static final int EDIT_USER_REQ_CODE = 4;

    @Bind(R.id.main_picture) ImageView mainPicture;
    @Bind(R.id.name) TextView name;
    @Bind(R.id.age) TextView age;
    @Bind(R.id.description) TextView description;

    @Bind(R.id.first_sociotype_info) ImageView firstSociotypeInfo;
    @Bind(R.id.first_sociotype_code) TextView firstSociotypeCode;
    @Bind(R.id.first_sociotype_title) TextView firstSociotypeTitle;
    //@Bind(R.id.second_sociotype_code) TextView secondSociotypeCode;
    //@Bind(R.id.second_sociotype_title) TextView secondSociotypeTitle;

    @Bind(R.id.photos) RecyclerView photosView;
    @Bind(R.id.edit_photos) ImageView editPhotos;

    @Bind(R.id.accounts) GridView accountsGridView;
    @Bind(R.id.edit_sociotypes) ImageView editSociotypes;
    @Bind(R.id.edit_accounts) ImageView editAccounts;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_layout, container, false);
        ButterKnife.bind(this, view);

        editSociotypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(AddSociotypeActivity.createIntent(getActivity()), ADD_SOCIOTYPE_REQ_CODE);
            }
        });

        editAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(EditAccountsActivity.createIntent(getActivity()), EDIT_ACCOUNTS_REQ_CODE);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        load(false);
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
                logout();
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
                    load(true);
                }
                break;
            case EDIT_PHOTOS_REQ_CODE:
            case EDIT_USER_REQ_CODE:
                load(false);
                break;
        }
    }

    private void load(boolean forceUpdate) {
        new UserDataManager(getActivity()).getUser(forceUpdate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .compose(this.<User>bindToLifecycle())
                .subscribe(new EmptySubscriber<User>() {
                @Override
                public void onNext(User user) {
                    render(user);
                }
            });
    }

    private void renderUser(User user) {
        name.setText(user.getName());
        age.setText("(" + Integer.toString(user.getAge()) + ")");
        if (TextUtils.isEmpty(user.getDescription())) {
            description.setText(getResources().getString(R.string.add_description));
            description.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            description.setText(user.getDescription());
        }

        final Photo photo = user.getPhotos().iterator().next();
        mainPicture.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Picasso.with(getActivity())
                        .load(photo.getSourceUrl())
                        .resize(mainPicture.getWidth(), mainPicture.getHeight())
                        .centerCrop()
                        .error(R.drawable.image_not_found)
                        .into(mainPicture);
            }
        });
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
        UserPhotosRecyclerAdapter userPhotosRecyclerAdapter = new UserPhotosRecyclerAdapter(photos);
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

    private void logout() {
        new UserDataManager(getActivity()).logout()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptySubscriber<Void>() {
                    @Override
                    public void onNext(Void v) {
                        TokenProvider.getInstance().storeToken(null);
                        DatabaseHelper.reset(getActivity());
                        AccountUtils.removeAccount(AccountManager.get(getActivity()));
                        Intent newIntent = MainActivity.createIntent(getActivity());
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(newIntent);
                    }
                });
    }

    @Override
    protected String getActionBarTitle() {
        return getResources().getString(R.string.profile);
    }
}
