package lt.dualpair.android.utils;

import android.content.Context;

import lt.dualpair.android.data.local.entity.PurposeOfBeing;
import lt.dualpair.android.data.local.entity.RelationshipStatus;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.ui.accounts.AccountType;

public class LabelUtils {

    public static String getRelationshipStatusLabel(Context context, RelationshipStatus relationshipStatus) {
        return getString(context, "rs_" + relationshipStatus.name().toLowerCase());
    }

    public static String getPurposeOfBeingLabel(Context context, PurposeOfBeing purpose) {
        return getString(context, "pob_" + purpose.name().toLowerCase());
    }

    public static String getSociotypeSocialRole(Context context, Sociotype.Code code) {
        return getString(context, code.name().toLowerCase() + "_social_role");
    }

    public static String getSociotypeFullTitle(Context context, Sociotype.Code code) {
        return getString(context, code.name().toLowerCase() + "_full");
    }

    public static String getAccountTypeLabel(Context context, AccountType accountType) {
        return getString(context, accountType.name());
    }

    public static String getHeOrShe(Context context, String gender) {
        return getString(context, gender.equals("F") ? "she" : "he");
    }

    public static String getSociotypeAcronym(Context context, Sociotype.Code code) {
        return getString(context, code.name().toLowerCase());
    }

    public static String getSociotype4LetterAcronym(Context context, Sociotype.Code code) {
        return getString(context, code.name().toLowerCase() + "_4letter");
    }

    private static String getString(Context context, String idString) {
        int id = context.getResources().getIdentifier(idString, "string", context.getPackageName());
        return context.getString(id);
    }
}
