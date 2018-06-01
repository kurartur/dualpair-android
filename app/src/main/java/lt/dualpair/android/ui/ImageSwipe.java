package lt.dualpair.android.ui;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.UserPhoto;

public class ImageSwipe extends LinearLayout {

    private ImageView[] dotImages;

    private ViewPager photoPager;
    private LinearLayout photoDots;

    public ImageSwipe(Context context) {
        super(context);
        initView();
    }

    public ImageSwipe(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ImageSwipe(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ImageSwipe(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.image_swipe_layout, null);

        photoPager = view.findViewById(R.id.photo_pager);
        photoDots = view.findViewById(R.id.photo_dots);

        photoPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotImages.length; i++) {
                    dotImages[i].setImageDrawable(getContext().getResources().getDrawable(R.drawable.non_selected_item_dot));
                }
                dotImages[position].setImageDrawable(getContext().getResources().getDrawable(R.drawable.selected_item_dot));
            }
        });

        addView(view);
    }

    public void setPhotos(List<UserPhoto> photos) {
        photoPager.setAdapter(new ImagePagerAdapter(getContext(), photos));
        photoDots.removeAllViews();
        dotImages = new ImageView[photos.size()];
        for (int i = 0; i < photos.size(); i++) {
            dotImages[i] = new ImageView(getContext());
            if (i == 0) {
                dotImages[i].setImageDrawable(getContext().getResources().getDrawable(R.drawable.selected_item_dot));
            } else {
                dotImages[i].setImageDrawable(getContext().getResources().getDrawable(R.drawable.non_selected_item_dot));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(7, 0, 7, 0);
            photoDots.addView(dotImages[i], params);
        }
    }

    public static class ImagePagerAdapter extends PagerAdapter {

        private Context context;
        private List<UserPhoto> userPhotos;

        public ImagePagerAdapter(Context context, List<UserPhoto> userPhotos) {
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
            View view = inflater.inflate(R.layout.image_swipe_image_layout,null);
            container.addView(view);
            final ImageView photo = view.findViewById(R.id.picture);
            final ProgressBar progressBar = view.findViewById(R.id.progress_bar);
            Picasso.with(context)
                    .load(userPhotos.get(position).getSourceLink())
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

}
