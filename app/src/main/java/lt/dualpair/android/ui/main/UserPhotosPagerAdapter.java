package lt.dualpair.android.ui.main;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Photo;

public class UserPhotosPagerAdapter extends PagerAdapter {

    private Context context;
    private List<Photo> userPhotos;

    public UserPhotosPagerAdapter(Context context, List<Photo> userPhotos) {
        this.context = context;
        this.userPhotos = userPhotos;
    }

    @Override
    public int getCount() {
        return userPhotos.size();
    }

    @Override
    public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
        arg0.removeView((View) arg2);
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return userPhotos.indexOf(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.photo_layout,null);
        container.addView(view);
        final ImageView photo = (ImageView) view.findViewById(R.id.picture);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        Picasso.with(context)
            .load(userPhotos.get(position).getSourceUrl())
            .error(R.drawable.image_not_found)
            .into(photo, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                    photo.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    progressBar.setVisibility(View.GONE);
                    photo.setVisibility(View.VISIBLE);
                }
            });
        return view;
    }
}
