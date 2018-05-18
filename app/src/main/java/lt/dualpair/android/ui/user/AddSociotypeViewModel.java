package lt.dualpair.android.ui.user;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import lt.dualpair.android.data.resource.Sociotype;

public class AddSociotypeViewModel extends ViewModel {

    private final MediatorLiveData<List<Sociotype>> sociotypes;

    public AddSociotypeViewModel() {
        sociotypes = new MediatorLiveData<>();
        sociotypes.setValue(buildSociotypes());
    }

    public LiveData<List<Sociotype>> getSociotypes() {
        return sociotypes;
    }

    private List<Sociotype> buildSociotypes() {
        // hardcoded
        List<Sociotype> sociotypes = new ArrayList<>();
        sociotypes.add(createSociotype("LII", "INTJ"));
        sociotypes.add(createSociotype("ILE", "ENTP"));
        sociotypes.add(createSociotype("ESE", "ESFJ"));
        sociotypes.add(createSociotype("SEI", "ISFP"));
        sociotypes.add(createSociotype("LSI", "ISTJ"));
        sociotypes.add(createSociotype("SLE", "ESTP"));
        sociotypes.add(createSociotype("EIE", "ENFJ"));
        sociotypes.add(createSociotype("IEI", "INFP"));
        sociotypes.add(createSociotype("ESI", "ISFJ"));
        sociotypes.add(createSociotype("SEE", "ESFP"));
        sociotypes.add(createSociotype("LIE", "ENTJ"));
        sociotypes.add(createSociotype("ILI", "INTP"));
        sociotypes.add(createSociotype("EII", "INFJ"));
        sociotypes.add(createSociotype("IEE", "ENFP"));
        sociotypes.add(createSociotype("LSE", "ESTJ"));
        sociotypes.add(createSociotype("SLI", "ISTP"));
        return sociotypes;
    }

    private Sociotype createSociotype(String code1, String code2) {
        Sociotype sociotype = new Sociotype();
        sociotype.setCode1(code1);
        sociotype.setCode2(code2);
        return sociotype;
    }
}
