package lt.dualpair.android.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lt.dualpair.android.R;

public abstract class BaseLayoutFragment extends BaseFragment {

    View loadingView;
    View noConnectionView;
    View contentView;
    View unexpectedErrorView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.base_layout, container, false);
        loadingView = view.findViewById(R.id.loading);
        noConnectionView = view.findViewById(R.id.no_connection);
        contentView = view.findViewById(R.id.content_layout);
        unexpectedErrorView = view.findViewById(R.id.unexpected_error);
        showLoading();
        return view;
    }

    protected void showLoading() {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        noConnectionView.setVisibility(View.GONE);
        unexpectedErrorView.setVisibility(View.GONE);
    }

    protected void showContent() {
        contentView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.GONE);
        unexpectedErrorView.setVisibility(View.GONE);
    }

    protected void showNoConnection() {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.VISIBLE);
        unexpectedErrorView.setVisibility(View.GONE);
    }

    protected void showUnexpectedError() {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        noConnectionView.setVisibility(View.GONE);
        unexpectedErrorView.setVisibility(View.VISIBLE);
    }
}
