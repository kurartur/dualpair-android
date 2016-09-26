package lt.dualpair.android.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.resource.Sociotype;
import lt.dualpair.android.ui.socionics.SocionicsTestActivity;

public class AddSociotypeActivity extends Activity {

    private static final int CONFIRM_REQUEST_CODE = 1;

    @Bind(R.id.button_start_test)
    LinearLayout startTestButton;

    @Bind(R.id.grid_sociotypes)
    GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_sociotype);
        ButterKnife.bind(this);
        fillGrid();
        startTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTest();
            }
        });
    }

    private void openTest() {
        Intent intent = new Intent(this, SocionicsTestActivity.class);
        startActivity(intent);
    }

    private void fillGrid() {
        for (final Sociotype sociotype : getSociotypes()) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_grid_sociotype, gridLayout, false);
            TextView code = (TextView)view.findViewById(R.id.sociotype_code);
            code.setText(sociotype.getCode1() + " (" + sociotype.getCode2() + ")");
            TextView title = (TextView)view.findViewById(R.id.sociotype_title);
            title.setText(getResources().getString(getResources().getIdentifier(sociotype.getCode1().toLowerCase() + "_title", "string", getPackageName())));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openConfirmation(sociotype);
                }
            });
            gridLayout.addView(view);
        }
    }

    private void openConfirmation(Sociotype sociotype) {
        startActivityForResult(ConfirmSociotypeActivity.createIntent(this, sociotype), CONFIRM_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CONFIRM_REQUEST_CODE:
                if (Activity.RESULT_OK == resultCode) {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
                break;
        }
    }

    private List<Sociotype> getSociotypes() {
        // hardcoded
        List<Sociotype> sociotypes = new ArrayList<>();
        sociotypes.add(createSociotype("LII", "INTJ"));
        sociotypes.add(createSociotype("ILE", "ENTP"));
        sociotypes.add(createSociotype("ESE", "ESFJ"));
        sociotypes.add(createSociotype("SEI", "ISFP"));
        sociotypes.add(createSociotype("LSI", "ISTJ"));
        sociotypes.add(createSociotype("SLE", "ESTP"));
        sociotypes.add(createSociotype("EIE", "ENFJ"));
        sociotypes.add(createSociotype("IEI", "INFP"));
        sociotypes.add(createSociotype("ESI", "ISFJ"));
        sociotypes.add(createSociotype("SEE", "ESFP"));
        sociotypes.add(createSociotype("LIE", "ENTJ"));
        sociotypes.add(createSociotype("ILI", "INTP"));
        sociotypes.add(createSociotype("EII", "INFJ"));
        sociotypes.add(createSociotype("IEE", "ENFP"));
        sociotypes.add(createSociotype("LSE", "ESTJ"));
        sociotypes.add(createSociotype("SLI", "ISTP"));
        return sociotypes;
    }

    private Sociotype createSociotype(String code1, String code2) {
        Sociotype sociotype = new Sociotype();
        sociotype.setCode1(code1);
        sociotype.setCode2(code2);
        return sociotype;
    }

    public static Intent createIntent(Activity activity) {
        return new Intent(activity, AddSociotypeActivity.class);
    }
}
