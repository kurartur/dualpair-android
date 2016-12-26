package lt.dualpair.android.ui.user;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Photo;

public class EditPhotosRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private static final int PHOTO_ITEM = 1;
    private static final int ADD_PHOTO_ITEM = 2;

    private static final int MAX_PHOTOS = 9;

    private RecyclerView recyclerView;

    private List<Photo> photos = new ArrayList<>();

    private OnAddClickListener onAddClickListener;
    private OnStartDragListener onStartDragListener;

    public EditPhotosRecyclerAdapter(List<Photo> photos,
                                     OnAddClickListener onAddClickListener,
                                     OnStartDragListener onStartDragListener) {

        this.photos.addAll(photos);
        sortByPosition();

        this.onAddClickListener = onAddClickListener;
        this.onStartDragListener = onStartDragListener;
    }

    private void sortByPosition() {
        Collections.sort(this.photos, new Comparator<Photo>() {
            @Override
            public int compare(Photo o1, Photo o2) {
                return o1.getPosition() > o2.getPosition() ? 1 : -1;
            }
        });
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
        notifyItemInserted(photos.size() - 1);
        if (photos.size() == MAX_PHOTOS) {
            notifyItemRemoved(MAX_PHOTOS + 1);
        }
    }

    public List<Photo> getPhotos() {
        return photos;
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case PHOTO_ITEM:
                final PhotoHolder photoHolder = (PhotoHolder) holder;
                final Context context = photoHolder.context;
                final Photo photo = photos.get(position);
                Picasso.with(context)
                        .load(photo.getSourceUrl())
                        .error(R.drawable.image_not_found)
                        .into(photoHolder.photo);

                photoHolder.delete.setVisibility(View.VISIBLE);
                photoHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        photos.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        for (int i=holder.getAdapterPosition(); i < photos.size(); i++) {
                            updatePositionText(i);
                        }
                    }
                });

                photoHolder.position.setText(position + 1 + "");
                photoHolder.position.setVisibility(View.VISIBLE);
                photoHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onStartDragListener.onStartDrag(photoHolder);
                        return true;
                    }
                });

                break;

            case ADD_PHOTO_ITEM:
                final AddPhotoHolder addPhotoHolder = (AddPhotoHolder) holder;
                addPhotoHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAddClickListener.onAddClick();
                    }
                });
        }

    }

    @Override
    public int getItemCount() {
        return photos.size() < MAX_PHOTOS ? photos.size() + 1 : photos.size();
    }

    protected static class PhotoHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.photo)  protected ImageView photo;
        @Bind(R.id.delete) protected ImageView delete;
        @Bind(R.id.position) protected TextView position;

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

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public interface OnAddClickListener {
        void onAddClick();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                photos.get(i).setPosition(i + 1);
                photos.get(i + 1).setPosition(i);
                Collections.swap(photos, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                photos.get(i).setPosition(i - 1);
                photos.get(i - 1).setPosition(i);
                Collections.swap(photos, i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i <= toPosition; i++) {
                updatePositionText(i);
            }
        } else {
            for (int i = fromPosition; i >= toPosition; i--) {
                updatePositionText(i);
            }
        }
    }

    private void updatePositionText(int position) {
        TextView tv = (TextView)recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.position);
        tv.setText(position + 1 + "");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = null;
    }
}
