package lt.dualpair.android.data.resource;

public class ChoicePair {

    String id;
    Choice choice1;
    Choice choice2;
    Choice selected;

    public ChoicePair(String id, Choice choice1, Choice choice2) {
        this.id = id;
        this.choice1 = choice1;
        this.choice2 = choice2;
    }

    public String getId() {
        return id;
    }

    public Choice getChoice1() {
        return choice1;
    }

    public Choice getChoice2() {
        return choice2;
    }

    public int getChoiceNumber(Choice choice) {
        if (choice == choice1) {
            return 1;
        } else if (choice == choice2) {
            return 2;
        } else {
            throw new IllegalArgumentException(choice + " is not part of this choice pair");
        }
    }

    public void setSelected(Choice choice) {
        if (choice != choice1 && choice != choice2) {
            throw new IllegalArgumentException("Invalid choice");
        }
        selected = choice;
    }

    public boolean isChoice1Selected() {
        return selected == choice1;
    }

    public boolean isChoice2Selected() {
        return selected == choice2;
    }

    public boolean isAnySelected() {
        return selected != null;
    }
}
