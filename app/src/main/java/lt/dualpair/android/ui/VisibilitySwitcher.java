package lt.dualpair.android.ui;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class VisibilitySwitcher {

    public View parentView;
    private List<View> views = new ArrayList<>();

    public VisibilitySwitcher(View parentView, Integer ...ids) {
        this.parentView = parentView;
        for (Integer id : ids) {
            views.add(parentView.findViewById(id));
        }
    }

    public void switchTo(Integer id) {
        for (View view : views) {
            if (view.getId() == id) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }
}
