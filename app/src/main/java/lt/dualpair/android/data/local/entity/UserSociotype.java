package lt.dualpair.android.data.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "user_sociotypes")
public class UserSociotype {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;

    @ColumnInfo(name = "user_id")
    private Long userId;

    @ColumnInfo(name = "sociotype_id")
    private Long sociotypeId;

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSociotypeId() {
        return sociotypeId;
    }

    public void setSociotypeId(Long sociotypeId) {
        this.sociotypeId = sociotypeId;
    }
}
