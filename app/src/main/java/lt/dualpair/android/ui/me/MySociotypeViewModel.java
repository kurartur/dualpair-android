package lt.dualpair.android.ui.me;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.data.local.entity.UserSociotype;
import lt.dualpair.android.data.repository.SociotypeRepository;
import lt.dualpair.android.data.repository.UserPrincipalRepository;
import lt.dualpair.android.data.repository.UserRepository;

public class MySociotypeViewModel extends AndroidViewModel {

    private Long userId;
    private Flowable<UserSociotypeModel> userSociotypeFlowable;

    public MySociotypeViewModel(@NonNull Application application) {
        super(application);
        userId = AccountUtils.getUserId(application);
        UserPrincipalRepository userPrincipalRepository = new UserPrincipalRepository(application);
        SociotypeRepository sociotypeRepository = new SociotypeRepository(application);
        UserRepository userRepository = new UserRepository(application);
        userSociotypeFlowable = userRepository.getUserSociotype(userId)
                .flatMap(new Function<UserSociotype, Publisher<UserSociotypeModel>>() {
                    @Override
                    public Publisher<UserSociotypeModel> apply(UserSociotype userSociotype) throws Exception {
                        return Flowable.zip(
                                Flowable.just(userSociotype),
                                sociotypeRepository.getSociotype(userSociotype.getSociotypeId()),
                                new BiFunction<UserSociotype, Sociotype, UserSociotypeModel>() {
                                    @Override
                                    public UserSociotypeModel apply(UserSociotype userSociotype, Sociotype sociotype) throws Exception {
                                        return new UserSociotypeModel(userSociotype, sociotype);
                                    }
                                }
                        );
                    }
                });
    }

    public Flowable<UserSociotypeModel> getUserSociotype() {
        return userSociotypeFlowable;
    }
}
