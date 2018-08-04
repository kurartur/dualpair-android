package lt.dualpair.android.data.local.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "sociotypes")
public class Sociotype implements Serializable {

    public enum Code { LII,  ILE,  ESE,  SEI,  LSI,  SLE,  EIE,  IEI,  ESI,  SEE,  LIE,  ILI,  EII,  IEE,  LSE,  SLI }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;

    @NonNull
    private Code code;

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public Sociotype(@NonNull Code code) {
        this.code = code;
    }

    @NonNull
    public Code getCode() {
        return code;
    }

    public void setCode(@NonNull Code code) {
        this.code = code;
    }

}
