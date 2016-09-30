package lt.dualpair.android.data.repo;

import android.provider.BaseColumns;

public class MatchMeta {

    public static class Match implements BaseColumns {

        public static final String TABLE_NAME = "match";

    }

    public static class Party implements BaseColumns {

        public static final String TABLE_NAME = "match_parties";

        public static final String USER_ID = "user_id";
        public static final String MATCH_ID = "match_id";
        public static final String RESPONSE = "response";

    }



}
