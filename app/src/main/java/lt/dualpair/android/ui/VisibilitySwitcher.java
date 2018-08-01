package lt.dualpair.android.ui;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class VisibilitySwitcher {

    private List<View> views = new ArrayList<>();

    public VisibilitySwitcher(View parentView, Integer ...ids) {
        for (Integer id : ids) {
            View viewById = parentView.findViewById(id);
            if (viewById == null) throw new NullPointerException("View not found");
            views.add(viewById);
        }
    }

    public void switchTo(Integer id) {
        boolean found = false;
        for (View view : views) {
            if (view.getId() == id) {
                view.setVisibility(View.VISIBLE);
                found = true;
            } else {
                view.setVisibility(View.GONE);
            }
        }
        if (!found) {
            throw new IllegalArgumentException("View not found");
        }
    }
}
