package lt.dualpair.android.data.local.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.Arrays;
import java.util.List;

public class FullUserSociotype {

    @Embedded
    private UserSociotype userSociotype;

    @Relation(entityColumn = "id", parentColumn = "sociotype_id", entity = Sociotype.class)
    private List<Sociotype> sociotypes;

    public FullUserSociotype(UserSociotype userSociotype, Sociotype sociotype) {
        this.userSociotype = userSociotype;
        this.sociotypes = Arrays.asList(sociotype);
    }

    public FullUserSociotype() {}

    public UserSociotype getUserSociotype() {
        return userSociotype;
    }

    public void setUserSociotype(UserSociotype userSociotype) {
        this.userSociotype = userSociotype;
    }

    public List<Sociotype> getSociotypes() {
        return sociotypes;
    }

    public void setSociotypes(List<Sociotype> sociotypes) {
        this.sociotypes = sociotypes;
    }

    public Sociotype getSociotype() {
        return sociotypes.get(0);
    }
}
