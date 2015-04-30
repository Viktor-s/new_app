package me.justup.upme.fragments;

import android.app.Fragment;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;

import me.justup.upme.R;
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
    private int currentModuleId;
    private TextView questionNameTextview, questionContentTextView;
    private Button previousQuestionButton, nextQuestionButton;
    private GridLayout answersContainerLayout;
    private int column = 3;
    private int screenWidth;
    private LayoutInflater layoutInflater;


    private EducationTestEntity educationTestEntity;

    private int currentQuestionListPosition;

    public static EducationTestFragment newInstance(int moduleId) {
        EducationTestFragment fragment = new EducationTestFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MODULE_ID, moduleId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            currentModuleId = bundle.getInt(ARG_MODULE_ID);
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x - CommonUtils.convertDpToPixels(getActivity(), 440);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_education_test, container, false);
        layoutInflater = LayoutInflater.from(getActivity());
        questionNameTextview = (TextView) view.findViewById(R.id.education_test_question_name_textView);
        questionContentTextView = (TextView) view.findViewById(R.id.education_test_question_content_textView);
        previousQuestionButton = (Button) view.findViewById(R.id.education_test_previous_button);
        nextQuestionButton = (Button) view.findViewById(R.id.education_test_next_button);
        questionNameTextview.setVisibility(View.INVISIBLE);
        questionContentTextView.setVisibility(View.INVISIBLE);
        previousQuestionButton.setVisibility(View.INVISIBLE);
        nextQuestionButton.setVisibility(View.INVISIBLE);

        answersContainerLayout = (GridLayout) view.findViewById(R.id.education_test_answers_container);
        previousQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuestionListPosition = currentQuestionListPosition - 1;
                updateQuestion(currentQuestionListPosition);
            }
        });

        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentQuestionListPosition = currentQuestionListPosition + 1;
                updateQuestion(currentQuestionListPosition);

            }
        });
        EducationGetTestsQuery testsQuery = new EducationGetTestsQuery();
        testsQuery.params.module_id = currentModuleId;
        ApiWrapper.syncQuery(testsQuery, new OnTestResponse());

        return view;
    }

    private void updateQuestion(int positionInList) {
        LOGE("pavel", " " + positionInList);
        if (positionInList == 0) {
            previousQuestionButton.setVisibility(View.INVISIBLE);
            nextQuestionButton.setVisibility(View.VISIBLE);
        } else if (positionInList == educationTestEntity.getQuestions().size()-1) {
            nextQuestionButton.setVisibility(View.INVISIBLE);
            previousQuestionButton.setVisibility(View.VISIBLE);
        } else {
            nextQuestionButton.setVisibility(View.VISIBLE);
            previousQuestionButton.setVisibility(View.VISIBLE);
        }
        if (positionInList <= educationTestEntity.getQuestions().size() -1) {
            EducationTestQuestionEntity questionEntity = educationTestEntity.getQuestions().get(positionInList);
            questionContentTextView.setText(questionEntity.getQuestion_text());
            //answersContainerLayout.removeAllViews();
            generateAnswersView(questionEntity.getQuestion_hash(), questionEntity.getAnswers());
        }

    }


    private void generateAnswersView(final String rightHash, ArrayList<EducationTestAnswerEntity> testAnswerEntities) {
        answersContainerLayout.removeAllViews();
        for (int i = 0, c = 0, r = 0; i < testAnswerEntities.size(); i++, c++) {
            if (c == column) {
                c = 0;
                r++;
            }
            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = (screenWidth / 3);
            param.leftMargin = CommonUtils.convertDpToPixels(getActivity(), 25);
            param.topMargin = CommonUtils.convertDpToPixels(getActivity(), 10);
            ;
            //  param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(c);
            param.rowSpec = GridLayout.spec(r);

            LinearLayout answerItemLayout = (LinearLayout) layoutInflater.inflate(R.layout.education_test_anser_item, null, false);
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
                    // int idCurrentGroup = Integer.parseInt(((TextView) v.findViewById(R.id.grid_hide_id)).getText().toString());
                    // String nameCurrentGroup = ((TextView) v.findViewById(R.id.grid_row_name_extView)).getText().toString();
                    // showProductHtmlFragment(idCurrentGroup, nameCurrentGroup, namePath);
                }
            });
            answersContainerLayout.addView(answerItemLayout);
        }
    }

    private class OnTestResponse extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGI("pavel", "Sucsess : " + content);
            EducationGetTestsResponse response = null;
            try {
                response = ApiWrapper.gson.fromJson(content, EducationGetTestsResponse.class);
            } catch (JsonSyntaxException e) {
                LOGE("pavel", "OnPushEducationGetTestsResponse gson.fromJson:\n" + content);
            }

            if (response != null) {
                questionNameTextview.setVisibility(View.VISIBLE);
                questionContentTextView.setVisibility(View.VISIBLE);
                previousQuestionButton.setVisibility(View.VISIBLE);
                nextQuestionButton.setVisibility(View.VISIBLE);
                educationTestEntity = new EducationTestEntity();
                educationTestEntity.setId(response.result.get(0).id);
                educationTestEntity.setModule_id(response.result.get(0).module_id);
                educationTestEntity.setName(response.result.get(0).name);
                educationTestEntity.setDescription(response.result.get(0).description);
                educationTestEntity.setPass_limit(response.result.get(0).pass_limit);
                ArrayList<EducationTestQuestionEntity> testQuestionEntities = new ArrayList<>();
                for (int i = 0; i < response.result.get(0).questions.size(); i++) {
                    EducationTestQuestionEntity questionEntity = new EducationTestQuestionEntity();
                    questionEntity.setQuestion_text(response.result.get(0).questions.get(i).question_text);
                    questionEntity.setQuestion_hash(response.result.get(0).questions.get(i).question_hash);
                    ArrayList<EducationTestAnswerEntity> answerEntities = new ArrayList<>();
                    for (int j = 0; j < response.result.get(0).questions.get(i).answers.size(); j++) {
                        EducationTestAnswerEntity answerEntity = new EducationTestAnswerEntity();
                        answerEntity.setAnswer_text(response.result.get(0).questions.get(i).answers.get(j).answer_text);
                        answerEntity.setAnswer_hash(response.result.get(0).questions.get(i).answers.get(j).answer_hash);
                        answerEntities.add(answerEntity);
                    }
                    questionEntity.setAnswers(answerEntities);
                    testQuestionEntities.add(questionEntity);
                }
                educationTestEntity.setQuestions(testQuestionEntities);
                LOGE("pavel", educationTestEntity.toString());
                currentQuestionListPosition = 0;
                questionNameTextview.setText(educationTestEntity.getName());
                updateQuestion(currentQuestionListPosition);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGI("pavel", "OnFailure : " + content);
        }
    }
}