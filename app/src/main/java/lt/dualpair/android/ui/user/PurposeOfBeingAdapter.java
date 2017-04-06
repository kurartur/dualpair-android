package lt.dualpair.android.ui.user;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.PurposeOfBeing;
import lt.dualpair.android.utils.LabelUtils;

public class PurposeOfBeingAdapter extends BaseAdapter {

    private EditUserActivity editUserActivity;
    private PurposeOfBeing[] purposes = PurposeOfBeing.values();
    private Set<PurposeOfBeing> checkedPurposes = new HashSet<>();
    private OnPurposeListChangeListener onPurposeListChangeListener;

    public PurposeOfBeingAdapter(EditUserActivity editUserActivity, OnPurposeListChangeListener onPurposeListChangeListener) {
        this.editUserActivity = editUserActivity;
        this.onPurposeListChangeListener = onPurposeListChangeListener;
    }

    @Override
    public int getCount() {
        return purposes.length;
    }

    @Override
    public Object getItem(int position) {
        return purposes[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PurposeOfBeing purposeOfBeing = (PurposeOfBeing)getItem(position);
        LinearLayout ll = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_with_label_layout, null);

        final CheckBox checkBox = (CheckBox) ll.findViewById(R.id.checkbox);
        if (checkedPurposes.contains(purposeOfBeing))
            checkBox.setChecked(true);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox)v).isChecked()) {
                    checkedPurposes.add(purposeOfBeing);
                } else {
                    checkedPurposes.remove(purposeOfBeing);
                }
                onPurposeListChangeListener.onChange(checkedPurposes);
            }
        });

        TextView text = (TextView)ll.findViewById(R.id.text);
        text.setText(LabelUtils.getPurposeOfBeingLabel(editUserActivity, purposeOfBeing));
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.performClick();
            }
        });

        return ll;
    }

    public void setCheckedPurposes(Set<PurposeOfBeing> checkedPurposes) {
        this.checkedPurposes = checkedPurposes;
        notifyDataSetChanged();
    }

    public interface OnPurposeListChangeListener {
        void onChange(Set<PurposeOfBeing> purposes);
    }

}
