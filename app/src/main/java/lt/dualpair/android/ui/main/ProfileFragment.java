package lt.dualpair.android.ui.main;

import android.app.Activity;
import android.app.DialogFragment;
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
import android.webkit.CookieManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.TokenProvider;
import lt.dualpair.android.accounts.LoginActivity;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.resource.UserAccount;
import lt.dualpair.android.data.task.LogoutTask;
import lt.dualpair.android.ui.AboutActivity;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.accounts.EditAccountsActivity;
import lt.dualpair.android.ui.user.AddSociotypeActivity;
import lt.dualpair.android.ui.user.EditUserDialog;
import lt.dualpair.android.utils.ToastUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfileFragment extends BaseFragment {

    private static final int ADD_SOCIOTYPE_REQ_CODE = 1;
    private static final int EDIT_ACCOUNTS_REQ_CODE = 2;

    @Bind(R.id.user) LinearLayout user;
    @Bind(R.id.main_picture) ImageView mainPicture;
    @Bind(R.id.name) TextView name;
    @Bind(R.id.age) TextView age;
    @Bind(R.id.description) TextView description;
    @Bind(R.id.first_sociotype_code) TextView firstSociotypeCode;
    @Bind(R.id.first_sociotype_title) TextView firstSociotypeTitle;
    //@Bind(R.id.second_sociotype_code) TextView secondSociotypeCode;
    //@Bind(R.id.second_sociotype_title) TextView secondSociotypeTitle;
    @Bind(R.id.photos) RecyclerView photos;
    @Bind(R.id.edit_photos) ImageView editPhotos;
    @Bind(R.id.accounts) GridView accountsGridView;
    @Bind(R.id.edit_sociotypes) ImageView editSociotypes;
    @Bind(R.id.edit_accounts) ImageView editAccounts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_layout, container, false);
        ButterKnife.bind(this, view);

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = (DialogFragment) EditUserDialog.instantiate(getActivity(), "lt.dualpair.android.ui.user.EditUserDialog");
                dialog.show(getActivity().getFragmentManager(), "EditUserDialog");
            }
        });

        editSociotypes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(AddSociotypeActivity.createIntent(getActivity()), ADD_SOCIOTYPE_REQ_CODE);
            }
        });

        editPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.show(getActivity(), "Should open photos editing"); // TODO
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
        load();
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
                    load();
                }
                break;
        }
    }

    private void load() {
        new UserDataManager(getActivity()).getUser()
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

    private void render(User user) {
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

        Sociotype firstSociotype = user.getSociotypes().iterator().next();
        firstSociotypeCode.setText(firstSociotype.getCode1() + " (" + firstSociotype.getCode2() + ")");
        firstSociotypeTitle.setText(getResources().getString(getResources().getIdentifier(firstSociotype.getCode1().toLowerCase() + "_title", "string", getActivity().getPackageName())));
        //getActivity().findViewById(R.id.second_sociotype_container).setVisibility(View.GONE);

        UserPhotosRecyclerAdapter userPhotosRecyclerAdapter = new UserPhotosRecyclerAdapter(user.getPhotos());
        photos.setAdapter(userPhotosRecyclerAdapter);

        AccountGridAdapter accountGridAdapter = new AccountGridAdapter(getActivity());
        for (UserAccount userAccount : user.getAccounts()) {
            accountGridAdapter.append(userAccount);
        }
        accountsGridView.setAdapter(accountGridAdapter);
    }

    private void logout() {
        new LogoutTask(getActivity()).execute(new EmptySubscriber<Void>() {
            @Override
            public void onNext(Void v) {
                CookieManager.getInstance().removeAllCookie();
                TokenProvider.getInstance().storeToken(null);
                Intent newIntent = new Intent(getActivity(), LoginActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newIntent);
            }
        });
    }
}
