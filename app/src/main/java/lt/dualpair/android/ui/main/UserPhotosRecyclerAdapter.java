package lt.dualpair.android.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Photo;


public class UserPhotosRecyclerAdapter extends RecyclerView.Adapter<UserPhotosRecyclerAdapter.PhotoHolder> {

    private List<Photo> photos;
    private OnPhotoClickListener onPhotoClickListener;

    public UserPhotosRecyclerAdapter(List<Photo> photos, OnPhotoClickListener onPhotoClickListener) {
        this.photos = photos;
        this.onPhotoClickListener = onPhotoClickListener;
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView v = (ImageView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.square_photo_layout, parent, false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(250, 250);
        v.setLayoutParams(layoutParams);
        return new PhotoHolder(parent.getContext(), v);
    }

    @Override
    public void onBindViewHolder(final PhotoHolder holder, final int position) {
        ImageView imageView = (ImageView)holder.itemView;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPhotoClickListener.onPhotoClick(photos.get(position));
            }
        });
        Picasso.with(holder.context)
                .load(photos.get(position).getSourceUrl())
                .error(R.drawable.image_not_found)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    protected static class PhotoHolder extends RecyclerView.ViewHolder {

        protected Context context;

        protected PhotoHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
        }
    }

    public interface OnPhotoClickListener {
        void onPhotoClick(Photo photo);
    }
}
