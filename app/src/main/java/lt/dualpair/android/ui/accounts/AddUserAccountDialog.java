package lt.dualpair.android.ui.accounts;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.UserAccount;

public class AddUserAccountDialog extends DialogFragment {

    public static final String USER_ACCOUNTS_KEY = "ExistingAccounts";

    @Bind(R.id.account_types)
    ListView accountTypes;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.add_user_account_dialog, null);
        ButterKnife.bind(this, view);

        List<UserAccount> userAccounts = (List)getArguments().getSerializable(USER_ACCOUNTS_KEY);

        AccountTypeAdapter accountTypeAdapter = new AccountTypeAdapter(getActivity(), getNotAddedAccountTypes(userAccounts));
        accountTypes.setAdapter(accountTypeAdapter);

        builder.setView(view);

        return builder.create();
    }

    private List<AccountType> getNotAddedAccountTypes(List<UserAccount> userAccounts) {
        List<AccountType> accountTypes = Arrays.asList(AccountType.values());
        for (UserAccount userAccount : userAccounts) {
            accountTypes.remove(AccountType.valueOf(userAccount.getAccountType()));
        }
        return accountTypes;
    }

}
