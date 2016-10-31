package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.ui.BaseActivity;
import lt.dualpair.android.utils.ToastUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditUserDialog extends DialogFragment {

    private static final String TAG = "EditUserDialog";

    @Bind(R.id.date_of_birth) EditText dateOfBirth;
    @Bind(R.id.description)   EditText description;

    private User user;

    protected OnUserSavedCallback onUserSavedCallback;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View v = getActivity().getLayoutInflater().inflate(R.layout.edit_user_layout, null);
        ButterKnife.bind(this, v);

        new UserDataManager(getActivity()).getUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new EmptySubscriber<User>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to get user", e);
                    }

                    @Override
                    public void onNext(User u) {
                        user = u;
                        dateOfBirth.setText(dateFormat.format(u.getDateOfBirth()));
                        description.setText(u.getDescription());
                    }
                });

        builder.setView(v);
        builder.setPositiveButton(R.string.save, null);

        Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                Button b = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {
                            user.setDateOfBirth(dateFormat.parse(dateOfBirth.getText().toString()));
                            user.setDescription(description.getText().toString());
                            new UserDataManager(getActivity()).updateUser(user)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .compose(((BaseActivity)getActivity()).<User>bindToLifecycle())
                                    .subscribe(new EmptySubscriber<User>() {
                                        @Override
                                        public void onError(Throwable e) {
                                            ToastUtils.show(getActivity(), "Unable to save user");
                                        }
                                    });
                            onUserSavedCallback.onUserSaved(user);
                            dialog.dismiss();
                        } catch (ParseException pe) {
                            ToastUtils.show(getActivity(), "Invalid date");
                        }
                    }
                });
            }
        });

        return dialog;
    }

    public static final EditUserDialog getInstance(Activity activity, OnUserSavedCallback onUserSavedCallback) {
        EditUserDialog dialog = (EditUserDialog) EditUserDialog.instantiate(activity, "lt.dualpair.android.ui.user.EditUserDialog");
        dialog.onUserSavedCallback = onUserSavedCallback;
        return dialog;
    }

    public interface OnUserSavedCallback {

        void onUserSaved(User user);

    }
}
