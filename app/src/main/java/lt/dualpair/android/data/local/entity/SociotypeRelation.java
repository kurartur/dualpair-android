package lt.dualpair.android.data.local.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "sociotype_relations")
public class SociotypeRelation {

    @PrimaryKey
    private Long id;

    @ColumnInfo(name = "sociotype_id")
    private Long sociotypeId;

    @ColumnInfo(name = "related_sociotype_id")
    private Long relatedSociotypeId;

    public SociotypeRelation(Long sociotypeId, Long relatedSociotypeId) {
        this.sociotypeId = sociotypeId;
        this.relatedSociotypeId = relatedSociotypeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSociotypeId() {
        return sociotypeId;
    }

    public void setSociotypeId(Long sociotypeId) {
        this.sociotypeId = sociotypeId;
    }

    public Long getRelatedSociotypeId() {
        return relatedSociotypeId;
    }

    public void setRelatedSociotypeId(Long relatedSociotypeId) {
        this.relatedSociotypeId = relatedSociotypeId;
    }
}
