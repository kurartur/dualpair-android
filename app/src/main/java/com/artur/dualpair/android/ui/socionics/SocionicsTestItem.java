package com.artur.dualpair.android.ui.socionics;

import com.artur.dualpair.android.dto.Choice;
import com.artur.dualpair.android.dto.ChoicePair;

public class SocionicsTestItem {

    private ChoicePair choicePair;
    private Choice selectedChoice;

    public SocionicsTestItem(ChoicePair choicePair) {
        this.choicePair = choicePair;
    }

    public String getId() {
        return choicePair.getId();
    }

    public Choice getChoice1() {
        return choicePair.getChoice1();
    }

    public Choice getChoice2() {
        return choicePair.getChoice2();
    }

    public int getChoinceNumber(Choice choice) {
        return choicePair.getChoiceNumber(choice);
    }

    public void setSelected(int selected) {
        if (selected < 0 || selected > 2) {
            throw new IllegalArgumentException("Selected value must be 1 or 2");
        }
        this.selectedChoice = selected == 0 ? null : selected == 1 ? getChoice1() : getChoice2();
    }

    public void setSelected(Choice choice) {
        if (getChoice1() != choice && getChoice2() != choice) {
            throw new IllegalArgumentException("Invalid choice");
        }
        this.selectedChoice = choice;
    }

    public boolean isSomethingChosen() {
        return selectedChoice != null;
    }

    public Choice getSelectedChoice() {
        return selectedChoice;
    }
}
