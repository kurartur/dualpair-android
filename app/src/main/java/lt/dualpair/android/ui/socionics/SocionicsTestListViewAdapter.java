package lt.dualpair.android.ui.socionics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Choice;
import lt.dualpair.android.data.resource.ChoicePair;

public class SocionicsTestListViewAdapter extends BaseAdapter implements View.OnClickListener {

    private List<SocionicsTestItem> items = new ArrayList<>();
    private SocionicsTestActivity activity;
    private Button submitButton;

    public SocionicsTestListViewAdapter(SocionicsTestActivity activity, List<ChoicePair> choicePairs, Button submitButton) {
        this.activity = activity;
        this.submitButton = submitButton;
        Iterator<ChoicePair> choicePairsIterator = choicePairs.iterator();
        while (choicePairsIterator.hasNext()) {
            items.add(new SocionicsTestItem(choicePairsIterator.next()));
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.item_socionics_test, parent, false);

        SocionicsTestItem item = (SocionicsTestItem)getItem(position);
        setupChoice(position, item.getChoice1(), view, 1);
        setupChoice(position, item.getChoice2(), view, 2);
        if (item.isSomethingChosen()) {
            selectChoice(view, item.getChoinceNumber(item.getSelectedChoice()));
        }
        return view;
    }

    private void setupChoice(int position, Choice choice, View view, int choiceNumber) {
        LinearLayout choiceLayout = (LinearLayout)view.findViewById(getChoiceLayoutId(choiceNumber));
        choiceLayout.setTag(new RowTag(position, choice));
        choiceLayout.setOnClickListener(this);
        TextView choiceText = (TextView)choiceLayout.findViewById(getChoiceTextId(choiceNumber));
        choiceText.setText(activity.getResources().getString(getChoiceStringId(choice)));
        ImageView choiceTick = (ImageView)choiceLayout.findViewById(getChoiceTickId(choiceNumber));
        choiceTick.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        RowTag rowTag = (RowTag)view.getTag();
        SocionicsTestItem item = (SocionicsTestItem)getItem(rowTag.getPosition());
        if (item.isSomethingChosen()) deselectChoice((View)view.getParent(), item.getChoinceNumber(item.getSelectedChoice()));
        item.setSelected(rowTag.getChoice());
        selectChoice((View) view.getParent(), item.getChoinceNumber(item.getSelectedChoice()));
        int selectedItemCount = countSelectedItems();
        int totalItemCount = getCount();
        activity.getActionBar().setTitle(SocionicsTestActivity.TitleCreator.createTitle(activity, selectedItemCount, totalItemCount));
        if (selectedItemCount == totalItemCount) {
            submitButton.setEnabled(true);
        }
    }

    private void deselectChoice(View view, int choiceNumber) {
        View layout = view.findViewById(getChoiceLayoutId(choiceNumber));
        ImageView choiceTick = (ImageView)layout.findViewById(getChoiceTickId(choiceNumber));
        choiceTick.setVisibility(View.INVISIBLE);
    }

    private void selectChoice(View view, int choiceNumber) {
        View layout = view.findViewById(getChoiceLayoutId(choiceNumber));
        ImageView choiceTick = (ImageView)layout.findViewById(getChoiceTickId(choiceNumber));
        choiceTick.setVisibility(View.VISIBLE);
    }

    private int getChoiceStringId(Choice choice) {
        return activity.getResources().getIdentifier(choice.name().toLowerCase(), "string", activity.getPackageName());
    }

    private int getChoiceLayoutId(int choiceNumber) {
        return activity.getResources().getIdentifier("choice" + choiceNumber + "_layout", "id", activity.getPackageName());
    }

    private int getChoiceTickId(int choiceNumber) {
        return activity.getResources().getIdentifier("choice" + choiceNumber + "_tick", "id", activity.getPackageName());
    }

    private int getChoiceTextId(int choiceNumber) {
        return activity.getResources().getIdentifier("choice" + choiceNumber + "_text", "id", activity.getPackageName());
    }

    private int countSelectedItems() {
        int count = 0;
        for (SocionicsTestItem item : items) {
            if (item.isSomethingChosen()) {
                count++;
            }
        }
        return count;
    }

    public Map<String, Choice> getResults() {
        Map<String, Choice> results = new HashMap<>();
        for(SocionicsTestItem item : items) {
            if (item.isSomethingChosen()) {
                results.put(item.getId(), item.getSelectedChoice());
            }
        }
        return results;
    }

    private static class RowTag {
        private int position;
        private Choice choice;

        public RowTag(int position, Choice choice) {
            this.position = position;
            this.choice = choice;
        }

        public int getPosition() {
            return position;
        }

        public Choice getChoice() {
            return choice;
        }
    }
}
