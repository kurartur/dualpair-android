package lt.dualpair.android.data.task.user;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lt.dualpair.android.data.remote.client.user.AddPhotoClient;
import lt.dualpair.android.data.repo.DatabaseHelper;
import lt.dualpair.android.data.repo.UserRepository;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.data.task.AuthenticatedUserTask;

public class AddPhotoTask extends AuthenticatedUserTask<User> {

    private Photo photo;
    private UserRepository userRepository;

    public AddPhotoTask(Context context, Photo photo) {
        super(context);
        this.photo = photo;
        SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
        userRepository = new UserRepository(db);
    }

    @Override
    protected User run() throws Exception {
        Photo photo = new AddPhotoClient(getUserId(), this.photo).observable().toBlocking().first();
        User user = userRepository.get(getUserId());
        user.getPhotos().add(photo);
        userRepository.save(user);
        return user;
    }
}
