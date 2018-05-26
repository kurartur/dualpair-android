package lt.dualpair.android.data.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "user_purposes_of_being")
public class UserPurposeOfBeing {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;

    @ColumnInfo(name = "user_id")
    private Long userId;

    private PurposeOfBeing purpose;

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PurposeOfBeing getPurpose() {
        return purpose;
    }

    public void setPurpose(PurposeOfBeing purpose) {
        this.purpose = purpose;
    }
}
