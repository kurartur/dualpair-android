package lt.dualpair.android.ui.main;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.TokenProvider;
import lt.dualpair.android.accounts.LoginActivity;
import lt.dualpair.android.accounts.LogoutTask;
import lt.dualpair.android.resource.Photo;
import lt.dualpair.android.resource.Sociotype;
import lt.dualpair.android.resource.User;
import lt.dualpair.android.rx.EmptySubscriber;
import lt.dualpair.android.ui.AboutActivity;
import lt.dualpair.android.ui.user.AddSociotypeActivity;

public class ProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDIT_FIRST_SOCIOTYPE_CODE = 1;
    private static final int USER_LOADER = 1;
    private static final int SOCIOTYPE_LOADER = 2;
    private static final int PHOTO_LOADER = 3;

    @Bind(R.id.main_picture) ImageView mainPicture;
    @Bind(R.id.name) TextView name;
    @Bind(R.id.age) TextView age;
    @Bind(R.id.description) TextView description;
    @Bind(R.id.first_sociotype_code) TextView firstSociotypeCode;
    @Bind(R.id.first_sociotype_title) TextView firstSociotypeTitle;
    @Bind(R.id.second_sociotype_code) TextView secondSociotypeCode;
    @Bind(R.id.second_sociotype_title) TextView secondSociotypeTitle;
    @Bind(R.id.edit_first_sociotype) Button editFirstSociotype;

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
        editFirstSociotype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(AddSociotypeActivity.createIntent(getActivity()), EDIT_FIRST_SOCIOTYPE_CODE);
            }
        });
        load();
        return view;
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
        LoaderManager lm = getLoaderManager();
        lm.initLoader(USER_LOADER, null, this);
        lm.initLoader(SOCIOTYPE_LOADER, null, this);
        lm.initLoader(PHOTO_LOADER, null, this);
    }

    private void render(User user) {
        name.setText(user.getName());
        age.setText(Integer.toString(user.getAge()));
        if (TextUtils.isEmpty(user.getDescription())) {
            description.setText(getResources().getString(R.string.add_description));
            description.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            description.setText(user.getDescription());
        }
    }

    private void render(Sociotype firstSociotype) {
        firstSociotypeCode.setText(firstSociotype.getCode1() + " (" + firstSociotype.getCode2() + ")");
        firstSociotypeTitle.setText(getResources().getString(getResources().getIdentifier(firstSociotype.getCode1().toLowerCase() + "_title", "string", getActivity().getPackageName())));
        getActivity().findViewById(R.id.second_sociotype_container).setVisibility(View.GONE);
    }

    private void render(Photo photo, ImageView picture) {
        Picasso.with(getActivity())
                .load(photo.getSourceUrl())
                .resize(picture.getWidth(), picture.getHeight())
                .centerCrop()
                .error(R.drawable.image_not_found)
                .into(picture);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case USER_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        lt.dualpair.android.data.provider.user.User.UserColumns.USER_URI,        // Table to query
                        null,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            case SOCIOTYPE_LOADER:
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        lt.dualpair.android.data.provider.user.User.SociotypeColumns.SOCIOTYPES_URI,        // Table to query
                        null,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            case PHOTO_LOADER:
                return new CursorLoader(
                        getActivity(),   // Parent activity context
                        lt.dualpair.android.data.provider.user.User.PhotoColumns.PHOTOS_URI,        // Table to query
                        null,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case USER_LOADER:
                if (data.moveToNext()) {
                    render(createUser(data));
                }
                break;
            case SOCIOTYPE_LOADER:
                if (data.moveToNext()) {
                    Sociotype firstSociotype = createSociotype(data);
                    render(firstSociotype);
                }
                break;
            case PHOTO_LOADER:
                if (data.moveToNext()) {
                    Photo photo = createPhoto(data);
                    render(photo, mainPicture);
                }
                break;
        }
    }

    private User createUser(Cursor cursor) {
        User user = new User();
        user.setName(cursor.getString(cursor.getColumnIndex(lt.dualpair.android.data.provider.user.User.UserColumns.NAME)));
        user.setAge(cursor.getInt(cursor.getColumnIndex(lt.dualpair.android.data.provider.user.User.UserColumns.AGE)));
        user.setDescription(cursor.getString(cursor.getColumnIndex(lt.dualpair.android.data.provider.user.User.UserColumns.DESCRIPTION)));
        return user;
    }

    private Sociotype createSociotype(Cursor cursor) {
        Sociotype sociotype = new Sociotype();
        sociotype.setCode1(cursor.getString(cursor.getColumnIndex(lt.dualpair.android.data.provider.user.User.SociotypeColumns.CODE_1)));
        sociotype.setCode2(cursor.getString(cursor.getColumnIndex(lt.dualpair.android.data.provider.user.User.SociotypeColumns.CODE_2)));
        return sociotype;
    }

    private Photo createPhoto(Cursor cursor) {
        Photo photo = new Photo();
        photo.setSourceUrl(cursor.getString(cursor.getColumnIndex(lt.dualpair.android.data.provider.user.User.PhotoColumns.SOURCE_LINK)));
        return photo;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
