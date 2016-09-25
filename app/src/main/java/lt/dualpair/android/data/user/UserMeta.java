package lt.dualpair.android.data.user;

import android.provider.BaseColumns;

public class UserMeta {

    public static final class UserColumns implements BaseColumns {

        private UserColumns() {}

        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String DATE_OF_BIRTH = "date_of_birth";
        public static final String AGE = "age";

    }

    public static final class SociotypeColumns implements BaseColumns {

        private SociotypeColumns() {}

        public static final String USER_ID = "user_id";
        public static final String CODE_1 = "code1";
        public static final String CODE_2 = "code2";

    }

    public static final class PhotoColumns implements BaseColumns {

        private PhotoColumns() {}

        public static final String USER_ID = "user_id";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String ID_ON_ACCOUNT = "id_on_account";
        public static final String SOURCE_LINK = "source_link";
        public static final String POSITION = "position";

    }

}
