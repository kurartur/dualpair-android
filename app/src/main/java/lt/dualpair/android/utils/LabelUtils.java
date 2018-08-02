package lt.dualpair.android.utils;

import android.content.Context;

import lt.dualpair.android.data.local.entity.PurposeOfBeing;
import lt.dualpair.android.data.local.entity.RelationshipStatus;
import lt.dualpair.android.ui.accounts.AccountType;

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

    public static String getAccountTypeLabel(Context context, AccountType accountType) {
        int labelId = context.getResources().getIdentifier(accountType.name(), "string", context.getPackageName());
        return context.getString(labelId);
    }

    public static String getHeOrShe(Context context, String gender) {
        int labelId = context.getResources().getIdentifier(gender.equals("F") ? "she" : "he", "string", context.getPackageName());
        return context.getString(labelId);
    }

}
