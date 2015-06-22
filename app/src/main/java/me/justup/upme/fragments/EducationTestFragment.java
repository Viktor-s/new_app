package me.justup.upme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;

import me.justup.upme.R;
import me.justup.upme.dialogs.EducationChoseTestDialog;
import me.justup.upme.dialogs.WarningDialog;
import me.justup.upme.entity.EducationGetTestsQuery;
import me.justup.upme.entity.EducationGetTestsResponse;
import me.justup.upme.entity.EducationTestAnswerEntity;
import me.justup.upme.entity.EducationTestEntity;
import me.justup.upme.entity.EducationTestQuestionEntity;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.utils.CommonUtils;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;

public class EducationTestFragment extends Fragment {
    private static final String TAG = makeLogTag(EducationTestFragment.class);

    private static final String ARG_MODULE_ID = "module_id";

    private int mCurrentModuleId;
    private TextView mQuestionNameTextView = null, mQuestionContentTextView = null;
    private Button mPreviousQuestionButton = null, mNextQuestionButton = null, mSendButton = null;
    private GridLayout mAnswersContainerLayout = null;
    private RadioGroup mHourRadioGroup = null;
    private int mColumn = 3;
    private int mScreenWidth;
    private LayoutInflater mLayoutInflater = null;

    private EducationTestEntity mEducationTestEntity = null;

    private int mCurrentQuestionListPosition;

    private ArrayList<String> mAnsweredQuestions = new ArrayList<>();

    private View mContentView = null;

    public static EducationTestFragment newInstance(int moduleId) {
        EducationTestFragment fragment = new EducationTestFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MODULE_ID, moduleId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x - CommonUtils.convertDpToPixels(getActivity(), 440);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = super.onCreateView(inflater, container, savedInstanceState);

        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.fragment_education_test, container, false);
        }

        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            mCurrentModuleId = bundle.getInt(ARG_MODULE_ID);
        }

        // Init UI
        if (getActivity() != null) {
            initUI();
        }
    }

    private void initUI(){
        mLayoutInflater = LayoutInflater.from(getActivity());
        mQuestionNameTextView = (TextView) mContentView.findViewById(R.id.education_test_question_name_textView);
        mQuestionContentTextView = (TextView) mContentView.findViewById(R.id.education_test_question_content_textView);
        mPreviousQuestionButton = (Button) mContentView.findViewById(R.id.education_test_previous_button);
        mNextQuestionButton = (Button) mContentView.findViewById(R.id.education_test_next_button);
        mSendButton = (Button) mContentView.findViewById(R.id.education_test_send_button);

        mQuestionNameTextView.setVisibility(View.INVISIBLE);
        mQuestionContentTextView.setVisibility(View.INVISIBLE);
        mPreviousQuestionButton.setVisibility(View.INVISIBLE);
        mNextQuestionButton.setVisibility(View.INVISIBLE);

        // mAnswersContainerLayout = (GridLayout) view.findViewById(R.id.education_test_answers_container);
        mHourRadioGroup = (RadioGroup) mContentView.findViewById(R.id.hour_radio_group);

        mPreviousQuestionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setAnswerArr();
                mCurrentQuestionListPosition = mCurrentQuestionListPosition - 1;
                updateQuestion(mCurrentQuestionListPosition);

            }
        });

        mNextQuestionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setAnswerArr();
                mCurrentQuestionListPosition = mCurrentQuestionListPosition + 1;
                updateQuestion(mCurrentQuestionListPosition);

            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setAnswerArr();
                for (String str : mAnsweredQuestions)
                    if (str.equals("")) {
                        WarningDialog dialog = WarningDialog.newInstance(getString(R.string.test_warning), "Вы не ответили на все вопросы!");
                        dialog.show(getChildFragmentManager(), WarningDialog.WARNING_DIALOG);
                        return;
                    }
                ((EducationModuleFragment) getParentFragment()).closeTest();
            }
        });

        EducationGetTestsQuery testsQuery = new EducationGetTestsQuery();
        testsQuery.params.module_id = mCurrentModuleId;
        ApiWrapper.query(testsQuery, new OnTestResponse());
    }

    private void setAnswerArr() {
        int radioButtonID = mHourRadioGroup.getCheckedRadioButtonId();
        View radioButton = mHourRadioGroup.findViewById(radioButtonID);
        int index = mHourRadioGroup.indexOfChild(radioButton);
        EducationTestQuestionEntity questionEntity = mEducationTestEntity.getQuestions().get(mCurrentQuestionListPosition);
        String currentHash = (index == -1) ? "" : questionEntity.getAnswers().get(index).getAnswer_hash();
        mAnsweredQuestions.set(mCurrentQuestionListPosition, currentHash);
    }

    private void updateQuestion(int positionInList) {
        LOGE(TAG, "PositionInList :  " + positionInList);

        if (positionInList == 0) {
            mPreviousQuestionButton.setVisibility(View.INVISIBLE);
            mNextQuestionButton.setVisibility(View.VISIBLE);
        } else if (positionInList == mEducationTestEntity.getQuestions().size() - 1) {
            mNextQuestionButton.setVisibility(View.INVISIBLE);
            mPreviousQuestionButton.setVisibility(View.VISIBLE);
            mSendButton.setVisibility(View.VISIBLE);
        } else {
            mNextQuestionButton.setVisibility(View.VISIBLE);
            mPreviousQuestionButton.setVisibility(View.VISIBLE);
            mSendButton.setVisibility(View.GONE);
        }

        if (positionInList <= mEducationTestEntity.getQuestions().size() - 1) {
            EducationTestQuestionEntity questionEntity = mEducationTestEntity.getQuestions().get(positionInList);
            mQuestionContentTextView.setText(questionEntity.getQuestion_text());

            generateAnswersView(positionInList, questionEntity.getAnswers());
        }
    }

    private void generateAnswersView(final int questionNumber, ArrayList<EducationTestAnswerEntity> testAnswerEntities) {
        mHourRadioGroup.removeAllViews();
        String answerNumber = mAnsweredQuestions.get(questionNumber);
        int index = -1;
        int i = -1;
        for (EducationTestAnswerEntity itemAnswer : testAnswerEntities) {
            i++;

            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            RadioButton radioButtonView = new RadioButton(getActivity());
            radioButtonView.setText(itemAnswer.getAnswer_text());
            radioButtonView.setLayoutParams(p);
            radioButtonView.setTextColor(Color.BLACK);
            radioButtonView.setTextSize(25);
            if (itemAnswer.getAnswer_hash().equals(answerNumber))
                index = i;

            mHourRadioGroup.addView(radioButtonView);
        }

        if (index != -1) {
            ((RadioButton) mHourRadioGroup.getChildAt(index)).setChecked(true);
        }
    }

    private void generateAnswersViewOld(final String rightHash, ArrayList<EducationTestAnswerEntity> testAnswerEntities) {
        mAnswersContainerLayout.removeAllViews();

        for (int i = 0, c = 0, r = 0; i < testAnswerEntities.size(); i++, c++) {
            if (c == mColumn) {
                c = 0;
                r++;
            }

            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = (mScreenWidth / 3);
            param.leftMargin = CommonUtils.convertDpToPixels(getActivity(), 25);
            param.topMargin = CommonUtils.convertDpToPixels(getActivity(), 10);
            // param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(c);
            param.rowSpec = GridLayout.spec(r);
            LinearLayout answerItemLayout = (LinearLayout) mLayoutInflater.inflate(R.layout.education_test_anser_item, null, false);
            TextView answerHash = (TextView) answerItemLayout.findViewById(R.id.education_test_item_hash);
            answerHash.setText(testAnswerEntities.get(i).getAnswer_hash());
            Button answerButton = (Button) answerItemLayout.findViewById(R.id.education_test_item_button);
            answerButton.setText(testAnswerEntities.get(i).getAnswer_text());
            answerItemLayout.setLayoutParams(param);
            answerItemLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String answerHash = ((TextView) v.findViewById(R.id.education_test_item_hash)).getText().toString();
                    if (rightHash.equals(answerHash)) {
                        Toast.makeText(getActivity(), "Right answer", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Wrong answer", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mAnswersContainerLayout.addView(answerItemLayout);
        }
    }

    private class OnTestResponse extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGI(TAG, "onSuccess : " + content);

            EducationGetTestsResponse response = null;
            try {
                response = ApiWrapper.gson.fromJson(content, EducationGetTestsResponse.class);
            } catch (JsonSyntaxException e) {
                LOGE(TAG, "OnPushEducationGetTestsResponse gson.fromJson:\n" + content);
            }

            if (response != null) {
                // TODO тут будет выбор теста сначала
                EducationChoseTestDialog educationChoseTestDialog = new EducationChoseTestDialog(EducationTestFragment.this, response, getActivity());
                educationChoseTestDialog.show();

            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGI(TAG, "OnFailure : " + content);
        }
    }

    public void generateViewFromChosenTest(EducationGetTestsResponse response, int testPosition) {
        mQuestionNameTextView.setVisibility(View.VISIBLE);
        mQuestionContentTextView.setVisibility(View.VISIBLE);
        mPreviousQuestionButton.setVisibility(View.VISIBLE);
        mNextQuestionButton.setVisibility(View.VISIBLE);
        mEducationTestEntity = new EducationTestEntity();
        mEducationTestEntity.setId(response.result.get(testPosition).id);
        mEducationTestEntity.setModule_id(response.result.get(testPosition).module_id);
        mEducationTestEntity.setName(response.result.get(testPosition).name);
        mEducationTestEntity.setDescription(response.result.get(testPosition).description);
        mEducationTestEntity.setPass_limit(response.result.get(testPosition).pass_limit);
        ArrayList<EducationTestQuestionEntity> testQuestionEntities = new ArrayList<>();
        for (int i = 0; i < response.result.get(testPosition).questions.size(); i++) {
            EducationTestQuestionEntity questionEntity = new EducationTestQuestionEntity();
            questionEntity.setQuestion_text(response.result.get(testPosition).questions.get(i).question_text);
            questionEntity.setQuestion_hash(response.result.get(testPosition).questions.get(i).question_hash);
            ArrayList<EducationTestAnswerEntity> answerEntities = new ArrayList<>();
            for (int j = 0; j < response.result.get(testPosition).questions.get(i).answers.size(); j++) {
                EducationTestAnswerEntity answerEntity = new EducationTestAnswerEntity();
                answerEntity.setAnswer_text(response.result.get(testPosition).questions.get(i).answers.get(j).answer_text);
                answerEntity.setAnswer_hash(response.result.get(testPosition).questions.get(i).answers.get(j).answer_hash);
                answerEntities.add(answerEntity);
            }
            questionEntity.setAnswers(answerEntities);
            testQuestionEntities.add(questionEntity);
            mAnsweredQuestions.add("");
        }

        mEducationTestEntity.setQuestions(testQuestionEntities);
        LOGE(TAG, mEducationTestEntity.toString());
        mCurrentQuestionListPosition = 0;
        mQuestionNameTextView.setText(mEducationTestEntity.getName());
        updateQuestion(mCurrentQuestionListPosition);
    }
}
