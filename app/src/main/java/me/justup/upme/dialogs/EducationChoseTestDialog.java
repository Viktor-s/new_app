package me.justup.upme.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import me.justup.upme.R;
import me.justup.upme.entity.EducationGetTestsResponse;
import me.justup.upme.fragments.EducationTestFragment;
import me.justup.upme.utils.CommonUtils;

public class EducationChoseTestDialog extends Dialog {
    private EducationGetTestsResponse response;
    private Context context;
    private LinearLayout linearLayout;
    EducationTestFragment testFragment;


    public EducationChoseTestDialog(EducationTestFragment testFragment, EducationGetTestsResponse response, Context context) {
        super(context);
        this.response = response;
        this.context = context;
        this.testFragment = testFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_education_choose_test);
        setCanceledOnTouchOutside(false);
        linearLayout = (LinearLayout) findViewById(R.id.dialog_education_test_linear_layout);
        generateButtons();

    }

    @SuppressWarnings("ResourceType")
    private void generateButtons() {
        for (int i = 0; i <= response.result.size() - 1; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            Button btn = new Button(context);
            btn.setId(i);
            params.setMargins(0, CommonUtils.convertDpToPixels(context, 25), 0, 0);
            btn.setLayoutParams(params);
            btn.setText(response.result.get(i).name);
            btn.setBackground(context.getResources().getDrawable(R.drawable.button_pink_bg));
            final int index = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    testFragment.generateViewFromChosenTest(response, index);
                    dismiss();
                }
            });
            linearLayout.addView(btn, params);
        }
    }

}