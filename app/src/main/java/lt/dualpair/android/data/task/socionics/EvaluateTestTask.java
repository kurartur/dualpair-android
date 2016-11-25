package lt.dualpair.android.data.task.socionics;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

import lt.dualpair.android.data.remote.client.socionics.EvaluateTestClient;
import lt.dualpair.android.data.resource.Choice;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.data.task.AuthenticatedUserTask;
import rx.Observable;

public class EvaluateTestTask extends AuthenticatedUserTask<Sociotype> {

    private Map<String, Choice> choices;

    public EvaluateTestTask(String authToken, Map<String, Choice> choices) {
        super(authToken);
        this.choices = choices;
    }

    @Override
    protected Observable<Sociotype> run(Context context) {
        return new EvaluateTestClient(convertChoicesToStrings(choices)).observable();
    }

    private Map<String, String> convertChoicesToStrings(Map<String, Choice> input) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Choice> entry : input.entrySet()) {
            result.put(entry.getKey(), entry.getValue().name());
        }
        return result;
    }
}
