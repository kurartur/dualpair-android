package lt.dualpair.android.data.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity(tableName = "user_search_parameters")
public class UserSearchParameters implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "user_id")
    @NonNull
    private Long userId;

    @ColumnInfo(name = "search_male")
    private Boolean searchMale;

    @ColumnInfo(name = "search_female")
    private Boolean searchFemale;

    @ColumnInfo(name = "min_age")
    private Integer minAge;

    @ColumnInfo(name = "max_age")
    private Integer maxAge;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getSearchMale() {
        return searchMale;
    }

    public void setSearchMale(Boolean searchMale) {
        this.searchMale = searchMale;
    }

    public Boolean getSearchFemale() {
        return searchFemale;
    }

    public void setSearchFemale(Boolean searchFemale) {
        this.searchFemale = searchFemale;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }
}
