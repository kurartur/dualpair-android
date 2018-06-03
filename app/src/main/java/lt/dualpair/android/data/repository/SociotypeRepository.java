package lt.dualpair.android.data.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import lt.dualpair.android.data.local.DualPairRoomDatabase;
import lt.dualpair.android.data.local.dao.SociotypeDao;
import lt.dualpair.android.data.local.entity.Choice;
import lt.dualpair.android.data.local.entity.ChoicePair;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.data.remote.client.socionics.EvaluateTestClient;

public class SociotypeRepository {

    private final LiveData<List<Sociotype>> sociotypes;
    private DualPairRoomDatabase database;
    private SociotypeDao sociotypeDao;

    public SociotypeRepository(Application application) {
        database = DualPairRoomDatabase.getDatabase(application);
        sociotypeDao = database.sociotypeDao();
        sociotypes = sociotypeDao.getAllSociotypesLiveData();
    }

    public LiveData<List<Sociotype>> getSociotypes() {
        return sociotypes;
    }

    public LiveData<Sociotype> getSociotype(String code1) {
        return sociotypeDao.getSociotypeLive(code1);
    }

    public Observable<Sociotype> evaluateTest(Map<String, ChoicePair> choicePairs) {
        return new EvaluateTestClient(convertChoicesToStrings(collectChoices(choicePairs))).observable()
                .map(new Function<lt.dualpair.android.data.resource.Sociotype, Sociotype>() {
                    @Override
                    public Sociotype apply(lt.dualpair.android.data.resource.Sociotype sociotypeResource) throws Exception {
                        return sociotypeDao.getSociotype(sociotypeResource.getCode1());
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

    private Map<String, Choice> collectChoices(Map<String, ChoicePair> choicePairs) {
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

}
