package lt.dualpair.android.ui.socionics;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.ui.BaseFragment;
import lt.dualpair.android.utils.ActionBarUtils;
import lt.dualpair.android.utils.LabelUtils;

public class SociotypeDetailsFragment extends BaseFragment {

    public static final String ARG_SOCIOTYPE = "sociotype";

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.link)
    TextView link;

    protected SociotypesViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.layout_sociotype_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity(), new SociotypesViewModel.Factory(getActivity().getApplication())).get(SociotypesViewModel.class);
        subscribeUi();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ActionBarUtils.setHomeButtonEnabled(getActivity(), true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void subscribeUi() {
        // nothing here, just show sociotype arg
        Sociotype sociotype = getSociotype();
        String t = LabelUtils.getSociotypeAcronym(getContext(), sociotype.getCode());
        String sociotype4LetterAcronym = LabelUtils.getSociotype4LetterAcronym(getContext(), sociotype.getCode());
        t += " (" + sociotype4LetterAcronym + ") - ";
        t += LabelUtils.getSociotypeSocialRole(getContext(), sociotype.getCode());
        title.setText(t);
        String url = "http://www.sociotype.com/socionics/types/"
                + sociotype.getCode().name()
                + "-"
                + sociotype4LetterAcronym
                + "/";
        link.setText(Html.fromHtml("<a href=\"" + url + "\">" + getString(R.string.more) + "...</a>"));
        link.setMovementMethod(LinkMovementMethod.getInstance());
        setupActionBar(sociotype);
    }

    private void setupActionBar(Sociotype sociotype) {
        Sociotype.Code code = sociotype.getCode();
        ActionBarUtils.setTitle(getActivity(), LabelUtils.getSociotypeSocialRole(getContext(), code) + " - " + code);
    }

    public Sociotype getSociotype() {
        return (Sociotype) getArguments().getSerializable(ARG_SOCIOTYPE);
    }

    public static SociotypeDetailsFragment newInstance(Sociotype sociotype) {
        SociotypeDetailsFragment f = new SociotypeDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SOCIOTYPE, sociotype);
        f.setArguments(args);
        return f;
    }
}
