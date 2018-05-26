package lt.dualpair.android.data.local.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "sociotypes")
public class Sociotype {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;

    @NonNull
    private String code1;

    @NonNull
    private String code2;

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public Sociotype(@NonNull String code1, @NonNull String code2) {
        this.code1 = code1;
        this.code2 = code2;
    }

    @NonNull
    public String getCode1() {
        return code1;
    }

    public void setCode1(@NonNull String code1) {
        this.code1 = code1;
    }

    @NonNull
    public String getCode2() {
        return code2;
    }

    public void setCode2(@NonNull String code2) {
        this.code2 = code2;
    }

    public static Sociotype[] populate() {
        return new Sociotype[] {
            new Sociotype("LII", "INTJ"),
            new Sociotype("ILE", "ENTP"),
            new Sociotype("ESE", "ESFJ"),
            new Sociotype("SEI", "ISFP"),
            new Sociotype("LSI", "ISTJ"),
            new Sociotype("SLE", "ESTP"),
            new Sociotype("EIE", "ENFJ"),
            new Sociotype("IEI", "INFP"),
            new Sociotype("ESI", "ISFJ"),
            new Sociotype("SEE", "ESFP"),
            new Sociotype("LIE", "ENTJ"),
            new Sociotype("ILI", "INTP"),
            new Sociotype("EII", "INFJ"),
            new Sociotype("IEE", "ENFP"),
            new Sociotype("LSE", "ESTJ"),
            new Sociotype("SLI", "ISTP")
        };
    }
}
