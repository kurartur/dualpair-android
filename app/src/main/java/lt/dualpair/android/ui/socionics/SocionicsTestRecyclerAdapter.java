package lt.dualpair.android.ui.socionics;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Choice;
import lt.dualpair.android.data.resource.ChoicePair;

public class SocionicsTestRecyclerAdapter extends RecyclerView.Adapter<SocionicsTestRecyclerAdapter.ChoiceViewHolder> {

    private List<ChoicePair> items = new ArrayList<>();
    private OnChoiceListener onChoiceListener;

    public SocionicsTestRecyclerAdapter(List<ChoicePair> choicePairs, OnChoiceListener onChoiceListener) {
        this.items = choicePairs;
        this.onChoiceListener = onChoiceListener;
    }

    public void setItems(List<ChoicePair> items) {
        this.items = items;
    }

    @Override
    public ChoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.socionics_test_choice, parent, false);
        ChoiceViewHolder vh = new ChoiceViewHolder(parent.getContext(), v);
        ButterKnife.bind(vh, v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ChoiceViewHolder holder, int position) {
        holder.setupChoicePair(items.get(position), onChoiceListener, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void notifyItemChanged(String id) {
        for (int i=0; i<items.size(); i++) {
            if (items.get(i).getId().equals(id)) {
                notifyItemChanged(i);
            }
        }
    }

    public static class ChoiceViewHolder extends RecyclerView.ViewHolder {

        Context context;
        @Bind(R.id.choice1_layout) View choice1Layout;
        @Bind(R.id.choice2_layout) View choice2Layout;
        @Bind(R.id.choice1_text) TextView choice1Text;
        @Bind(R.id.choice2_text) TextView choice2Text;

        public ChoiceViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
        }

        public void setupChoicePair(ChoicePair choicePair, OnChoiceListener onChoiceListener, int position) {
            setupChoice1(choicePair.getId(), choicePair.getChoice1(), onChoiceListener, position);
            setupChoice2(choicePair.getId(), choicePair.getChoice2(), onChoiceListener, position);
            setSelected(choicePair);
        }

        private void setupChoice1(final String id, final Choice choice, final OnChoiceListener onChoiceListener, final int position) {
            choice1Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChoiceListener.onChoice(id, position, choice);
                }
            });
            choice1Text.setText(context.getResources().getString(getChoiceStringId(choice)));
        }

        private void setupChoice2(final String id, final Choice choice, final OnChoiceListener onChoiceListener, final int position) {
            choice2Layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onChoiceListener.onChoice(id, position, choice);
                }
            });
            choice2Text.setText(context.getResources().getString(getChoiceStringId(choice)));
        }

        private void setSelected(ChoicePair choicePair) {
            choice1Layout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            choice2Layout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            choice1Text.setTypeface(null, Typeface.NORMAL);
            choice2Text.setTypeface(null, Typeface.NORMAL);
            if (choicePair.isChoice1Selected()) {
                choice1Layout.setBackgroundColor(ContextCompat.getColor(context, R.color.light_green));
                choice1Text.setTypeface(null, Typeface.BOLD);
            } else if (choicePair.isChoice2Selected()) {
                choice2Layout.setBackgroundColor(ContextCompat.getColor(context, R.color.light_green));
                choice2Text.setTypeface(null, Typeface.BOLD);
            }
        }

        private int getChoiceStringId(Choice choice) {
            return context.getResources().getIdentifier(choice.name().toLowerCase(), "string", context.getPackageName());
        }

    }

    public interface OnChoiceListener {
        void onChoice(String id, int position, Choice choice);
    }

}
