package lt.dualpair.android.ui.user;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import lt.dualpair.android.data.local.entity.RelationshipStatus;
import lt.dualpair.android.utils.LabelUtils;

class RelationshipStatusAdapter extends ArrayAdapter<RelationshipStatus> {

    private EditUserActivity editUserActivity;
    private RelationshipStatus[] statuses;

    public RelationshipStatusAdapter(EditUserActivity editUserActivity, Context context, int resource, RelationshipStatus[] statuses) {
        super(context, resource, statuses);
        this.editUserActivity = editUserActivity;
        this.statuses = statuses;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new KNoFilter();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = (TextView) super.getView(position, convertView, parent);
        tv.setText(LabelUtils.getRelationshipStatusLabel(editUserActivity, getItem(position)));
        return tv;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView tv = (TextView) super.getView(position, convertView, parent);
        tv.setText(LabelUtils.getRelationshipStatusLabel(editUserActivity, getItem(position)));
        return tv;
    }

    private class KNoFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence arg0) {
            FilterResults result = new FilterResults();
            result.values = statuses;
            result.count = statuses.length;
            return result;
        }

        @Override
        protected void publishResults(CharSequence arg0, FilterResults arg1) {
            notifyDataSetChanged();
        }
    }

}
