package lt.dualpair.android.utils;

import android.content.Context;

import lt.dualpair.android.data.local.entity.PurposeOfBeing;
import lt.dualpair.android.data.local.entity.RelationshipStatus;

public class LabelUtils {

    public static String getRelationshipStatusLabel(Context context, RelationshipStatus relationshipStatus) {
        return context.getResources().getString(context.getResources().getIdentifier("rs_" + relationshipStatus.name().toLowerCase(), "string", context.getPackageName()));
    }

    public static String getPurposeOfBeingLabel(Context context, PurposeOfBeing purpose) {
        return context.getResources().getString(context.getResources().getIdentifier("pob_" + purpose.name().toLowerCase(), "string", context.getPackageName()));
    }

    public static String getSociotypeTitle(Context context, String code1) {
        int titleId = context.getResources().getIdentifier(code1.toLowerCase() + "_title", "string", context.getPackageName());
        return context.getString(titleId);
    }

}
