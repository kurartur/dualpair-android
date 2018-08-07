package lt.dualpair.android.data.remote.client.user;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import lt.dualpair.android.data.remote.client.ObservableClient;
import lt.dualpair.android.data.remote.resource.PhotoResource;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

public class SetPhotosClient extends ObservableClient<List<PhotoResource>> {

    private Long userId;
    private List<PhotoResource> photos;

    public SetPhotosClient(Long userId, List<PhotoResource> photos) {
        this.userId = userId;
        this.photos = photos;
    }

    @Override
    protected Observable<List<PhotoResource>> getApiObserable(Retrofit retrofit) {
        try {
            List<MultipartBody.Part> parts = new ArrayList<>();
            for (PhotoResource photo : photos) {
                if (photo.getId() == null) {
                    RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), downloadImage(photo.getSource()));
                    String id = photos.indexOf(photo) + "";
                    MultipartBody.Part file = MultipartBody.Part.createFormData("photoFiles", id, requestBody);
                    parts.add(file);
                    photo.setSource(id);
                }
            }
            RequestBody data = RequestBody.create(MediaType.parse("multipart/form-data"), new Gson().toJson(new PhotoResourceCollection(photos)));
            return retrofit.create(UserService.class)
                    .setPhotos(userId, parts, data);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return Observable.error(ioe);
        }
    }

    private byte[] downloadImage(String source) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = new URL(source).openStream ();
            byte[] byteChunk = new byte[4096];
            int n;

            while ( (n = is.read(byteChunk)) > 0 ) {
                baos.write(byteChunk, 0, n);
            }
            return baos.toByteArray();
        } finally {
            if (is != null) { is.close(); }
        }
    }

    public static final class PhotoResourceCollection {

        private List<PhotoResource> photoResources;

        public PhotoResourceCollection(List<PhotoResource> photoResources) {
            this.photoResources = photoResources;
        }

        public List<PhotoResource> getPhotoResources() {
            return photoResources;
        }

    }
}
