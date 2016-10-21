package lt.dualpair.android.ui.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.utils.ToastUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditUserDialog extends DialogFragment {

    private static final String TAG = "EditUserDialog";

    @Bind(R.id.date_of_birth) EditText dateOfBirth;
    @Bind(R.id.description)   EditText description;

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
                    public void onNext(User user) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        dateOfBirth.setText(sdf.format(user.getDateOfBirth()));
                        description.setText(user.getDescription());
                    }
                });

        builder.setView(v);

        builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ToastUtils.show(getActivity(), dateOfBirth.getText() + " " + description.getText()); // TODO
            }
        });

        return builder.create();
    }
}
