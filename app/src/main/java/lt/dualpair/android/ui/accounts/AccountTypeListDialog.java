package lt.dualpair.android.ui.accounts;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;

import java.util.List;

import lt.dualpair.android.R;

public class AccountTypeListDialog extends DialogFragment {

    private List<AccountType> accountTypes;
    private AccountTypeAdapter.OnAccountTypeClickListener onAccountTypeClickListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.account_type_list_dialog_layout, null);
        ListView accountTypesListView = (ListView)v.findViewById(R.id.account_types);
        AccountTypeAdapter accountTypeAdapter = new AccountTypeAdapter(getActivity(), accountTypes, onAccountTypeClickListener);
        accountTypesListView.setAdapter(accountTypeAdapter);
        return v;
    }

    protected void setAccountTypes(List<AccountType> accountTypes) {
        this.accountTypes = accountTypes;
    }

    protected void setOnAccountTypeClickListener(AccountTypeAdapter.OnAccountTypeClickListener onAccountTypeClickListener) {
        this.onAccountTypeClickListener = onAccountTypeClickListener;
    }

    public static AccountTypeListDialog getInstance(final AccountTypeAdapter.OnAccountTypeClickListener onAccountTypeClickListener,
                                                    List<AccountType> accountTypes,
                                                    boolean dismissOnClick) {
        final AccountTypeListDialog fragment = new AccountTypeListDialog();
        fragment.setAccountTypes(accountTypes);
        if (dismissOnClick) {
            fragment.setOnAccountTypeClickListener(new AccountTypeAdapter.OnAccountTypeClickListener() {
                @Override
                public void onClick(AccountType accountType) {
                    onAccountTypeClickListener.onClick(accountType);
                    fragment.dismiss();
                }
            });
        } else {
            fragment.setOnAccountTypeClickListener(onAccountTypeClickListener);
        }
        return fragment;
    }

}
