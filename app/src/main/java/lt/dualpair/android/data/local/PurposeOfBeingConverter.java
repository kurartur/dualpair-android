package lt.dualpair.android.data.local;

import android.arch.persistence.room.TypeConverter;

import lt.dualpair.android.data.local.entity.PurposeOfBeing;

public class PurposeOfBeingConverter {

    @TypeConverter
    public static PurposeOfBeing toPurposeOfBeing(String code) {
        return code == null ? null : PurposeOfBeing.fromCode(code);
    }

    @TypeConverter
    public static String toCode(PurposeOfBeing value) {
        return value == null ? null : value.getCode();
    }

}
