package lt.dualpair.android.ui.accounts;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Completable;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.repository.UserPrincipalRepository;

public class EditAccountsViewModel extends ViewModel {

    private UserPrincipalRepository userPrincipalRepository;
    private final LiveData<List<SocialAccountItem>> accountsLive;

    public EditAccountsViewModel(UserPrincipalRepository userPrincipalRepository) {
        this.userPrincipalRepository = userPrincipalRepository;
        accountsLive = Transformations.map(userPrincipalRepository.getUserAccountsLive(), new Function<List<UserAccount>, List<SocialAccountItem>>() {
            @Override
            public List<SocialAccountItem> apply(List<UserAccount> input) {
                return buildItems(input);
            }
        });
    }

    public LiveData<List<SocialAccountItem>> getAccounts() {
        return accountsLive;
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

    public Completable reloadAccounts() {
        return userPrincipalRepository.loadFromApi();
    }

    public Completable connectAccount(String providerId, String accessToken, Long expiresIn, String scope) {
        return userPrincipalRepository.connectAccount(providerId, accessToken, expiresIn, scope);
    }

    public Completable disconnectAccount(String providerId) {
        return userPrincipalRepository.disconnectAccount(providerId);
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
