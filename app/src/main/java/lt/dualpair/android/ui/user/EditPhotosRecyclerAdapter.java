package lt.dualpair.android.ui.user;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.EmptySubscriber;
import lt.dualpair.android.data.manager.UserDataManager;
import lt.dualpair.android.data.resource.Photo;
import lt.dualpair.android.data.resource.User;
import lt.dualpair.android.utils.ToastUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class EditPhotosRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int PHOTO_ITEM = 1;
    private static final int ADD_PHOTO_ITEM = 2;

    private List<Photo> photos;

    public EditPhotosRecyclerAdapter(List<Photo> photos) {
        this.photos = photos;
    }

    @Override
    public int getItemViewType(int position) {
        return position == photos.size() ? ADD_PHOTO_ITEM : PHOTO_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case PHOTO_ITEM:
                v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.edit_photos_photo_layout, parent, false);
                return new EditPhotosRecyclerAdapter.PhotoHolder(parent.getContext(), v);
            case ADD_PHOTO_ITEM:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.edit_photos_add_photo_layout, parent, false);
                return new EditPhotosRecyclerAdapter.AddPhotoHolder(parent.getContext(), v);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case PHOTO_ITEM:
                final PhotoHolder photoHolder = (PhotoHolder) holder;
                final Context context = photoHolder.context;
                final Photo photo = photos.get(position);
                Picasso.with(context)
                        .load(photo.getSourceUrl())
                        .error(R.drawable.image_not_found)
                        .into(photoHolder.photo);

                photoHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new UserDataManager(context).deletePhoto(photo)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new EmptySubscriber<User>() {
                                    @Override
                                    public void onCompleted() {
                                        photos.remove(photoHolder.getAdapterPosition());
                                        notifyDataSetChanged();
                                    }
                                });
                    }
                });
                break;
            case ADD_PHOTO_ITEM:
                final AddPhotoHolder addPhotoHolder = (AddPhotoHolder) holder;
                addPhotoHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastUtils.show(addPhotoHolder.context, "Select photo dialog...");
                    }
                });
        }

    }

    @Override
    public int getItemCount() {
        return photos.size() + 1;
    }

    protected static class PhotoHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.photo)  protected ImageView photo;
        @Bind(R.id.delete) protected ImageView delete;

        private Context context;

        protected PhotoHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            ButterKnife.bind(this, itemView);
        }
    }

    protected static class AddPhotoHolder extends RecyclerView.ViewHolder {

        private Context context;

        protected AddPhotoHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            ButterKnife.bind(this, itemView);
        }

    }

}
