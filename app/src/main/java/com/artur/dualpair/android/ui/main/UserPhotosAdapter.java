package com.artur.dualpair.android.ui.main;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.artur.dualpair.android.R;
import com.artur.dualpair.android.dto.Photo;
import com.artur.dualpair.android.utils.ToastUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserPhotosAdapter extends PagerAdapter {

    private Context context;
    private List<Photo> userPhotos;

    public UserPhotosAdapter(Context context, List<Photo> userPhotos) {
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
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_user_photo,null);
        container.addView(view);
        final ImageView photo = (ImageView) view.findViewById(R.id.photo);
        Picasso.with(context)
            .load(userPhotos.get(position).getSourceLink())
            .into(photo, new Callback() {
                @Override
                public void onSuccess() {
                    ToastUtils.show((Activity)context, "Success");
                }

                @Override
                public void onError() {
                    ToastUtils.show((Activity)context, "Error");
                }
            });
        return view;
    }
}
