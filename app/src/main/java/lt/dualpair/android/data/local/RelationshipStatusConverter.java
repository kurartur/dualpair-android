package lt.dualpair.android.data.local;

import android.arch.persistence.room.TypeConverter;

import lt.dualpair.android.data.local.entity.RelationshipStatus;

public class RelationshipStatusConverter {

    @TypeConverter
    public static RelationshipStatus toRelationshipStatus(String value) {
        return value == null || value.isEmpty() ? RelationshipStatus.NONE : RelationshipStatus.fromCode(value);
    }

    @TypeConverter
    public static String toString(RelationshipStatus value) {
        return value == null ? "" : value.getCode();
    }
}