package lt.dualpair.android.ui.socionics;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.data.remote.client.ServiceException;
import lt.dualpair.android.data.remote.client.socionics.EvaluateTestClient;
import lt.dualpair.android.data.resource.Choice;
import lt.dualpair.android.data.resource.ChoicePair;
import lt.dualpair.android.data.resource.ErrorResponse;
import lt.dualpair.android.data.resource.Sociotype;

public class SocionicsTestPresenter {

    private static final String TAG = "SocionicsTestPresenter";

    private SocionicsTestActivity view;

    private Map<String, ChoicePair> choicePairs = new LinkedHashMap<>();

    private String error;

    private Sociotype result;

    public SocionicsTestPresenter() {
        buildChoicePairs();
    }

    public SocionicsTestPresenter(Bundle outState) {
        buildChoicePairs();
        // TODO restore state
    }

    private void buildChoicePairs() {
        choicePairs.put("1", (new ChoicePair("1", Choice.SISTEMATIC, Choice.SPONTANEOUS)));
        choicePairs.put("2", (new ChoicePair("2", Choice.STRUCTURE, Choice.FLOW)));
        choicePairs.put("3", (new ChoicePair("3", Choice.PLAN, Choice.IMPROVISATION)));
        choicePairs.put("4", (new ChoicePair("4", Choice.SOLUTION, Choice.IMPULSE)));
        choicePairs.put("5", (new ChoicePair("5", Choice.REGULARITY, Choice.ACCIDENT)));
        choicePairs.put("6", (new ChoicePair("6", Choice.ORGANIZED, Choice.IMPULSIVE)));
        choicePairs.put("7", (new ChoicePair("7", Choice.PREPARATION, Choice.IMPROMTU)));

        choicePairs.put("8", (new ChoicePair("8", Choice.RESOLUTE, Choice.DEDICATED)));
        choicePairs.put("9", (new ChoicePair("9", Choice.SOLID, Choice.KIND_HEARTED)));
        choicePairs.put("10", (new ChoicePair("10", Choice.PRONE_TO_CRITICISM, Choice.WELLWISHING)));
        choicePairs.put("11", (new ChoicePair("11", Choice.ADVANTAGE, Choice.LUCK)));
        choicePairs.put("12", (new ChoicePair("12", Choice.HEAD, Choice.HEART)));
        choicePairs.put("13", (new ChoicePair("13", Choice.THOUGHTS, Choice.FEELINGS)));
        choicePairs.put("14", (new ChoicePair("14", Choice.ANALYZE, Choice.SYMPATHIZE)));

        choicePairs.put("15", (new ChoicePair("15", Choice.FACTUAL, Choice.THEORETICAL)));
        choicePairs.put("16", (new ChoicePair("16", Choice.APPLICATION_IN_PRACTICE, Choice.HIDDEN_MEANING_SEARCH)));
        choicePairs.put("17", (new ChoicePair("17", Choice.EXPERIENCE, Choice.THEORY)));
        choicePairs.put("18", (new ChoicePair("18", Choice.REASONABLE, Choice.ASTONISHING)));
        choicePairs.put("19", (new ChoicePair("19", Choice.PRACTICIAN, Choice.VISIONARY)));
        choicePairs.put("20", (new ChoicePair("20", Choice.REALIST, Choice.DREAMER)));
        choicePairs.put("21", (new ChoicePair("21", Choice.REALITY, Choice.PROSPECTS)));

        choicePairs.put("22", (new ChoicePair("22", Choice.NOISY, Choice.QUIET)));
        choicePairs.put("23", (new ChoicePair("23", Choice.LIVELY, Choice.CALM)));
        choicePairs.put("24", (new ChoicePair("24", Choice.SOCIABILITY, Choice.CONCENTRATION)));
        choicePairs.put("25", (new ChoicePair("25", Choice.ENERGY_EXPENDITURE, Choice.ENERGY_SAVING)));
        choicePairs.put("26", (new ChoicePair("26", Choice.ORIENTED_TO_OUTSIDE_WORLD, Choice.ORIENTED_INWARD)));
        choicePairs.put("27", (new ChoicePair("27", Choice.SPEAK_ALOUD, Choice.LIVE_THROUGH)));
        choicePairs.put("28", (new ChoicePair("28", Choice.BRAVE, Choice.COLD_BLOODED)));
    }

    public void onTakeView(SocionicsTestActivity view) {
        this.view = view;
        publish();
    }

    private void publish() {
        if (view != null) {
            view.setChoicePairs(new ArrayList<>(choicePairs.values()));
            if (!TextUtils.isEmpty(error)) {
                view.showError(error);
                error = null;
            } else if (result != null) {
                view.showResults(result);
                result = null;
            }
        }
    }

    public void onChoice(String id, Choice choice) {
        choicePairs.get(id).setSelected(choice);
        publish();
        view.onSelectionChange(id);
    }

    public void submitTest() {
        new EvaluateTestClient(convertChoicesToStrings(collectChoices())).observable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Sociotype>() {
                    @Override
                    public void accept(Sociotype sociotype) {
                        result = sociotype;
                        publish();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) {
                        ServiceException se = (ServiceException)e;
                        String message;
                        try {
                            message = "Couldn't evaluate test: " + se.getErrorBodyAs(ErrorResponse.class).getMessage();
                        } catch (IOException ioe) {
                            message = se.getMessage();
                        }
                        Log.e(TAG, message, e);
                        error = message;
                        publish();
                    }
                });
    }

    private Map<String, String> convertChoicesToStrings(Map<String, Choice> input) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Choice> entry : input.entrySet()) {
            result.put(entry.getKey(), entry.getValue().name());
        }
        return result;
    }

    private Map<String, Choice> collectChoices() {
        Map<String, Choice> choices = new HashMap<>();
        for (ChoicePair choicePair : choicePairs.values()) {
            Choice choice;
            if (choicePair.isChoice1Selected()) {
                choice = choicePair.getChoice1();
            } else if (choicePair.isChoice2Selected()) {
                choice = choicePair.getChoice2();
            } else {
                throw new IllegalStateException("Choice not selected");
            }
            choices.put(choicePair.getId(), choice);
        }
        return choices;
    }

    public void selectRandom() {
        for (ChoicePair choicePair : choicePairs.values()) {
            choicePair.setSelected(new Random().nextInt(2) == 1 ? choicePair.getChoice1() : choicePair.getChoice2());
            publish();
            view.onSelectionChange(choicePair.getId());
        }
    }
}
