package com.artur.dualpair.android.ui.socionics;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.artur.dualpair.android.R;
import com.artur.dualpair.android.core.socionics.EvaluateTestTask;
import com.artur.dualpair.android.dto.Choice;
import com.artur.dualpair.android.dto.ChoicePair;
import com.artur.dualpair.android.dto.ErrorResponse;
import com.artur.dualpair.android.dto.Sociotype;
import com.artur.dualpair.android.rx.EmptySubscriber;
import com.artur.dualpair.android.services.ServiceException;
import com.artur.dualpair.android.ui.user.ConfirmSociotypeActivity;
import com.artur.dualpair.android.utils.ToastUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SocionicsTestActivity extends ListActivity {

    private static final String TAG = "SocionicsTestActivity";

    private Button submitButton;

    private SocionicsTestListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.layout_socionics_test);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        submitButton = createSubmitButton();
        adapter = new SocionicsTestListViewAdapter(this, buildChoicePairs(), submitButton);
        setListAdapter(adapter);

        actionBar.setTitle(TitleCreator.createTitle(this, 0, adapter.getCount()));

        ListView view = getListView();
        view.addFooterView(submitButton);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Button createSubmitButton() {
        Button button = new Button(this);
        button.setText(R.string.submit);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EvaluateTestTask(SocionicsTestActivity.this, adapter.getResults()).execute(new EmptySubscriber<Sociotype>() {
                    @Override
                    public void onError(Throwable e) {
                        ServiceException se = (ServiceException)e;
                        String message;
                        try {
                            message = "Couldn't evaluate test: " + se.getErrorBodyAs(ErrorResponse.class).getErrorDescription();
                        } catch (IOException ioe) {
                            message = se.getMessage();
                        }
                        Log.e(TAG, message, e);
                        ToastUtils.show(SocionicsTestActivity.this, message);
                    }

                    @Override
                    public void onNext(Sociotype sociotype) {
                        showResults(sociotype);
                    }
                });
            }
        });
        return button;
    }

    private void showResults(final Sociotype sociotype) {
        Intent intent = new Intent(this, ConfirmSociotypeActivity.class);
        intent.putExtra(ConfirmSociotypeActivity.PARAM_SOCIOTYPE, sociotype);
        startActivity(intent);
        finish();
    }

    public static class TitleCreator {
        public static final String createTitle(Context context, int itemsSelected, int totalItems) {
            return context.getResources().getString(R.string.socionics_test) + " (" + itemsSelected + "/" + totalItems + ")";
        }
    }

    private List<ChoicePair> buildChoicePairs() {
        List<ChoicePair> choicePairs = new ArrayList<>();
        choicePairs.add(new ChoicePair("1", Choice.SISTEMATIC, Choice.SPONTANEOUS));
        /*choicePairs.add(new ChoicePair("2", Choice.STRUCTURE, Choice.FLOW));
        choicePairs.add(new ChoicePair("3", Choice.PLAN, Choice.IMPROVISATION));
        choicePairs.add(new ChoicePair("4", Choice.SOLUTION, Choice.IMPULSE));
        choicePairs.add(new ChoicePair("5", Choice.REGULARITY, Choice.ACCIDENT));
        choicePairs.add(new ChoicePair("6", Choice.ORGANIZED, Choice.IMPULSIVE));
        choicePairs.add(new ChoicePair("7", Choice.PREPARATION, Choice.IMPROMTU));

        choicePairs.add(new ChoicePair("8", Choice.RESOLUTE, Choice.DEDICATED));
        choicePairs.add(new ChoicePair("9", Choice.SOLID, Choice.KIND_HEARTED));
        choicePairs.add(new ChoicePair("10", Choice.PRONE_TO_CRITICISM, Choice.WELLWISHING));
        choicePairs.add(new ChoicePair("11", Choice.ADVANTAGE, Choice.LUCK));
        choicePairs.add(new ChoicePair("12", Choice.HEAD, Choice.HEART));
        choicePairs.add(new ChoicePair("13", Choice.THOUGHTS, Choice.FEELINGS));
        choicePairs.add(new ChoicePair("14", Choice.ANALYZE, Choice.SYMPATHIZE));*/

        return choicePairs;
    }
}
