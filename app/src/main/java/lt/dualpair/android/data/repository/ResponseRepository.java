package lt.dualpair.android.data.repository;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.local.DualPairRoomDatabase;
import lt.dualpair.android.data.local.dao.UserResponseDao;
import lt.dualpair.android.data.local.entity.UserListItem;
import lt.dualpair.android.data.local.entity.UserResponse;
import lt.dualpair.android.data.remote.client.user.GetUserResponsesClient;
import lt.dualpair.android.data.remote.resource.ResourceCollection;

public class ResponseRepository {

    private UserResponseDao userResponseDao;
    private Long userPrincipalId;
    private DualPairRoomDatabase database;

    public ResponseRepository(Application application) {
        userPrincipalId = AccountUtils.getUserId(application);
        database = DualPairRoomDatabase.getDatabase(application);
        userResponseDao = database.swipeDao();
    }

    public Flowable<List<UserListItem>> getReviewedUsers() {
        return userResponseDao.getResponsesFlowable()
                .map(new Function<List<UserResponse>, List<UserListItem>>() {
                    @Override
                    public List<UserListItem> apply(List<UserResponse> userResponses) throws Exception {
                        List<UserListItem> items = new ArrayList<>();
                        for (UserResponse userResponse : userResponses) {
                            items.add(new UserListItem(userResponse.getUserId(), userResponse.getName(), userResponse.getPhotoSource()));
                        }
                        return items;
                    }
                });
    }

    public Completable loadFromApi() {
        return new GetUserResponsesClient(userPrincipalId).observable()
                .doOnNext(new Consumer<ResourceCollection<lt.dualpair.android.data.remote.resource.UserResponse>>() {
                    @Override
                    public void accept(ResourceCollection<lt.dualpair.android.data.remote.resource.UserResponse> collection) throws Exception {
                        for (lt.dualpair.android.data.remote.resource.UserResponse resource : collection.getContent()) {
                            saveResource(resource);
                        }
                    }
                }).ignoreElements();
    }

    private void saveResource(lt.dualpair.android.data.remote.resource.UserResponse resource) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(resource.getUser().getId());
        userResponse.setType(resource.getResponse());
        userResponse.setName(resource.getUser().getName());
        userResponse.setPhotoSource(resource.getUser().getPhotos().get(0).getSourceUrl());
        userResponse.setMatch(resource.isMatch());
        userResponse.setDate(resource.getDate());
        userResponseDao.save(userResponse);
    }

}
