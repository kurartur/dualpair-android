package lt.dualpair.android.data.repository;

import android.app.Application;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import lt.dualpair.android.accounts.AccountUtils;
import lt.dualpair.android.data.local.entity.UserPhoto;
import lt.dualpair.android.ui.accounts.AccountType;

public class PhotoRepository {

    private Long userPrincipalId;

    public PhotoRepository(Application application) {
        userPrincipalId = AccountUtils.getUserId(application);
    }

    public Observable<List<UserPhoto>> getAvailableUserPhotos(AccountType accountType) {
        if (accountType == AccountType.FB) {
            return Observable.fromCallable(this::getFacebookPhotos);
        } else if (accountType == AccountType.VK) {
            return getVkontaktePhotos();
        }
        throw new IllegalArgumentException("Unsupported account type");
    }

    private Observable<List<UserPhoto>> getVkontaktePhotos() {
        return Observable.create(new ObservableOnSubscribe<List<UserPhoto>>() {
            @Override
            public void subscribe(ObservableEmitter<List<UserPhoto>> emitter) throws Exception {
                VKRequest request = new VKRequest("photos.getAll", VKParameters.from(VKApiConst.USER_IDS, VKSdk.getAccessToken().userId, VKApiConst.FIELDS, "photo_200") );
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        try {
                            List<UserPhoto> result = new ArrayList<>();
                            JSONArray items = response.json.getJSONObject("response").getJSONArray("items");
                            for (int i=0; i<items.length(); i++) {
                                JSONObject photo = items.getJSONObject(i);
                                UserPhoto userPhoto = new UserPhoto();
                                userPhoto.setAccountType(AccountType.VK.name());
                                userPhoto.setUserId(userPrincipalId);
                                userPhoto.setIdOnAccount(photo.getString("id"));
                                userPhoto.setSourceLink(photo.getString("photo_604"));
                                result.add(userPhoto);
                            }
                            emitter.onNext(result);
                        } catch (JSONException e) {
                            emitter.onError(e);
                        }
                    }

                    @Override
                    public void onError(VKError error) {
                        emitter.onError(new RuntimeException(error.errorMessage));
                    }
                });
            }
        });
    }

    private List<UserPhoto> getFacebookPhotos() {
        GraphRequest request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "me/albums", null);
        Bundle bundle = new Bundle();
        bundle.putString("fields", "photos.fields(id,source)");
        request.setParameters(bundle);
        GraphResponse response = request.executeAndWait();
        try {
            List<UserPhoto> result = new ArrayList<>();
            JSONArray albumsData = response.getJSONObject().getJSONArray("data");
            for (int i=0; i<albumsData.length(); i++) {
                if (!albumsData.getJSONObject(i).has("photos")) {
                    continue;
                }
                JSONArray photos = albumsData.getJSONObject(i).getJSONObject("photos").getJSONArray("data");
                for (int j=0; j<photos.length(); j++) {
                    JSONObject photo = photos.getJSONObject(j);
                    UserPhoto userPhoto = new UserPhoto();
                    userPhoto.setAccountType(AccountType.FB.name());
                    userPhoto.setUserId(userPrincipalId);
                    userPhoto.setIdOnAccount(photo.getString("id"));
                    userPhoto.setSourceLink(photo.getString("source"));
                    result.add(userPhoto);
                }
            }
            return result;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
