package lt.dualpair.android.ui.user;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Photo;

public class AvailableProviderPhotosRecyclerAdapter extends RecyclerView.Adapter<AvailableProviderPhotosRecyclerAdapter.PhotoHolder> {

    private List<Photo> photos;
    private OnPhotoClickListener onPhotoClickListener;

    public AvailableProviderPhotosRecyclerAdapter(List<Photo> photos, OnPhotoClickListener onPhotoClickListener) {
        this.photos = photos;
        this.onPhotoClickListener = onPhotoClickListener;
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.available_provider_photos_photo_layout, parent, false);
        return new PhotoHolder(parent.getContext(), v);
    }

    @Override
    public void onBindViewHolder(final PhotoHolder holder, int position) {
        Picasso.with(holder.context)
                .load(photos.get(position).getSourceUrl())
                .error(R.drawable.image_not_found)
                .into((ImageView)holder.itemView);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPhotoClickListener.onClick(photos.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    protected static class PhotoHolder extends RecyclerView.ViewHolder {

        private Context context;

        protected PhotoHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
        }
    }

    public interface OnPhotoClickListener {
        void onClick(Photo photo);
    }
}
