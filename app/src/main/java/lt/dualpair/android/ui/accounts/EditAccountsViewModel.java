package lt.dualpair.android.ui.accounts;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.repository.UserPrincipalRepository;

public class EditAccountsViewModel extends ViewModel {

    private UserPrincipalRepository userPrincipalRepository;
    private final MutableLiveData<List<SocialAccountItem>> accountsLiveData = new MutableLiveData<>();

    public EditAccountsViewModel(UserPrincipalRepository userPrincipalRepository) {
        this.userPrincipalRepository = userPrincipalRepository;
        load();
    }

    private void load() {
        userPrincipalRepository.getUserAccounts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(accounts -> accountsLiveData.setValue(buildItems(accounts)));
    }

    public MutableLiveData<List<SocialAccountItem>> getAccounts() {
        return accountsLiveData;
    }

    private List<SocialAccountItem> buildItems(List<UserAccount> userAccounts) {
        List<SocialAccountItem> items = new ArrayList<>();
        List<AccountType> allTypes = new ArrayList<>(Arrays.asList(AccountType.values()));
        for (UserAccount userAccount : userAccounts) {
            items.add(new SocialAccountItem(AccountType.valueOf(userAccount.getAccountType()), userAccount));
            allTypes.remove(AccountType.valueOf(userAccount.getAccountType()));
        }
        for (AccountType accountType : allTypes) {
            items.add(new SocialAccountItem(accountType, null));
        }
        return items;
    }

    public void reloadAccounts() {
        load();
    }

    public static class Factory implements ViewModelProvider.Factory {
        private Application application;
        public Factory(Application application) {
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(EditAccountsViewModel.class)) {
                return (T) new EditAccountsViewModel(new UserPrincipalRepository(application));
            }
            throw new IllegalArgumentException("Wrong classs");
        }
    }

}
