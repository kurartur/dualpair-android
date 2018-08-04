package lt.dualpair.android.data.local;

import android.arch.persistence.room.TypeConverter;

import lt.dualpair.android.data.local.entity.Sociotype;

public class SociotypeCodeConverter {

    @TypeConverter
    public static Sociotype.Code toCode(String value) {
        return Sociotype.Code.valueOf(value);
    }

    @TypeConverter
    public static String toString(Sociotype.Code value) {
        return value == null ? "" : value.name();
    }

}
