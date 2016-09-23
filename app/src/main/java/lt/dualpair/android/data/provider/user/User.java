package lt.dualpair.android.data.provider.user;

import android.net.Uri;
import android.provider.BaseColumns;

public class User {

    public static final String AUTHORITY = "lt.dualpair.android.data.User";

    public static final class UserColumns implements BaseColumns {

        private UserColumns() {}

        public static final Uri USER_URI = Uri.parse("content://" + AUTHORITY + "/user");

        public static final String CONTENT_TYPE = "vnd.android.cursor.item/vnd.lt.dualpair.android.user";

        public static final Uri CONTENT_URI = USER_URI;

        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String DATE_OF_BIRTH = "date_of_birth";
        public static final String AGE = "age";

    }

    public static final class SociotypeColumns implements BaseColumns {

        private SociotypeColumns() {}

        public static final Uri SOCIOTYPES_URI = Uri.parse("content://" + AUTHORITY + "/sociotypes");

        public static final String CONTENT_SOCIOTYPES_TYPE = "vnd.android.cursor.dir/vnd.lt.dualpair.android.sociotype";

        public static final Uri CONTENT_URI = SOCIOTYPES_URI;

        public static final String USER_ID = "user_id";
        public static final String CODE_1 = "code1";
        public static final String CODE_2 = "code2";

    }

    public static final class PhotoColumns implements BaseColumns {

        private PhotoColumns() {}

        public static final Uri PHOTOS_URI = Uri.parse("content://" + AUTHORITY + "/photos");

        public static final String CONTENT_PHOTOS_TYPE = "vnd.android.cursor.dir/vnd.lt.dualpair.android.photo";

        public static final Uri CONTENT_URI = PHOTOS_URI;

        public static final String USER_ID = "user_id";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String ID_ON_ACCOUNT = "id_on_account";
        public static final String SOURCE_LINK = "source_link";
        public static final String POSITION = "position";

    }

}
