package lt.dualpair.android.ui.user;


import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Photo;

public class EditPhotosRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

    private static final int PHOTO_ITEM = 1;
    private static final int ADD_PHOTO_ITEM = 2;

    public static final int NORMAL_MODE = 1;
    public static final int MOVE_MODE = 2;
    public static final int DELETE_MODE = 3;

    private static final int MAX_PHOTOS = 9;

    private static final float FIRST_POSITION_TEXT_SIZE = 40;
    private static final float POSITION_TEXT_SIZE = 30;

    private RecyclerView recyclerView;

    private List<Photo> photos;
    private View.OnClickListener onAddClickListener;
    private OnRemoveListener onRemoveListener;
    private OnStartDragListener onStartDragListener;

    private int mode = NORMAL_MODE;

    public EditPhotosRecyclerAdapter(List<Photo> photos,
                                     View.OnClickListener onAddClickListener,
                                     OnRemoveListener onRemoveListener,
                                     OnStartDragListener onStartDragListener) {
        this.photos = photos;
        this.onAddClickListener = onAddClickListener;
        this.onRemoveListener = onRemoveListener;
        this.onStartDragListener = onStartDragListener;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
        notifyItemInserted(photos.size() - 1);
    }

    public void setMode(int mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

    public int getMode() {
        return mode;
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

                photoHolder.delete.setVisibility(View.GONE);
                photoHolder.position.setVisibility(View.GONE);

                if (mode == DELETE_MODE) {
                    photoHolder.delete.setVisibility(View.VISIBLE);
                    photoHolder.delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            photos.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            onRemoveListener.onRemove(photo);
                        }
                    });
                } else if (mode == MOVE_MODE) {
                    setPositionTextSize(photoHolder.position, holder.getAdapterPosition());
                    photoHolder.position.setText(position + 1 + "");
                    photoHolder.position.setVisibility(View.VISIBLE);
                    photoHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (MotionEventCompat.getActionMasked(event) ==
                                    MotionEvent.ACTION_DOWN) {
                                onStartDragListener.onStartDrag(photoHolder);
                            }
                            return false;
                        }
                    });
                }

                break;

            case ADD_PHOTO_ITEM:
                final AddPhotoHolder addPhotoHolder = (AddPhotoHolder) holder;
                addPhotoHolder.itemView.setOnClickListener(onAddClickListener);
        }

    }

    private void setPositionTextSize(TextView tv, int position) {
        if (position == 0) {
            tv.setTextSize(FIRST_POSITION_TEXT_SIZE);
        } else {
            tv.setTextSize(POSITION_TEXT_SIZE);
        }
    }

    @Override
    public int getItemCount() {
        if (mode == NORMAL_MODE) {
            return photos.size() + 1;
        } else {
            return photos.size();
        }
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

    public interface OnRemoveListener {
        void onRemove(Photo photo);
    }

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
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
        setPositionTextSize(tv, position);
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
