package lt.dualpair.android.data.task.socionics;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

import lt.dualpair.android.accounts.AuthenticatedUserTask;
import lt.dualpair.android.data.remote.client.socionics.EvaluateTestClient;
import lt.dualpair.android.data.resource.Choice;
import lt.dualpair.android.data.resource.Sociotype;

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
