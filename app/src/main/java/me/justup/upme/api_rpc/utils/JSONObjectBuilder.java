package me.justup.upme.api_rpc.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class JSONObjectBuilder implements Serializable{
    private JSONObject mJSONObject;

    public JSONObjectBuilder(JSONObject jsonObject) {
        this.mJSONObject = jsonObject;
    }

    public JSONObjectBuilder phone(String phone) throws JSONException {
        this.mJSONObject.put(Constants.PHONE, phone);
        return this;
    }

    public JSONObjectBuilder code(String code) throws JSONException {
        this.mJSONObject.put(Constants.CODE, code);
        return this;
    }

    public JSONObjectBuilder programId(String programId) throws JSONException {
        this.mJSONObject.put(Constants.PROGRAM_ID, programId);
        return this;
    }

    public JSONObjectBuilder moduleId(String moduleId) throws JSONException {
        this.mJSONObject.put(Constants.MODULE_ID, moduleId);
        return this;
    }

    public JSONObjectBuilder testId(String testId) throws JSONException {
        this.mJSONObject.put(Constants.TEST_ID, testId);
        return this;
    }

    public JSONObjectBuilder data(String data) throws JSONException {
        this.mJSONObject.put(Constants.DATA, data);
        return this;
    }

    public JSONObjectBuilder questionHash(String questionHash) throws JSONException {
        this.mJSONObject.put(Constants.QUESTION_HASH, questionHash);
        return this;
    }

    public JSONObjectBuilder answer(String answer) throws JSONException {
        this.mJSONObject.put(Constants.ANSWERS, answer);
        return this;
    }

    public JSONObjectBuilder answersHash(String answersHash) throws JSONException {
        this.mJSONObject.put(Constants.ANSWERS_HASH, answersHash);
        return this;
    }

    public JSONObjectBuilder isCorrect(String isCorrect) throws JSONException {
        this.mJSONObject.put(Constants.IS_CORRECT, isCorrect);
        return this;
    }

    public JSONObjectBuilder limit(String limit) throws JSONException {
        this.mJSONObject.put(Constants.LIMIT, limit);
        return this;
    }

    public JSONObjectBuilder offset(String offset) throws JSONException {
        this.mJSONObject.put(Constants.OFFSET, offset);
        return this;
    }

    public JSONObjectBuilder order(String order) throws JSONException {
        this.mJSONObject.put(Constants.ORDER, order);
        return this;
    }

    public JSONObjectBuilder articleId(String articleId) throws JSONException {
        this.mJSONObject.put(Constants.ARTICLE_ID, articleId);
        return this;
    }

    public JSONObjectBuilder content(String content) throws JSONException {
        this.mJSONObject.put(Constants.CONTENT, content);
        return this;
    }

    public JSONObjectBuilder start(String start) throws JSONException {
        this.mJSONObject.put(Constants.START, start);
        return this;
    }

    public JSONObjectBuilder end(String end) throws JSONException {
        this.mJSONObject.put(Constants.END, end);
        return this;
    }

    public JSONObjectBuilder name(String name) throws JSONException {
        this.mJSONObject.put(Constants.NAME, name);
        return this;
    }

    public JSONObjectBuilder description(String description) throws JSONException {
        this.mJSONObject.put(Constants.DESCRIPTION, description);
        return this;
    }

    public JSONObjectBuilder type(String type) throws JSONException {
        this.mJSONObject.put(Constants.TYPE, type);
        return this;
    }

    public JSONObjectBuilder location(String location) throws JSONException {
        this.mJSONObject.put(Constants.LOCATION, location);
        return this;
    }

    public JSONObjectBuilder shareWith(String shareWith) throws JSONException {
        this.mJSONObject.put(Constants.SHARE_WITH, shareWith);
        return this;
    }

    public JSONObjectBuilder eventId(String eventId) throws JSONException {
        this.mJSONObject.put(Constants.EVENT_ID, eventId);
        return this;
    }

    public JSONObjectBuilder membersIds(String membersIds) throws JSONException {
        this.mJSONObject.put(Constants.MEMBER_IDS, membersIds);
        return this;
    }

    public JSONObjectBuilder userId(String userId) throws JSONException {
        this.mJSONObject.put(Constants.USER_ID, userId);
        return this;
    }

    public JSONObjectBuilder num(String num) throws JSONException {
        this.mJSONObject.put(Constants.NUM, num);
        return this;
    }

    public JSONObjectBuilder img(String img) throws JSONException {
        this.mJSONObject.put(Constants.IMG, img);
        return this;
    }

    public JSONObjectBuilder longitude(String longitude) throws JSONException {
        this.mJSONObject.put(Constants.LONGITUDE, longitude);
        return this;
    }

    public JSONObjectBuilder latitude(String latitude) throws JSONException {
        this.mJSONObject.put(Constants.LATITUDE, latitude);
        return this;
    }

    public JSONObjectBuilder level(String level) throws JSONException {
        this.mJSONObject.put(Constants.LEVEL, level);
        return this;
    }

    public JSONObjectBuilder ts(String ts) throws JSONException {
        this.mJSONObject.put(Constants.TS, ts);
        return this;
    }

    public JSONObjectBuilder fileHash(String fileHash) throws JSONException {
        this.mJSONObject.put(Constants.FILE_HASH, fileHash);
        return this;
    }

    public JSONObjectBuilder deleteSource(String deleteSource) throws JSONException {
        this.mJSONObject.put(Constants.DELETE_SOURCE, deleteSource);
        return this;
    }

    public JSONObjectBuilder network(String network) throws JSONException {
        this.mJSONObject.put(Constants.NETWORK, network);
        return this;
    }

    public JSONObjectBuilder metadata(String metadata) throws JSONException {
        this.mJSONObject.put(Constants.METADATA, metadata);
        return this;
    }

    public JSONObjectBuilder shareAll(String shareAll) throws JSONException {
        this.mJSONObject.put(Constants.SHARE_ALL, shareAll);
        return this;
    }

    public JSONObjectBuilder directLink(String directLink) throws JSONException {
        this.mJSONObject.put(Constants.DIRECT_LINK, directLink);
        return this;
    }

    public JSONObjectBuilder brandId(String brandId) throws JSONException {
        this.mJSONObject.put(Constants.BRAND_ID_UNDERLINE, brandId);
        return this;
    }

    public JSONObjectBuilder productId(String productId) throws JSONException {
        this.mJSONObject.put(Constants.PRODUCT_ID, productId);
        return this;
    }

    public JSONObjectBuilder lastName(String lastName) throws JSONException {
        this.mJSONObject.put(Constants.LAST_NAME, lastName);
        return this;
    }

    public JSONObjectBuilder firstName(String firstName) throws JSONException {
        this.mJSONObject.put(Constants.FIRST_NAME, firstName);
        return this;
    }

    public JSONObjectBuilder patronymic(String patronymic) throws JSONException {
        this.mJSONObject.put(Constants.PATRONYMIC, patronymic);
        return this;
    }

    public JSONObjectBuilder sex(String sex) throws JSONException {
        this.mJSONObject.put(Constants.SEX, sex);
        return this;
    }

    public JSONObjectBuilder age(String age) throws JSONException {
        this.mJSONObject.put(Constants.AGE, age);
        return this;
    }

    public JSONObjectBuilder email(String email) throws JSONException {
        this.mJSONObject.put(Constants.EMAIL, email);
        return this;
    }

    public JSONObjectBuilder key(String key) throws JSONException {
        this.mJSONObject.put(Constants.KEY, key);
        return this;
    }

    public JSONObjectBuilder cancel(String cancel) throws JSONException {
        this.mJSONObject.put(Constants.CANCEL, cancel);
        return this;
    }

    public JSONObjectBuilder cardNumber(String cardNumber) throws JSONException {
        this.mJSONObject.put(Constants.CARD_NUMBER, cardNumber);
        return this;
    }

    public JSONObjectBuilder destinationCardNumber(String destinationCardNumber) throws JSONException {
        this.mJSONObject.put(Constants.DESTINATION_CARDNUMBER, destinationCardNumber);
        return this;
    }

    public JSONObjectBuilder userIds(String userIds) throws JSONException {
        this.mJSONObject.put(Constants.USER_IDS, userIds);
        return this;
    }

    public JSONObjectBuilder roomId(String roomId) throws JSONException {
        this.mJSONObject.put(Constants.ROOM_ID, roomId);
        return this;
    }

    public JSONObjectBuilder idGooglePush(String idGooglePush) throws JSONException {
        this.mJSONObject.put(Constants.GOOGLE_PUSH_ID, idGooglePush);
        return this;
    }

    public JSONObjectBuilder planId(String planId) throws JSONException {
        this.mJSONObject.put(Constants.PLAN_ID, planId);
        return this;
    }

    public JSONObjectBuilder id(String id) throws JSONException {
        this.mJSONObject.put(Constants.ID, id);
        return this;
    }

    public String build() {
        return this.mJSONObject.toString();
    }
}
