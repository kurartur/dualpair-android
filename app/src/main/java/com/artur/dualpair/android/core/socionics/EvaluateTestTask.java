package com.artur.dualpair.android.core.socionics;

import android.app.Activity;

import com.artur.dualpair.android.accounts.AuthenticatedUserTask;
import com.artur.dualpair.android.dto.Choice;
import com.artur.dualpair.android.dto.Sociotype;
import com.artur.dualpair.android.services.socionics.EvaluateTestClient;

import java.util.HashMap;
import java.util.Map;

public class EvaluateTestTask extends AuthenticatedUserTask<Sociotype> {

    private Map<String, Choice> choices;

    public EvaluateTestTask(Activity activity, Map<String, Choice> choices) {
        super(activity);
        this.choices = choices;
    }

    @Override
    protected Sociotype run() throws Exception {
        return new EvaluateTestClient(convertChoicesToStrings(choices)).observable().toBlocking().first();
    }

    private Map<String, String> convertChoicesToStrings(Map<String, Choice> input) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Choice> entry : input.entrySet()) {
            result.put(entry.getKey(), entry.getValue().name());
        }
        return result;
    }
}
