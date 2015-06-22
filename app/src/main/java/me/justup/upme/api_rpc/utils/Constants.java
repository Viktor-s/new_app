package me.justup.upme.api_rpc.utils;

import me.justup.upme.BuildConfig;

public class Constants {

    // API methods constants
    // Authorization
    public static final String AUTH_GET_VERIFICATION = "Auth.getVerificationPhoneCode";
    public static final String AUTH_CHECK_VERIFICATION = "Auth.checkVerificationPhoneCode";
    public static final String AUTH_GET_LOGGED_USER_INFO = "Auth.getLoggedUserInfo";
    // Education
    public static final String EDUCATION_GET_PROGRAMS = "Education.getPrograms";
    public static final String EDUCATION_GET_MODULES_BY_PROGRAM_ID = "Education.getModulesByProgramId";
    public static final String EDUCATION_GET_MATERIALS_BY_MODULE_ID = "Education.getMaterialsByModuleId";
    public static final String EDUCATION_GET_TESTS = "Education.getTestsByModuleId";
    public static final String EDUCATION_PASS_TESTS = "Education.passTest";
    // News Section
    public static final String ARTICLES_GET = "Articles.get";
    public static final String ARTICLES_GET_SHORT_DESCRIPTION = "Articles.getShortDescription";
    public static final String ARTICLES_GET_FULL_DESCRIPTION = "Articles.find";
    public static final String ARTICLES_ADD_COMMENT = "ArticleComments.add";
    public static final String ARTICLES_FULL_GET_COMMENTS = "ArticleComments.getByArticleId";
    // Calendar
    public static final String CALENDAR_GET_EVENTS = "Calendar.getEvents";
    public static final String CALENDAR_ADD_EVENT = "Calendar.addEvent";
    public static final String CALENDAR_UPDATE_EVENT = "Calendar.updateEvent";
    public static final String CALENDAR_REMOVE_EVENT = "Calendar.removeEvent";
    public static final String CALENDAR_EVENT_GET_ALL_SHARED_WITH_ME = "Calendar.Events.getAllSharedWithMe";
    public static final String CALENDAR_EVENT_GET_ALL_OWN = "Calendar.Events.getAllOwn";
    public static final String CALENDAR_EVENT_SHARE_WITH = "Calendar.Events.shareWith";
    public static final String CALENDAR_EVENT_AVAILABLE_TYPES = "Calendar.getAvailableTypes";
    // Account
    public static final String ACCOUNT_GET_ALL_CONTACTS = "Account.getAllAllowedContacts";
    public static final String ACCOUNT_GET_REFERRALS_BY_ID = "Account.getReferralsById";
    public static final String ACCOUNT_GET_PARENTS = "Account.getParents";
    public static final String ACCOUNT_ADD_REFERRAL = "Account.addReferral";
    public static final String ACCOUNT_GET_USER_PANEL_INFO = "Account.getUserPanelInfo";
    public static final String ACCOUNT_ADD_USER_PANEL_INFO = "Account.addUserPanelInfo";
    public static final String ACCOUNT_ADD_USER_LOCATION = "Account.addUserLocation";
    public static final String ACCOUNT_SET_AVATAR_FILE = "Account.setAvatarFile";
    // Social
    public static final String SOCIAL_SAVE_METADATA = "Social.saveMetadata";
    // File
    public static final String FILE_GET_SHARE_WITH = "File.getShareWith";
    public static final String FILE_ADD_SHARE_WITH = "File.addShareWith";
    public static final String FILE_DROP_SHARE_WITH = "File.dropShareWith";
    public static final String FILE_UNLINK_SHARED_FILE = "File.unlinkSharedFile";
    public static final String FILE_SHARE_ALL = "File.shareAll";
    public static final String FILE_COPY_SHARED_TO_ME = "File.copySharedToMe";
    public static final String FILE_GET_PROPERTIES_BY_HASH = "File.getByHash";
    public static final String FILE_GET_MY_FILES = "File.getAllOwn";
    public static final String FILE_GET_ALL_SHARED_WITH_ME = "File.getAllSharedWithMe";
    public static final String FILE_GET_ALL = "File.getAll";
    public static final String FILE_DELETE = "File.delete";
    public static final String FILE_ADD_TO_FAVORITE = "File.addToFavorite";
    public static final String FILE_DROP_FROM_FAVORITE = "File.dropFromFavorite";
    public static final String FILE_DIRECT_LINK = "File.directLink";

    private static final String CALL_CLOUD_UPLOAD = "/upload";
    private static final String CALL_CLOUD_FILE = "/file/";
    // Products
    public static final String PRODUCTS_GET_ALL_CATEGORIES = "Products.getProductCategories";
    public static final String PRODUCTS_GET_PRODUCT_CATEGORY_BY_ID = "Products.getProductCategoryById";
    public static final String PRODUCTS_GET_BRAND_CATEGORY_BY_ID = "Products.getBrandProductCategoryById";
    public static final String PRODUCTS_GET_BRAND_CATEGORIES = "Products.getBrandProductCategories";
    public static final String PRODUCTS_GET_HTML_BY_ID = "Products.getProductById";
    public static final String PRODUCTS_BRANDS_GET_ALL = "Brands.getAll";
    public static final String PRODUCTS_BRANDS_GET_BY_ID = "Brands.getById";
    public static final String PRODUCTS_BRANDS_GET_BY_NAME = "Brands.getByName";
    // Orders
    public static final String PRODUCTS_ORDER_CREATE = "Order.create";
    public static final String PRODUCTS_ORDER_GET_FORM = "Order.getForm";
    public static final String PRODUCTS_ORDER_RESPONSE_FORM = "Order.responseForm";
    // WebRTC
    public static final String WEB_RTC_START_CALL = "WebRtc.startCall";
    public static final String WEB_RTC_STOP_CALL = "WebRtc.stopCall";
    // Jabber
    public static final String JABBER_START_CHAT = "Jabber.startChat";
    // Push
    public static final String PUSH_SET_GOOGLE_PUSH_ID = "Push.setGooglePushId";
    // File Storage
    public static final String FILE_STORAGE_SET_PROFILE = "FileStorage.setProfile";

    // API variables constants
    public static final String PHONE = "phone";
    public static final String CODE = "code";
    public static final String PROGRAM_ID = "program_id";
    public static final String MODULE_ID = "module_id";
    public static final String TEST_ID = "test_id";
    public static final String DATA = "data";
    public static final String QUESTION_HASH = "question_hash";
    public static final String ANSWERS = "answers";
    public static final String ANSWERS_HASH = "answer_hash";
    public static final String IS_CORRECT = "is_correct";
    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";
    public static final String ORDER = "order";
    public static final String ARTICLE_ID = "article_id";
    public static final String CONTENT = "content";
    public static final String START = "start";
    public static final String END = "end";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String TYPE = "type";
    public static final String LOCATION = "location";
    public static final String SHARE_WITH = "shared_with";
    public static final String EVENT_ID = "event_id";
    public static final String MEMBER_IDS = "member_ids";
    public static final String USER_ID = "user_id";
    public static final String NUM = "num";
    public static final String IMG = "img";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String LEVEL = "level";
    public static final String TS = "ts";
    public static final String FILE_HASH = "file_hash";
    public static final String DELETE_SOURCE = "delete_source";
    public static final String NETWORK = "network";
    public static final String METADATA = "metadata";
    public static final String SHARE_ALL = "share_all";
    public static final String DIRECT_LINK = "direct_link";
    public static final String BRAND_ID_UNDERLINE = "brand_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String LAST_NAME = "LastName";
    public static final String FIRST_NAME = "FirstName";
    public static final String PATRONYMIC = "Patronymic";
    public static final String SEX = "sex";
    public static final String AGE = "age";
    public static final String EMAIL = "Email";
    public static final String KEY = "key";
    public static final String CANCEL = "cancel";
    public static final String CARD_NUMBER = "card_number";
    public static final String DESTINATION_CARDNUMBER = "destination_cardnumber";
    public static final String USER_IDS = "user_ids";
    public static final String ROOM_ID = "room_id";
    public static final String GOOGLE_PUSH_ID = "google_push_id";
    public static final String PLAN_ID = "plan_id";
    public static final String ID = "id";

    // API response variables constants
    public static final String SUCCESS = "success";
    public static final String TOKEN = "token";
    public static final String PARENT_ID = "parent_id";
    public static final String JABBER_ID = "jabber_id";
    public static final String DATE_ADD = "dateAdd";
    public static final String LOGIN = "login";
    public static final String IN_SYSTEM = "in_system";
    public static final String TOTAL_SUM = "total_sum";
    public static final String CREATE_AT = "created_at";
    public static final String UPDATE_AT = "updated_at";
    public static final String MATERIALS = "materials";
    public static final String CONTENT_TYPE = "content_type";
    public static final String PRIORITY_TYPE = "priority_type";
    public static final String EXTRADATA = "extradata";
    public static final String SOURCE = "source";
    public static final String LINK = "link";
    public static final String SORT_WEIGHT = "sort_weight";
    public static final String PASS_LIMIT = "pass_limit";
    public static final String QUESTIONS = "questions";
    public static final String QUESTION_TEXT = "question_text";
    public static final String ANSWER_TEXT = "answer_text";
    public static final String PASS_RESULT = "pass_result";
    public static final String PASSED = "passed";
    public static final String TITLE = "title";
    public static final String SHORT_DESCR = "short_descr";
    public static final String FULL_DESCR = "full_descr";
    public static final String THUMBNAIL = "thumbnail";
    public static final String IS_HIDDEN = "is_hidden";
    public static final String POSTED_AT = "posted_at";
    public static final String COMMENTS = "comments";
    public static final String AUTHOR_ID = "author_id";
    public static final String AUTHOR = "author";
    public static final String OWNER_ID = "owner_id";
    public static final String START_DATETIME = "start_datetime";
    public static final String REFERRALS = "referrals";
    public static final String PARENTS = "parents";
    public static final String END_DATETIME = "end_datetime";
    public static final String PUSH_RESULT = "push_result";
    public static final String HEADERS = "headers";
    public static final String MESSAGE = "message";
    public static final String ERROR = "error";
    public static final String STATUS = "status";
    public static final String SIZE = "size";
    public static final String HASH_NAME = "hash_name";
    public static final String CREATE_DATE = "create_date";
    public static final String UPDATE_DATE = "update_date";
    public static final String OWNER = "owner";
    public static final String FAVORITE = "favorite";
    public static final String BRAND_CATEGORIES = "brandCategories";
    public static final String IMAGE = "image";
    public static final String SHORT_DESCRIPTION = "shortDescription";
    public static final String FULL_DESCRIPTION = "fullDescription";
    public static final String CATEGORY_ID = "categoryId";
    public static final String BRAND_ID = "brandId";
    public static final String PRODUCTS = "products";
    public static final String BRAND = "brand";
    public static final String SHORT_DESCRIPTION_UNDERLINE = "short_description";
    public static final String FULL_DESCRIPTION_UNDERLINE = "full_description";
    public static final String CATEGORY_ID_UNDERLINE = "category_id";
    public static final String HTML = "html";
    public static final String ORDER_STATUS = "order_status";
    public static final String SUM = "sum";
    public static final String HASH = "hash";
    public static final String CURRENCY = "currency";
    public static final String PROCESSING_DATA = "processing_data";
    public static final String AMOUNT = "amount";
    public static final String USER_NAME = "userName";
    public static final String USERID = "userId";
    public static final String ROOM = "room";
    public static final String DATE = "date";
    public static final String JABBERID = "jabberId";
    public static final String FILENAME = "fileName";
    public static final String FORM_ID = "formId";
    public static final String PUSH_DESCRIPTION = "pushDescription";

    // API action constants
    // Authorization
    public static final String ACTION_AUTH_GET_VERIFICATION = BuildConfig.APPLICATION_ID + ".ACTION_AUTH_GET_VERIFICATION";
    public static final String ACTION_AUTH_CHECK_VERIFICATION = BuildConfig.APPLICATION_ID + ".ACTION_AUTH_CHECK_VERIFICATION";
    public static final String ACTION_AUTH_GET_LOGGED_USER_INFO = BuildConfig.APPLICATION_ID + ".ACTION_AUTH_GET_LOGGED_USER_INFO";
    // Education
    public static final String ACTION_EDUCATION_GET_PROGRAMS = BuildConfig.APPLICATION_ID + ".ACTION_EDUCATION_GET_PROGRAMS";
    public static final String ACTION_EDUCATION_GET_MODULES_BY_PROGRAM_ID = BuildConfig.APPLICATION_ID + ".ACTION_EDUCATION_GET_MODULES_BY_PROGRAM_ID";
    public static final String ACTION_EDUCATION_GET_MATERIALS_BY_MODULE_ID = BuildConfig.APPLICATION_ID + ".ACTION_EDUCATION_GET_MATERIALS_BY_MODULE_ID";
    public static final String ACTION_EDUCATION_GET_TESTS = BuildConfig.APPLICATION_ID + ".ACTION_EDUCATION_GET_TESTS";
    public static final String ACTION_EDUCATION_PASS_TESTS = BuildConfig.APPLICATION_ID + ".ACTION_EDUCATION_PASS_TESTS";
    // News Section
    public static final String ACTION_ARTICLES_GET = BuildConfig.APPLICATION_ID + ".ACTION_ARTICLES_GET";
    public static final String ACTION_ARTICLES_GET_SHORT_DESCRIPTION = BuildConfig.APPLICATION_ID + ".ACTION_ARTICLES_GET_SHORT_DESCRIPTION";
    public static final String ACTION_ARTICLES_GET_FULL_DESCRIPTION = BuildConfig.APPLICATION_ID + ".ACTION_ARTICLES_GET_FULL_DESCRIPTION";
    public static final String ACTION_ARTICLES_ADD_COMMENT = BuildConfig.APPLICATION_ID + ".ACTION_ARTICLES_ADD_COMMENT";
    public static final String ACTION_ARTICLES_FULL_GET_COMMENTS = BuildConfig.APPLICATION_ID + ".ACTION_ARTICLES_FULL_GET_COMMENTS";
    // Calendar
    public static final String ACTION_CALENDAR_GET_EVENTS = BuildConfig.APPLICATION_ID + ".ACTION_CALENDAR_GET_EVENTS";
    public static final String ACTION_CALENDAR_ADD_EVENT = BuildConfig.APPLICATION_ID + ".ACTION_CALENDAR_ADD_EVENT";
    public static final String ACTION_CALENDAR_UPDATE_EVENT = BuildConfig.APPLICATION_ID + ".ACTION_CALENDAR_UPDATE_EVENT";
    public static final String ACTION_CALENDAR_REMOVE_EVENT = BuildConfig.APPLICATION_ID + ".ACTION_CALENDAR_REMOVE_EVENT";
    public static final String ACTION_CALENDAR_EVENT_GET_ALL_SHARED_WITH_ME = BuildConfig.APPLICATION_ID + ".ACTION_CALENDAR_EVENT_GET_ALL_SHARED_WITH_ME";
    public static final String ACTION_CALENDAR_EVENT_GET_ALL_OWN = BuildConfig.APPLICATION_ID + ".ACTION_CALENDAR_EVENT_GET_ALL_OWN";
    public static final String ACTION_CALENDAR_EVENT_SHARE_WITH = BuildConfig.APPLICATION_ID + ".ACTION_CALENDAR_EVENT_SHARE_WITH";
    public static final String ACTION_CALENDAR_EVENT_AVAILABLE_TYPES = BuildConfig.APPLICATION_ID + ".ACTION_CALENDAR_EVENT_AVAILABLE_TYPES";
    // Account
    public static final String ACTION_ACCOUNT_GET_ALL_CONTACTS = BuildConfig.APPLICATION_ID + ".ACTION_ACCOUNT_GET_ALL_CONTACTS";
    public static final String ACTION_ACCOUNT_GET_REFERRALS_BY_ID = BuildConfig.APPLICATION_ID + ".ACTION_ACCOUNT_GET_REFERRALS_BY_ID";
    public static final String ACTION_ACCOUNT_GET_PARENTS = BuildConfig.APPLICATION_ID + ".ACTION_ACCOUNT_GET_PARENTS";
    public static final String ACTION_ACCOUNT_ADD_REFERRAL = BuildConfig.APPLICATION_ID + ".ACTION_ACCOUNT_ADD_REFERRAL";
    public static final String ACTION_ACCOUNT_GET_USER_PANEL_INFO = BuildConfig.APPLICATION_ID + ".ACTION_ACCOUNT_GET_USER_PANEL_INFO";
    public static final String ACTION_ACCOUNT_ADD_USER_PANEL_INFO = BuildConfig.APPLICATION_ID + ".ACTION_ACCOUNT_ADD_USER_PANEL_INFO";
    public static final String ACTION_ACCOUNT_ADD_USER_LOCATION = BuildConfig.APPLICATION_ID + ".ACTION_ACCOUNT_ADD_USER_LOCATION";
    public static final String ACTION_ACCOUNT_SET_AVATAR_FILE = BuildConfig.APPLICATION_ID + ".ACTION_ACCOUNT_SET_AVATAR_FILE";
    // Social
    public static final String ACTION_SOCIAL_SAVE_METADATA = BuildConfig.APPLICATION_ID + ".ACTION_SOCIAL_SAVE_METADATA";
    // File
    public static final String ACTION_FILE_GET_SHARE_WITH = BuildConfig.APPLICATION_ID + ".ACTION_FILE_GET_SHARE_WITH";
    public static final String ACTION_FILE_ADD_SHARE_WITH = BuildConfig.APPLICATION_ID + ".ACTION_FILE_ADD_SHARE_WITH";
    public static final String ACTION_FILE_DROP_SHARE_WITH = BuildConfig.APPLICATION_ID + ".ACTION_FILE_DROP_SHARE_WITH";
    public static final String ACTION_FILE_UNLINK_SHARED_FILE = BuildConfig.APPLICATION_ID + ".ACTION_FILE_UNLINK_SHARED_FILE";
    public static final String ACTION_FILE_SHARE_ALL = BuildConfig.APPLICATION_ID + ".ACTION_FILE_SHARE_ALL";
    public static final String ACTION_FILE_COPY_SHARED_TO_ME = BuildConfig.APPLICATION_ID + ".ACTION_FILE_COPY_SHARED_TO_ME";
    public static final String ACTION_FILE_GET_PROPERTIES_BY_HASH = BuildConfig.APPLICATION_ID + ".ACTION_FILE_GET_PROPERTIES_BY_HASH";
    public static final String ACTION_FILE_GET_MY_FILES = BuildConfig.APPLICATION_ID + ".ACTION_FILE_GET_MY_FILES";
    public static final String ACTION_FILE_GET_ALL_SHARED_WITH_ME = BuildConfig.APPLICATION_ID + ".ACTION_FILE_GET_ALL_SHARED_WITH_ME";
    public static final String ACTION_FILE_GET_ALL = BuildConfig.APPLICATION_ID + ".ACTION_FILE_GET_ALL";
    public static final String ACTION_FILE_DELETE = BuildConfig.APPLICATION_ID + ".ACTION_FILE_DELETE";
    public static final String ACTION_FILE_ADD_TO_FAVORITE = BuildConfig.APPLICATION_ID + ".ACTION_FILE_ADD_TO_FAVORITE";
    public static final String ACTION_FILE_DROP_FROM_FAVORITE = BuildConfig.APPLICATION_ID + ".ACTION_FILE_DROP_FROM_FAVORITE";
    public static final String ACTION_FILE_DIRECT_LINK = BuildConfig.APPLICATION_ID + ".ACTION_FILE_DIRECT_LINK";
    // Products
    public static final String ACTION_PRODUCTS_GET_ALL_CATEGORIES = BuildConfig.APPLICATION_ID + ".ACTION_PRODUCTS_GET_ALL_CATEGORIES";
    public static final String ACTION_PRODUCTS_GET_PRODUCT_CATEGORY_BY_ID = BuildConfig.APPLICATION_ID + ".ACTION_PRODUCTS_GET_PRODUCT_CATEGORY_BY_ID";
    public static final String ACTION_PRODUCTS_GET_BRAND_CATEGORY_BY_ID = BuildConfig.APPLICATION_ID + ".ACTION_PRODUCTS_GET_BRAND_CATEGORY_BY_ID";
    public static final String ACTION_PRODUCTS_GET_BRAND_CATEGORIES = BuildConfig.APPLICATION_ID + ".ACTION_PRODUCTS_GET_BRAND_CATEGORIES";
    public static final String ACTION_PRODUCTS_GET_HTML_BY_ID = BuildConfig.APPLICATION_ID + ".ACTION_PRODUCTS_GET_HTML_BY_ID";
    public static final String ACTION_PRODUCTS_BRANDS_GET_ALL = BuildConfig.APPLICATION_ID + ".ACTION_PRODUCTS_BRANDS_GET_ALL";
    public static final String ACTION_PRODUCTS_BRANDS_GET_BY_ID = BuildConfig.APPLICATION_ID + ".ACTION_PRODUCTS_BRANDS_GET_BY_ID";
    public static final String ACTION_PRODUCTS_BRANDS_GET_BY_NAME = BuildConfig.APPLICATION_ID + ".ACTION_PRODUCTS_BRANDS_GET_BY_NAME";
    // Orders
    public static final String ACTION_PRODUCTS_ORDER_CREATE = BuildConfig.APPLICATION_ID + ".ACTION_PRODUCTS_ORDER_CREATE";
    public static final String ACTION_PRODUCTS_ORDER_GET_FORM = BuildConfig.APPLICATION_ID + ".ACTION_PRODUCTS_ORDER_GET_FORM";
    public static final String ACTION_PRODUCTS_ORDER_RESPONSE_FORM = BuildConfig.APPLICATION_ID + ".ACTION_PRODUCTS_ORDER_RESPONSE_FORM";
    // WebRTC
    public static final String ACTION_WEB_RTC_START_CALL = BuildConfig.APPLICATION_ID + ".ACTION_WEB_RTC_START_CALL";
    public static final String ACTION_WEB_RTC_STOP_CALL = BuildConfig.APPLICATION_ID + ".ACTION_WEB_RTC_STOP_CALL";
    // Jabber
    public static final String ACTION_JABBER_START_CHAT = BuildConfig.APPLICATION_ID + ".ACTION_JABBER_START_CHAT";
    // Push
    public static final String ACTION_PUSH_SET_GOOGLE_PUSH_ID = BuildConfig.APPLICATION_ID + ".ACTION_PUSH_SET_GOOGLE_PUSH_ID";
    // File Storage
    public static final String ACTION_FILE_STORAGE_SET_PROFILE = BuildConfig.APPLICATION_ID + ".ACTION_FILE_STORAGE_SET_PROFILE";

    public static boolean isActionOwn(String action) {
        return ACTION_AUTH_GET_VERIFICATION.equals(action)
                || ACTION_AUTH_CHECK_VERIFICATION.equals(action)
                || ACTION_AUTH_GET_LOGGED_USER_INFO.equals(action)

                || ACTION_EDUCATION_GET_PROGRAMS.equals(action)
                || ACTION_EDUCATION_GET_MODULES_BY_PROGRAM_ID.equals(action)
                || ACTION_EDUCATION_GET_MATERIALS_BY_MODULE_ID.equals(action)
                || ACTION_EDUCATION_GET_TESTS.equals(action)
                || ACTION_EDUCATION_PASS_TESTS.equals(action)

                || ACTION_ARTICLES_GET.equals(action)
                || ACTION_ARTICLES_GET_SHORT_DESCRIPTION.equals(action)
                || ACTION_ARTICLES_GET_FULL_DESCRIPTION.equals(action)
                || ACTION_ARTICLES_ADD_COMMENT.equals(action)
                || ACTION_ARTICLES_FULL_GET_COMMENTS.equals(action)

                || ACTION_CALENDAR_GET_EVENTS.equals(action)
                || ACTION_CALENDAR_ADD_EVENT.equals(action)
                || ACTION_CALENDAR_UPDATE_EVENT.equals(action)
                || ACTION_CALENDAR_REMOVE_EVENT.equals(action)
                || ACTION_CALENDAR_EVENT_GET_ALL_SHARED_WITH_ME.equals(action)
                || ACTION_CALENDAR_EVENT_GET_ALL_OWN.equals(action)
                || ACTION_CALENDAR_EVENT_SHARE_WITH.equals(action)
                || ACTION_CALENDAR_EVENT_AVAILABLE_TYPES.equals(action)

                || ACTION_ACCOUNT_GET_ALL_CONTACTS.equals(action)
                || ACTION_ACCOUNT_GET_REFERRALS_BY_ID.equals(action)
                || ACTION_ACCOUNT_GET_PARENTS.equals(action)
                || ACTION_ACCOUNT_ADD_REFERRAL.equals(action)
                || ACTION_ACCOUNT_GET_USER_PANEL_INFO.equals(action)
                || ACTION_ACCOUNT_ADD_USER_PANEL_INFO.equals(action)
                || ACTION_ACCOUNT_ADD_USER_LOCATION.equals(action)
                || ACTION_ACCOUNT_SET_AVATAR_FILE.equals(action)

                || ACTION_SOCIAL_SAVE_METADATA.equals(action)

                || ACTION_FILE_GET_SHARE_WITH.equals(action)
                || ACTION_FILE_ADD_SHARE_WITH.equals(action)
                || ACTION_FILE_DROP_SHARE_WITH.equals(action)
                || ACTION_FILE_UNLINK_SHARED_FILE.equals(action)
                || ACTION_FILE_SHARE_ALL.equals(action)
                || ACTION_FILE_COPY_SHARED_TO_ME.equals(action)
                || ACTION_FILE_GET_PROPERTIES_BY_HASH.equals(action)
                || ACTION_FILE_GET_MY_FILES.equals(action)
                || ACTION_FILE_GET_ALL_SHARED_WITH_ME.equals(action)
                || ACTION_FILE_GET_ALL.equals(action)
                || ACTION_FILE_DELETE.equals(action)
                || ACTION_FILE_ADD_TO_FAVORITE.equals(action)
                || ACTION_FILE_DROP_FROM_FAVORITE.equals(action)
                || ACTION_FILE_DIRECT_LINK.equals(action)

                || ACTION_PRODUCTS_GET_ALL_CATEGORIES.equals(action)
                || ACTION_PRODUCTS_GET_PRODUCT_CATEGORY_BY_ID.equals(action)
                || ACTION_PRODUCTS_GET_BRAND_CATEGORY_BY_ID.equals(action)
                || ACTION_PRODUCTS_GET_BRAND_CATEGORIES.equals(action)
                || ACTION_PRODUCTS_GET_HTML_BY_ID.equals(action)
                || ACTION_PRODUCTS_BRANDS_GET_ALL.equals(action)
                || ACTION_PRODUCTS_BRANDS_GET_BY_ID.equals(action)
                || ACTION_PRODUCTS_BRANDS_GET_BY_NAME.equals(action)

                || ACTION_PRODUCTS_ORDER_CREATE.equals(action)
                || ACTION_PRODUCTS_ORDER_GET_FORM.equals(action)
                || ACTION_PRODUCTS_ORDER_RESPONSE_FORM.equals(action)

                || ACTION_WEB_RTC_START_CALL.equals(action)
                || ACTION_WEB_RTC_STOP_CALL.equals(action)

                || ACTION_JABBER_START_CHAT.equals(action)

                || ACTION_PUSH_SET_GOOGLE_PUSH_ID.equals(action)

                || ACTION_FILE_STORAGE_SET_PROFILE.equals(action);
    }

    public static boolean isActionVerification(String action) {
        return ACTION_AUTH_GET_VERIFICATION.equals(action)
                || ACTION_AUTH_CHECK_VERIFICATION.equals(action)
                || ACTION_AUTH_GET_LOGGED_USER_INFO.equals(action);
    }

    public static boolean isActionEducation(String action) {
        return ACTION_EDUCATION_GET_PROGRAMS.equals(action)
                || ACTION_EDUCATION_GET_MODULES_BY_PROGRAM_ID.equals(action)
                || ACTION_EDUCATION_GET_MATERIALS_BY_MODULE_ID.equals(action)
                || ACTION_EDUCATION_GET_TESTS.equals(action)
                || ACTION_EDUCATION_PASS_TESTS.equals(action);
    }

    public static boolean isActionArticles(String action) {
        return ACTION_ARTICLES_GET.equals(action)
                || ACTION_ARTICLES_GET_SHORT_DESCRIPTION.equals(action)
                || ACTION_ARTICLES_GET_FULL_DESCRIPTION.equals(action)
                || ACTION_ARTICLES_ADD_COMMENT.equals(action)
                || ACTION_ARTICLES_FULL_GET_COMMENTS.equals(action);
    }

    public static boolean isActionCalendar(String action) {
        return ACTION_CALENDAR_GET_EVENTS.equals(action)
                || ACTION_CALENDAR_ADD_EVENT.equals(action)
                || ACTION_CALENDAR_UPDATE_EVENT.equals(action)
                || ACTION_CALENDAR_REMOVE_EVENT.equals(action)
                || ACTION_CALENDAR_EVENT_GET_ALL_SHARED_WITH_ME.equals(action)
                || ACTION_CALENDAR_EVENT_GET_ALL_OWN.equals(action)
                || ACTION_CALENDAR_EVENT_SHARE_WITH.equals(action)
                || ACTION_CALENDAR_EVENT_AVAILABLE_TYPES.equals(action);
    }

    public static boolean isActionAccount(String action) {
        return ACTION_ACCOUNT_GET_ALL_CONTACTS.equals(action)
                || ACTION_ACCOUNT_GET_REFERRALS_BY_ID.equals(action)
                || ACTION_ACCOUNT_GET_PARENTS.equals(action)
                || ACTION_ACCOUNT_ADD_REFERRAL.equals(action)
                || ACTION_ACCOUNT_GET_USER_PANEL_INFO.equals(action)
                || ACTION_ACCOUNT_ADD_USER_PANEL_INFO.equals(action)
                || ACTION_ACCOUNT_ADD_USER_LOCATION.equals(action)
                || ACTION_ACCOUNT_SET_AVATAR_FILE.equals(action);
    }

    public static boolean isActionSocial(String action) {
        return ACTION_SOCIAL_SAVE_METADATA.equals(action);
    }

    public static boolean isActionFile(String action) {
        return ACTION_FILE_GET_SHARE_WITH.equals(action)
                || ACTION_FILE_ADD_SHARE_WITH.equals(action)
                || ACTION_FILE_DROP_SHARE_WITH.equals(action)
                || ACTION_FILE_UNLINK_SHARED_FILE.equals(action)
                || ACTION_FILE_SHARE_ALL.equals(action)
                || ACTION_FILE_COPY_SHARED_TO_ME.equals(action)
                || ACTION_FILE_GET_PROPERTIES_BY_HASH.equals(action)
                || ACTION_FILE_GET_MY_FILES.equals(action)
                || ACTION_FILE_GET_ALL_SHARED_WITH_ME.equals(action)
                || ACTION_FILE_GET_ALL.equals(action)
                || ACTION_FILE_DELETE.equals(action)
                || ACTION_FILE_ADD_TO_FAVORITE.equals(action)
                || ACTION_FILE_DROP_FROM_FAVORITE.equals(action)
                || ACTION_FILE_DIRECT_LINK.equals(action);
    }

    public static boolean isActionProducts(String action) {
        return ACTION_PRODUCTS_GET_ALL_CATEGORIES.equals(action)
                || ACTION_PRODUCTS_GET_PRODUCT_CATEGORY_BY_ID.equals(action)
                || ACTION_PRODUCTS_GET_BRAND_CATEGORY_BY_ID.equals(action)
                || ACTION_PRODUCTS_GET_BRAND_CATEGORIES.equals(action)
                || ACTION_PRODUCTS_GET_HTML_BY_ID.equals(action)
                || ACTION_PRODUCTS_BRANDS_GET_ALL.equals(action)
                || ACTION_PRODUCTS_BRANDS_GET_BY_ID.equals(action)
                || ACTION_PRODUCTS_BRANDS_GET_BY_NAME.equals(action);
    }

    public static boolean isActionOrder(String action) {
        return ACTION_PRODUCTS_ORDER_CREATE.equals(action)
                || ACTION_PRODUCTS_ORDER_GET_FORM.equals(action)
                || ACTION_PRODUCTS_ORDER_RESPONSE_FORM.equals(action);
    }

    public static boolean isActionWebRTC(String action) {
        return ACTION_WEB_RTC_START_CALL.equals(action)
                || ACTION_WEB_RTC_STOP_CALL.equals(action);
    }

    public static boolean isActionJabber(String action) {
        return ACTION_JABBER_START_CHAT.equals(action);
    }

    public static boolean isActionGooglePush(String action) {
        return ACTION_PUSH_SET_GOOGLE_PUSH_ID.equals(action);
    }

    public static boolean isActionFileStorage(String action) {
        return ACTION_FILE_STORAGE_SET_PROFILE.equals(action);
    }
}
