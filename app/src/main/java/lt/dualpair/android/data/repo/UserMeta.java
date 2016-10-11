package lt.dualpair.android.data.repo;

import android.provider.BaseColumns;

public class UserMeta {

    public static final class User implements BaseColumns {

        public static final String TABLE_NAME = "users";

        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String DATE_OF_BIRTH = "date_of_birth";
        public static final String AGE = "age";
        public static final String UPDATE_TIME = "update_time";

    }

    public static final class Sociotype implements BaseColumns {

        public static final String TABLE_NAME = "user_sociotypes";

        public static final String USER_ID = "user_id";
        public static final String CODE_1 = "code1";
        public static final String CODE_2 = "code2";

    }

    public static final class Photo implements BaseColumns {

        public static final String TABLE_NAME = "user_photos";

        public static final String USER_ID = "user_id";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String ID_ON_ACCOUNT = "id_on_account";
        public static final String SOURCE_LINK = "source_link";
        public static final String POSITION = "position";

    }

    public static final class Location implements BaseColumns {

        public static final String TABLE_NAME = "user_locations";

        public static final String USER_ID = "user_id";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String COUNTRY_CODE = "country_code";
        public static final String CITY = "city";

    }

    public static final class UserAccount implements BaseColumns {

        public static final String TABLE_NAME = "user_accounts";

        public static final String USER_ID = "user_id";
        public static final String ACCOUNT_ID = "account_id";
        public static final String ACCOUNT_TYPE = "account_type";

    }

}
