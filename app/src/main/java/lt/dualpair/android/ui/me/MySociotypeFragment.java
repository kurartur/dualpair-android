package lt.dualpair.android.ui.me;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trello.rxlifecycle2.android.FragmentEvent;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.ConnectivityMonitor;
import lt.dualpair.android.R;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.ui.socionics.SetSociotypeActivity;
import lt.dualpair.android.utils.LabelUtils;

public class MySociotypeFragment extends BaseFragment {

    @Bind(R.id.first_letter)
    TextView firstLetter;
    @Bind(R.id.first_text)
    TextView firstText;
    @Bind(R.id.second_letter)
    TextView secondLetter;
    @Bind(R.id.second_text)
    TextView secondText;
    @Bind(R.id.third_letter)
    TextView thirdLetter;
    @Bind(R.id.third_text)
    TextView thirdText;

    private MySociotypeViewModel viewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.edit_sociotype_layout, container, false);
        ButterKnife.bind(this, view);
        requestOfflineNotification(view.findViewById(R.id.offline));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(MySociotypeViewModel.class);
        subscribeUi();
    }

    @SuppressLint("CheckResult")
    private void subscribeUi() {
        viewModel.getUserSociotype()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindUntilEvent(FragmentEvent.DESTROY))
            .subscribe(new Consumer<UserSociotypeModel>() {
                @Override
                public void accept(UserSociotypeModel userSociotypeModel) throws Exception {
                    render(userSociotypeModel);
                }
            });
    }

    private void render(UserSociotypeModel model) {
        String label = LabelUtils.getSociotypeFullTitle(getActivity(), model.getSociotype().getCode());
        String splittedLabel[] = label.split(" ");
        setSociotypeTitlePart(firstLetter, firstText, splittedLabel[0]);
        setSociotypeTitlePart(secondLetter, secondText, splittedLabel[1]);
        setSociotypeTitlePart(thirdLetter, thirdText, splittedLabel[2]);
    }

    private void setSociotypeTitlePart(TextView letter, TextView text, String label) {
        letter.setText(label.substring(0, 1));
        text.setText(label.substring(1, label.length()));
    }

    @OnClick(R.id.change_button) void onChangeButtonClick(View view) {
        startActivity(SetSociotypeActivity.createIntent(getActivity(), false));
    }

    @SuppressLint("CheckResult")
    protected void requestOfflineNotification(final View offlineNotificationView) {
        ConnectivityMonitor.getInstance().getConnectivityInfo()
                .compose(bindToLifecycle())
                .subscribe(new Consumer<ConnectivityMonitor.ConnectivityInfo>() {
                    @Override
                    public void accept(ConnectivityMonitor.ConnectivityInfo connectivityInfo) throws Exception {
                        if (offlineNotificationView != null) {
                            offlineNotificationView.setVisibility(connectivityInfo.isNetworkAvailable() ? View.GONE : View.VISIBLE);
                        }
                    }
                });
    }

    public static MySociotypeFragment newInstance() {
        return new MySociotypeFragment();
    }
}
