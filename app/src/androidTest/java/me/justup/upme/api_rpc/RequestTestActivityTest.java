package me.justup.upme.api_rpc;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import me.justup.upme.R;
import me.justup.upme.RequestTestActivity;
import me.justup.upme.api_rpc.request_model.handler.AppIntentHandler;
import me.justup.upme.api_rpc.request_model.service.RequestServiceHelper;
import me.justup.upme.api_rpc.response_object.AuthorizationObject;
import me.justup.upme.api_rpc.response_object.EducationObject;
import me.justup.upme.api_rpc.response_object.RPCError;
import me.justup.upme.api_rpc.utils.Constants;
import me.justup.upme.api_rpc.utils.JSONObjectBuilder;
import me.justup.upme.api_rpc.utils.RequestParamBuilder;

import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGI;

@LargeTest
public class RequestTestActivityTest extends ActivityInstrumentationTestCase2<RequestTestActivity> {
    private static final String TAG = RequestTestActivityTest.class.getSimpleName();

    private RequestTestActivity activity;
    private RequestServiceHelper mRequestServiceHelper;
    private TextView titleInput;

    private static final String PHONE = "+380637759115";

    // Response Object
    private String TOKEN;
    private AuthorizationObject mLoggedUserInfoObject;
    private ArrayList<EducationObject> mEducationProgramList = null;
    private EducationObject mEducationModuleById, mEducationModuleByProgramId, mTestByModuleId = null;
    private String mAddEventResponse = null;

    public RequestTestActivityTest() {
        super(RequestTestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();

        activity = getActivity();
        assertNotNull(activity);

        mRequestServiceHelper = activity.getRequestServiceHelper();
        assertNotNull(mRequestServiceHelper);
    }

    public void testAPI() {
        // Check Internet connection
        assertEquals(checkConnectingToInternet(), true);
        // Set Service Listener
        activity.setOnRequestServiceListener(new RequestTestActivity.OnRequestServiceListener() {

            @Override
            public void onServiceCallback(int requestId, Intent requestIntent, int resultCode, Bundle data) {
                serviceCallbackHandler(requestId, requestIntent, resultCode, data);
            }
        });

        // Init Info View
        titleInput = (TextView) activity.findViewById(R.id.txt_notify);
        assertNotNull(titleInput);

        // 1. getVerificationPhoneCode
        setText("getVerificationPhoneCode");

        try {
            sendRequest(new RequestParamBuilder.ParamBuilder(Constants.AUTH_GET_VERIFICATION, new JSONObjectBuilder(new JSONObject()).phone(PHONE).build(), Constants.ACTION_AUTH_GET_VERIFICATION, AppIntentHandler.RequestType.OBJECT).build());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 2. Wait for SMS
        setText("Wait for SMS");
        waitRequest();

        // Get SMS
        SMSObject smsObject = getSMSObjectMap().get("JUSTUP.ME");
        final String body = smsObject.getBody();
        // Show body
        setText("TEXT : " + body);

        LOGI(TAG, "TEXT : " + body);

        // 3. checkVerificationPhoneCode
        setText("checkVerificationPhoneCode");
        try {
            sendRequest(new RequestParamBuilder.ParamBuilder(Constants.AUTH_CHECK_VERIFICATION, new JSONObjectBuilder(new JSONObject()).phone(PHONE).code(body).build(), Constants.ACTION_AUTH_CHECK_VERIFICATION, AppIntentHandler.RequestType.OBJECT).build());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        waitRequest();

        // 4. getLoggedUserInfo
        setText("getLoggedUserInfo");
        sendRequest(new RequestParamBuilder.ParamBuilder(Constants.AUTH_GET_LOGGED_USER_INFO, new JSONObjectBuilder(new JSONObject()).build(), Constants.ACTION_AUTH_GET_LOGGED_USER_INFO, AppIntentHandler.RequestType.OBJECT).token(TOKEN).build());
        waitRequest();

        // TODO Education.
        // 5. Education.getPrograms
        setText("Education.getPrograms");
        sendRequest(new RequestParamBuilder.ParamBuilder(Constants.EDUCATION_GET_PROGRAMS, new JSONObjectBuilder(new JSONObject()).build(), Constants.ACTION_EDUCATION_GET_PROGRAMS, AppIntentHandler.RequestType.LIST).token(TOKEN).build());
        waitRequest();

        // 6. Education.getModulesByProgramId
        setText("Education.getModulesByProgramId");
        try {
            sendRequest(new RequestParamBuilder.ParamBuilder(Constants.EDUCATION_GET_MODULES_BY_PROGRAM_ID, new JSONObjectBuilder(new JSONObject()).programId(String.valueOf(mEducationProgramList.get(0).getId())).build(), Constants.ACTION_EDUCATION_GET_MODULES_BY_PROGRAM_ID, AppIntentHandler.RequestType.OBJECT).token(TOKEN).build());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        waitRequest();

        // 7. Education.getMaterialsByModuleId
        setText("Education.getMaterialsByModuleId");
        try {
            sendRequest(new RequestParamBuilder.ParamBuilder(Constants.EDUCATION_GET_MATERIALS_BY_MODULE_ID, new JSONObjectBuilder(new JSONObject()).moduleId(String.valueOf(mEducationModuleById.getId())).build(), Constants.ACTION_EDUCATION_GET_MATERIALS_BY_MODULE_ID, AppIntentHandler.RequestType.OBJECT).token(TOKEN).build());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        waitRequest();

        // 8. Education.getTestsByModuleId
        setText("Education.getTestsByModuleId");
        try {
            sendRequest(new RequestParamBuilder.ParamBuilder(Constants.EDUCATION_GET_TESTS, new JSONObjectBuilder(new JSONObject()).moduleId(String.valueOf(mEducationModuleById.getId())).build(), Constants.ACTION_EDUCATION_GET_TESTS, AppIntentHandler.RequestType.OBJECT).token(TOKEN).build());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        waitRequest();

        // 9. Education.passTest
        setText("Education.passTest");
        try {
            sendRequest(new RequestParamBuilder.ParamBuilder(Constants.EDUCATION_PASS_TESTS, new JSONObjectBuilder(new JSONObject()).testId(String.valueOf(mTestByModuleId.getId())).data(new JSONObjectBuilder(new JSONObject()).questionHash("MD51").answer(new JSONObjectBuilder(new JSONObject()).answersHash("MD511").isCorrect("false").build()).build()).build(), Constants.ACTION_EDUCATION_PASS_TESTS, AppIntentHandler.RequestType.OBJECT).token(TOKEN).build());
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }

        waitRequest();

        // 7. Calendar.addEvent
        setText("Calendar.addEvent");
        try {
            sendRequest(new RequestParamBuilder.ParamBuilder(Constants.CALENDAR_ADD_EVENT, new JSONObjectBuilder(new JSONObject()).name("Vasya").description("Just test Evevnt").type("reminder").location("Moskau").start("1421937910").end("1421937910").shareWith("1,2,4").build(), Constants.ACTION_CALENDAR_ADD_EVENT, AppIntentHandler.RequestType.STRING).token(TOKEN).build());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        waitRequest();
    }

    @Override
    protected void tearDown() throws Exception {
        activity.finish();
        super.tearDown();
    }

    private void setText(final String text){
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                titleInput.setText(text);
            }
        });
    }

    private void sendRequest(final RequestParamBuilder paramBuilder){
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                LOGI(TAG, "Request id : " + mRequestServiceHelper.sendRequest(paramBuilder));
            }
        });
    }

    private void waitRequest(){
        try {
            Thread.sleep(15 * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * true - on / false - off
     */
    public boolean checkConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public HashMap<String, SMSObject> getSMSObjectMap(){
        HashMap<String, SMSObject> smsObjectHashMap = new HashMap<>();

        Cursor cursor = null;

        try {
            cursor = activity.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

            for (boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {
                final String sender_no = cursor.getString(cursor.getColumnIndexOrThrow("address"));

                if(sender_no.equals("JUSTUP.ME")) {
                    smsObjectHashMap.put(sender_no, new SMSObject(cursor.getString(cursor.getColumnIndexOrThrow("body")), sender_no, cursor.getString(cursor.getColumnIndexOrThrow("date")), cursor.getString(cursor.getColumnIndexOrThrow("type"))));

                    break;
                }
            }

        } catch (Exception e) {
            LOGE(TAG, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return smsObjectHashMap;
    }

    class SMSObject {
        String body;
        String sender_no;
        String date;
        String type;

        public SMSObject(String body, String sender_no, String date, String type) {
            this.body = body;
            this.sender_no = sender_no;
            this.date = date;
            this.type = type;
        }

        public String getBody() {
            return body;
        }

        public String getSender_no() {
            return sender_no;
        }

        public String getDate() {
            return date;
        }

        public String getType() {
            return type;
        }
    }

    private void serviceCallbackHandler(int requestId, Intent requestIntent, int resultCode, Bundle data){
        // Get Action
        String action = requestIntent.getAction();

        LOGI(TAG, "Request id : " + requestId + ", Intent : " + requestIntent + ", Result Code : " + resultCode + ", Bundle : " + data);

        switch (action){
            case Constants.ACTION_AUTH_GET_VERIFICATION :
                assertEquals(requestId, 0);

                break;
            case Constants.ACTION_AUTH_CHECK_VERIFICATION :
                if(resultCode==AppIntentHandler.SUCCESS_RESPONSE){
                    AuthorizationObject accountObject = (AuthorizationObject)  data.getSerializable(AppIntentHandler.EXTRA_PARAM_MAIL_RESPONSE);
                    assertNotNull(accountObject);
                    assertEquals(resultCode, 0);

                    TOKEN = accountObject.getToken();
                    LOGI(TAG, "TOKEN : " + TOKEN);
                }

                break;
            case Constants.AUTH_GET_LOGGED_USER_INFO :
                if(resultCode==AppIntentHandler.SUCCESS_RESPONSE){
                    AuthorizationObject accountObject = (AuthorizationObject)  data.getSerializable(AppIntentHandler.EXTRA_PARAM_MAIL_RESPONSE);
                    assertNotNull(accountObject);
                    assertEquals(requestId, 0);

                    mLoggedUserInfoObject = accountObject;
                    LOGI(TAG, "mLoggedUserInfoObject : " + mLoggedUserInfoObject.toString());
                }
                break;
            case Constants.ACTION_EDUCATION_GET_PROGRAMS :
                if(resultCode==AppIntentHandler.SUCCESS_RESPONSE){
                    mEducationProgramList = (ArrayList<EducationObject>)  data.getSerializable(AppIntentHandler.EXTRA_PARAM_MAIL_RESPONSE);
                    assertNotNull(mEducationProgramList);

                    LOGI(TAG, "mEducationProgramList : " + mEducationProgramList.toString());
                }
                break;
            case Constants.ACTION_EDUCATION_GET_MODULES_BY_PROGRAM_ID :
                if(resultCode==AppIntentHandler.SUCCESS_RESPONSE){
                    mEducationModuleById = (EducationObject)  data.getSerializable(AppIntentHandler.EXTRA_PARAM_MAIL_RESPONSE);
                    assertNotNull(mEducationModuleById);

                    LOGI(TAG, "mEducationModuleById : " + mEducationModuleById.toString());
                }
                break;
            case Constants.ACTION_EDUCATION_GET_MATERIALS_BY_MODULE_ID :
                if(resultCode==AppIntentHandler.SUCCESS_RESPONSE){
                    mEducationModuleByProgramId = (EducationObject)  data.getSerializable(AppIntentHandler.EXTRA_PARAM_MAIL_RESPONSE);
                    assertNotNull(mEducationModuleByProgramId);

                    LOGI(TAG, "mEducationModuleByProgramId : " + mEducationModuleByProgramId.toString());
                }
                break;
            case Constants.ACTION_EDUCATION_GET_TESTS :
                if(resultCode==AppIntentHandler.SUCCESS_RESPONSE){
                    mTestByModuleId = (EducationObject)  data.getSerializable(AppIntentHandler.EXTRA_PARAM_MAIL_RESPONSE);
                    assertNotNull(mTestByModuleId);

                    LOGI(TAG, "mTestByModuleId : " + mTestByModuleId.toString());
                }
                break;

            case Constants.ACTION_CALENDAR_ADD_EVENT :
                if(resultCode==AppIntentHandler.SUCCESS_RESPONSE){
                    mAddEventResponse = (String) data.getSerializable(AppIntentHandler.EXTRA_PARAM_MAIL_RESPONSE);
                    assertNotNull(mAddEventResponse);

                    LOGI(TAG, "mAddEventResponse : " + mAddEventResponse);
                }else{
                    RPCError rpcError = (RPCError) data.getSerializable(AppIntentHandler.EXTRA_PARAM_MAIL_RESPONSE);
                    assertNotNull(rpcError);

                    LOGI(TAG, "RPCError : " + rpcError.toString());
                }
                break;
            default :
                break;
        }
    }
}
