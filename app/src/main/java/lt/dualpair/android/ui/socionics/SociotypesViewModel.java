package lt.dualpair.android.ui.socionics;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.reactivex.Completable;
import io.reactivex.Observable;
import lt.dualpair.android.data.local.entity.Choice;
import lt.dualpair.android.data.local.entity.ChoicePair;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.data.repository.SociotypeRepository;
import lt.dualpair.android.data.repository.UserPrincipalRepository;

public class SociotypesViewModel extends ViewModel {

    private final SociotypeRepository sociotypeRepository;
    private UserPrincipalRepository userPrincipalRepository;
    private final LiveData<List<Sociotype>> sociotypeLiveData;
    private final MutableLiveData<Map<String, ChoicePair>> choicePairsLiveData = new MutableLiveData<>();
    private final Map<String, ChoicePair> choicePairs = buildChoicePairs();

    public SociotypesViewModel(UserPrincipalRepository userPrincipalRepository, SociotypeRepository sociotypeRepository) {
        this.userPrincipalRepository = userPrincipalRepository;
        this.sociotypeRepository = sociotypeRepository;
        sociotypeLiveData = sociotypeRepository.getSociotypes();
        choicePairsLiveData.setValue(choicePairs);
    }

    public LiveData<List<Sociotype>> getSociotypes() {
        return sociotypeLiveData;
    }

    public Completable saveSociotype(Sociotype.Code sociotypeCode) {
        return userPrincipalRepository.setSociotype(sociotypeCode);
    }

    private Map<String, ChoicePair> buildChoicePairs() {
        Map<String, ChoicePair> choicePairs = new LinkedHashMap<>();
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
        return choicePairs;
    }

    public LiveData<Map<String, ChoicePair>> getChoicePairs() {
        return choicePairsLiveData;
    }

    public void onChoice(String id, Choice choice) {
        choicePairs.get(id).setSelected(choice);
        choicePairsLiveData.setValue(choicePairs);
    }

    public Observable<Sociotype> evaluateTest() {
        return sociotypeRepository.evaluateTest(choicePairs);
    }

    public void fillRandomTestValues() {
        for (ChoicePair choicePair : choicePairs.values()) {
            choicePair.setSelected(new Random().nextInt(2) == 1 ? choicePair.getChoice1() : choicePair.getChoice2());
        }
        choicePairsLiveData.setValue(choicePairs);
    }

    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {

        private Application application;

        public Factory(@NonNull Application application) {
            super(application);
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(SociotypesViewModel.class)) {
                return (T) new SociotypesViewModel(new UserPrincipalRepository(application), new SociotypeRepository(application));
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
