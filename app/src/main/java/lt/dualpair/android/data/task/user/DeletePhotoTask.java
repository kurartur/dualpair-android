package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.remote.client.user.DeletePhotoClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.task.AuthenticatedUserTask;

public class DeletePhotoTask extends AuthenticatedUserTask<User> {

    private Photo photo;
    private UserRepository userRepository;

    public DeletePhotoTask(Context context, Photo photo) {
        super(context);
        this.photo = photo;
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        userRepository = new UserRepository(db);
    }

    @Override
    protected User run() throws Exception {
        User user = userRepository.get(getUserId());
        new DeletePhotoClient(user, photo).observable().toBlocking().first();
        user.getPhotos().remove(photo);
        userRepository.save(user);
        return user;
    }
}
