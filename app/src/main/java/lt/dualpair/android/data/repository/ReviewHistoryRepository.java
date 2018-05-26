package lt.dualpair.android.data.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.local.DualPairRoomDatabase;
import lt.dualpair.android.data.local.dao.SwipeDao;
import lt.dualpair.android.data.local.dao.UserDao;
import lt.dualpair.android.data.local.entity.History;
import lt.dualpair.android.data.local.entity.RelationshipStatus;
import lt.dualpair.android.data.local.entity.Swipe;
import lt.dualpair.android.data.local.entity.User;
import lt.dualpair.android.data.local.entity.UserAccount;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.data.remote.client.match.GetUserMatchListClient;
import lt.dualpair.android.data.resource.Match;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.ResourceCollection;

public class ReviewHistoryRepository {

    private LiveData<List<History>> historyLiveData;
    private SwipeDao swipeDao;
    private UserDao userDao;
    private Long userId;
    DualPairRoomDatabase database;

    public ReviewHistoryRepository(Application application) {
        database = DualPairRoomDatabase.getDatabase(application);
        swipeDao = database.swipeDao();
        userDao = database.userDao();
        historyLiveData = swipeDao.getHistory();
        userId = AccountUtils.getUserId(application);
        loadHistory();
    }

    private void loadHistory() {
        new GetUserMatchListClient(userId, GetUserMatchListClient.REVIEWED).observable()
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ResourceCollection<Match>>() {
                    @Override
                    public void accept(ResourceCollection<Match> matchResourceCollection) {
                        for (Match match : matchResourceCollection.getContent()) {
                            User user = mapUser(new User(), match.getOpponent().getUser());
                            List<UserAccount> userAccounts = new ArrayList<>();
                            if (match.getOpponent().getUser().getAccounts() != null) {
                                for (lt.dualpair.android.data.resource.UserAccount account : match.getOpponent().getUser().getAccounts()) {
                                    UserAccount userAccount = new UserAccount();
                                    userAccount.setUserId(user.getId());
                                    userAccount.setAccountId(account.getAccountId());
                                    userAccount.setAccountType(account.getAccountType().name());
                                    userAccounts.add(userAccount);
                                }
                            }
                            List<UserPhoto> userPhotos = new ArrayList<>();
                            for (Photo photo : match.getOpponent().getUser().getPhotos()) {
                                UserPhoto userPhoto = new UserPhoto();
                                userPhoto.setUserId(user.getId());
                                userPhoto.setAccountType(photo.getAccountType().name());
                                userPhoto.setIdOnAccount(photo.getIdOnAccount());
                                userPhoto.setPosition(photo.getPosition());
                                userPhoto.setSourceLink(photo.getSourceUrl());
                                userPhotos.add(userPhoto);
                            }
                            Swipe swipe = new Swipe();
                            swipe.setId(match.getId());
                            swipe.setUserId(userId);
                            swipe.setWho(user.getId());
                            swipe.setType(match.getUser().getResponse().toString());
                            database.runInTransaction(() -> {
                                userDao.saveUser(user);
                                userDao.replaceUserAccounts(user.getId(), userAccounts);
                                userDao.replaceUserPhotos(user.getId(), userPhotos);
                                swipeDao.save(swipe);
                            });
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.d(ReviewHistoryRepository.class.getName(), throwable.getMessage(), throwable);
                    }
                });
    }

    private User mapUser(User to, lt.dualpair.android.data.resource.User from) {
        to.setId(from.getId());
        to.setName(from.getName());
        to.setDateOfBirth(from.getDateOfBirth());
        to.setAge(from.getAge());
        to.setDescription(from.getDescription());
        to.setRelationshipStatus(RelationshipStatus.fromCode(from.getRelationshipStatus()));
        return to;
    }

    public LiveData<List<History>> getHistory() {
        return historyLiveData;
    }

    public void reload() {
        loadHistory();
    }
}
