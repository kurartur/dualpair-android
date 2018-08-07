package lt.dualpair.android.ui.user;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.UserPhoto;

public class EditPhotosRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter {

    private static final int PHOTO_ITEM = 1;
    private static final int ADD_PHOTO_ITEM = 2;

    private static final int MAX_PHOTOS = 6;

    private final ItemTouchHelper itemTouchHelper;

    private List<UserPhoto> photos = new ArrayList<>();

    private OnAddClickListener onAddClickListener;

    public EditPhotosRecyclerAdapter(List<UserPhoto> photos,
                                     OnAddClickListener onAddClickListener) {

        this.photos.addAll(photos);
        sortByPosition();

        this.onAddClickListener = onAddClickListener;

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(this);
        itemTouchHelper = new ItemTouchHelper(callback);
    }

    @Override
    public int getItemViewType(int position) {
        return position == photos.size() ? ADD_PHOTO_ITEM : PHOTO_ITEM;
    }

    @Override
    public int getItemCount() {
        int count = photos.size() < MAX_PHOTOS ? photos.size() + 1 : photos.size();
        return count;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case PHOTO_ITEM:
                v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.edit_photos_photo_layout, parent, false);
                return new EditPhotosRecyclerAdapter.PhotoHolder(v, itemTouchHelper);
            case ADD_PHOTO_ITEM:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.edit_photos_add_photo_layout, parent, false);
                return new EditPhotosRecyclerAdapter.AddPhotoHolder(v);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case PHOTO_ITEM:
                final PhotoHolder photoHolder = (PhotoHolder) holder;
                photoHolder.setPhoto(photos.get(position), photos.size() < 2, position, new OnDeleteClickListener() {
                    @Override
                    public void onDeleteClick(UserPhoto userPhoto) {
                        photos.remove(userPhoto);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                        if (photos.size() == 1) {
                            notifyItemChanged(0);
                        }
                        resetPositions();
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

    private void sortByPosition() {
        Collections.sort(this.photos, (o1, o2) -> o1.getPosition() > o2.getPosition() ? 1 : -1);
    }

    public void addPhoto(UserPhoto photo) {
        photos.add(photo);

        // insert item to view
        notifyItemInserted(photos.size() - 1);

        // if it is the last photo, remove the "add" item
        if (photos.size() == MAX_PHOTOS) {
            notifyItemRemoved(MAX_PHOTOS + 1);
        }

        // if we are adding second photo, redraw first item, because delete and position are hidden
        if (photos.size() == 2) {
            notifyItemChanged(0);
        }

        resetPositions();
    }

    public List<UserPhoto> getPhotos() {
        return photos;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(photos, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(photos, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        resetPositions();
    }

    @Override
    public void onItemMoved() {
        notifyItemRangeChanged(0, getItemCount());
    }

    private void resetPositions() {
        for (UserPhoto photo : photos) {
            photo.setPosition(photos.indexOf(photo) + 1);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        itemTouchHelper.attachToRecyclerView(null);
    }

    public interface OnAddClickListener {
        void onAddClick();
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(UserPhoto userPhoto);
    }

    protected static class PhotoHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.photo)  protected ImageView photoView;
        @Bind(R.id.delete) protected ImageView deleteView;
        @Bind(R.id.position) protected TextView positionView;

        private ItemTouchHelper itemTouchHelper;

        protected PhotoHolder(View itemView, ItemTouchHelper itemTouchHelper) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.itemTouchHelper = itemTouchHelper;
        }

        public void setPhoto(UserPhoto photo, boolean isLast, int position, OnDeleteClickListener onDeleteClickListener) {
            Picasso.with(itemView.getContext())
                    .load(photo.getSourceLink())
                    .error(R.drawable.person)
                    .placeholder(R.drawable.person)
                    .into(photoView);

            if (!isLast) {
                deleteView.setVisibility(View.VISIBLE);
                deleteView.setOnClickListener(v -> {
                    onDeleteClickListener.onDeleteClick(photo);
                });

                positionView.setText(position + 1 + "");
                positionView.setVisibility(View.VISIBLE);
                itemView.setOnLongClickListener(v -> {
                    itemTouchHelper.startDrag(this);
                    return true;
                });
            } else {
                deleteView.setVisibility(View.GONE);
                positionView.setVisibility(View.GONE);
            }
        }

    }

    protected static class AddPhotoHolder extends RecyclerView.ViewHolder {

        protected AddPhotoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
