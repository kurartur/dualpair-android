package lt.dualpair.android.data.repo;

import android.provider.BaseColumns;

public class SearchParametersMeta {

    public static class SearchParameters implements BaseColumns {

        public static final String TABLE_NAME = "search_parameters";
        public static final String MIN_AGE = "min_age";
        public static final String MAX_AGE = "max_age";
        public static final String SEARCH_MALE = "search_male";
        public static final String SEARCH_FEMALE = "search_female";

    }

}
