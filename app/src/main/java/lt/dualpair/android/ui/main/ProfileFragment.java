package lt.dualpair.android.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.TokenProvider;
import lt.dualpair.android.accounts.LoginActivity;
import lt.dualpair.android.accounts.LogoutTask;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.AboutActivity;
import lt.dualpair.android.ui.BaseFragment;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfileFragment extends BaseFragment {

    @Bind(R.id.main_picture) ImageView mainPicture;
    @Bind(R.id.name) TextView name;
    @Bind(R.id.age) TextView age;
    @Bind(R.id.description) TextView description;
    @Bind(R.id.first_sociotype_code) TextView firstSociotypeCode;
    @Bind(R.id.first_sociotype_title) TextView firstSociotypeTitle;
    @Bind(R.id.second_sociotype_code) TextView secondSociotypeCode;
    @Bind(R.id.second_sociotype_title) TextView secondSociotypeTitle;
    @Bind(R.id.accounts) ListView accountsListView;

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
        getActivity().findViewById(R.id.second_sociotype_container).setVisibility(View.GONE);

        accountsListView.setAdapter(new AccountListAdapter(user.getAccounts(), this.getActivity()));
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
