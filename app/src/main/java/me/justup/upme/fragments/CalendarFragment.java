package me.justup.upme.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.db.DBHelper;
import me.justup.upme.entity.ArticleShortCommentEntity;
import me.justup.upme.entity.CalendarGetEventsQuery;
import me.justup.upme.entity.EventEntity;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.utils.AppContext;
import me.justup.upme.weekview.WeekView;
import me.justup.upme.weekview.WeekViewEvent;

import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_DESCRIPTION;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_NAME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_SERVER_ID;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_START_DATETIME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_TABLE_NAME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_TYPE;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGI;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class CalendarFragment extends Fragment implements View.OnClickListener, WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EmptyViewClickListener {
    private static final String TAG = makeLogTag(CalendarFragment.class);

    private static final int START_TIME_EVENT = 1;
    private static final int DURATION_EVENT = 2;

    private WeekView mWeekView;
    private Dialog dialogSteTimeCalendar;
    private Dialog dialogInfoEvent;
    List<WeekViewEvent> events;

    private RelativeLayout panelAddEvent;
    private TextView tvStartTimeEvent;
    private TextView tvDurationEvent;
    private EditText etNewEventName;
    private EditText etNewEventLocation;

    private Calendar startTimeEvent;
    private int durationEventMin;

    private TextView selectWeekTextView;

    private final DateTime currentDate = new DateTime();
    private int currentWeek;
    private DateTime firstDayCurrentWeek;

    private DBAdapter mDBAdapter;
    private DBHelper mDBHelper;
    private String selectQueryEvents;
    private List<EventEntity> mEventEntityList;
    private BroadcastReceiver receiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentWeek = currentDate.getWeekOfWeekyear();
        firstDayCurrentWeek = currentDate.withDayOfWeek(1);

        events = new ArrayList<>();

        ////////////////////////////////////////////////////////////////////////////////////////////
        mDBHelper  = new DBHelper(AppContext.getAppContext());
        mDBAdapter = new DBAdapter(AppContext.getAppContext());
        mDBAdapter.open();
        selectQueryEvents = "SELECT * FROM " + EVENT_CALENDAR_TABLE_NAME;
        Cursor cursorEvents = mDBHelper.getWritableDatabase().rawQuery(selectQueryEvents, null);
        mEventEntityList = fillNewsFromCursor(cursorEvents);
        LOGD("TAG_", mEventEntityList.toString());


        if (cursorEvents != null)
            cursorEvents.close();
    }

    private List<EventEntity> fillNewsFromCursor(Cursor cursorEvents) {

        ArrayList<EventEntity> eventsList = new ArrayList<>();

        for (cursorEvents.moveToFirst(); !cursorEvents.isAfterLast(); cursorEvents.moveToNext()) {
            EventEntity articlesResponse = new EventEntity();
            articlesResponse.setId(cursorEvents.getInt(cursorEvents.getColumnIndex(EVENT_CALENDAR_SERVER_ID)));
            articlesResponse.setName(cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_NAME)));
            articlesResponse.setDescription(cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_DESCRIPTION)));
            articlesResponse.setType(cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_TYPE)));
            articlesResponse.setStart_datetime(cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_START_DATETIME)));
            int event_id = cursorEvents.getInt(cursorEvents.getColumnIndex(EVENT_CALENDAR_SERVER_ID));

            //// start
            String selectQueryShortNewsComments = "SELECT * FROM short_news_comments_table WHERE article_id=" + event_id;
            Cursor cursorComments = mDBHelper.getWritableDatabase().rawQuery(selectQueryShortNewsComments, null);
            ArrayList<ArticleShortCommentEntity> commentsList = new ArrayList<>();
            eventsList.add(articlesResponse);

            if (cursorComments != null) {
                cursorComments.close();
            }
        }
        return eventsList;
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(CalendarFragment.this.getActivity()).unregisterReceiver(receiver);
        LOGI(TAG, "unregisterRecNewsFeed");
        mDBAdapter.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        LOGI(TAG, "RegisterRecNewsFeed");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LOGI(TAG, "onReceive, first update");
                Cursor cursorEvents = mDBHelper.getWritableDatabase().rawQuery(selectQueryEvents, null);
                mEventEntityList = fillNewsFromCursor(cursorEvents);
                LOGD("TAG_", mEventEntityList.toString());
            }
        };

        LocalBroadcastManager.getInstance(CalendarFragment.this.getActivity())
                .registerReceiver(receiver, new IntentFilter(DBAdapter.CALENDAR_SQL_BROADCAST_INTENT));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        mWeekView = (WeekView) v.findViewById(R.id.weekView);

        TextView currentDateTextView = (TextView) v.findViewById(R.id.current_date_textView);
        currentDateTextView.setText(currentDate.toString("MMMM d, yyyy", new Locale("ru")));
        TextView selectMonthTextView = (TextView) v.findViewById(R.id.select_month_textView);
        selectMonthTextView.setText(currentDate.toString("MMMM yyyy", new Locale("ru")));
        selectWeekTextView = (TextView) v.findViewById(R.id.select_week_textView);
        selectWeekTextView.setText(Integer.toString(currentWeek) + getResources().getString(R.string.week));

        Button previousWeekButton = (Button) v.findViewById(R.id.previous_week_button);
        previousWeekButton.setOnClickListener(this);
        Button nextWeekButton = (Button) v.findViewById(R.id.next_week_button);
        nextWeekButton.setOnClickListener(this);

        /////////////////////////////// RIGHT PANEL ///////////////////////////////

        panelAddEvent = (RelativeLayout) v.findViewById(R.id.panel_add_event);

        Button calendarItemCloseButton = (Button) v.findViewById(R.id.calendar_item_close_button);
        calendarItemCloseButton.setOnClickListener(this);
        tvStartTimeEvent = (TextView) v.findViewById(R.id.start_time_event);
        tvStartTimeEvent.setOnClickListener(this);
        tvDurationEvent = (TextView) v.findViewById(R.id.duration_event);
        tvDurationEvent.setOnClickListener(this);
        etNewEventName = (EditText) v.findViewById(R.id.new_event_name);
        etNewEventLocation = (EditText) v.findViewById(R.id.new_event_location);
        Button addNewEventButton = (Button) v.findViewById(R.id.add_new_event_button);
        addNewEventButton.setOnClickListener(this);

        /////////////////////////////// CALENDAR ///////////////////////////////

        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEmptyViewClickListener(this);

        return v;
    }

    /////////////////////////////// DIALOG ///////////////////////////////

    public void TimePickerDialog(final int typeDialog) {
        dialogSteTimeCalendar = new Dialog(getActivity());
        dialogSteTimeCalendar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSteTimeCalendar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogSteTimeCalendar.setContentView(R.layout.dialog_time_picker);
        Button buttonOk = (Button) dialogSteTimeCalendar.findViewById(R.id.buttonDialogCalendarTimeOk);
        final NumberPicker numberPickerHours = (NumberPicker) dialogSteTimeCalendar.findViewById(R.id.numberPickerHours);
        final NumberPicker numberPickerMinutes = (NumberPicker) dialogSteTimeCalendar.findViewById(R.id.numberPickerMinutes);
        numberPickerHours.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerMinutes.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerHours.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        numberPickerMinutes.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        numberPickerHours.setMaxValue(23);
        numberPickerMinutes.setMaxValue(59);
        if (typeDialog == START_TIME_EVENT) {
            numberPickerHours.setValue(startTimeEvent.get(Calendar.HOUR));
            numberPickerMinutes.setValue(startTimeEvent.get(Calendar.MINUTE));
        } else if (typeDialog == DURATION_EVENT) {
            int minute = durationEventMin % 60;
            int hour = (durationEventMin - minute) / 60;
            numberPickerHours.setValue(hour);
            numberPickerMinutes.setValue(minute);
        }
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String convertedTime = convertTimeToString(numberPickerHours.getValue(), numberPickerMinutes.getValue());
                if (typeDialog == START_TIME_EVENT) {
                    startTimeEvent.set(Calendar.HOUR, numberPickerHours.getValue());
                    startTimeEvent.set(Calendar.MINUTE, numberPickerMinutes.getValue());
                    tvStartTimeEvent.setText(convertedTime);
                    dialogSteTimeCalendar.dismiss();
                } else if (typeDialog == DURATION_EVENT) {
                    durationEventMin = numberPickerHours.getValue() * 60 + numberPickerMinutes.getValue();
                    tvDurationEvent.setText(convertedTime);
                    dialogSteTimeCalendar.dismiss();
                }
            }
        });
        dialogSteTimeCalendar.show();
    }

    public void InfoDialog(final String Object) {
        dialogInfoEvent = new Dialog(getActivity());
        dialogInfoEvent.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInfoEvent.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogInfoEvent.setContentView(R.layout.dialog_info_event);
        final TextView userNameTextView = (TextView) dialogInfoEvent.findViewById(R.id.user_name_textView);
        userNameTextView.setText(Object);
        final Button cancelButton = (Button) dialogInfoEvent.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInfoEvent.dismiss();
            }
        });
        dialogInfoEvent.show();
    }


    public static String convertTimeToString(int hours, int minutes) {
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes);
    }


    //////////////////////////////////////////////////////////////////

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        InfoDialog(event.getName());
    }

    @Override
    public void onEmptyViewClicked(Calendar time) {
        startTimeEvent = time;
        panelAddEvent.setVisibility(View.VISIBLE);
        String stTime = String.format("%02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
        tvStartTimeEvent.setText(stTime);
        tvDurationEvent.setText("00:00");
        etNewEventName.setText("");
        etNewEventLocation.setText("");
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        return events;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previous_week_button:
                firstDayCurrentWeek = firstDayCurrentWeek.minusDays(Calendar.DAY_OF_WEEK);
                mWeekView.goToDate(firstDayCurrentWeek.toGregorianCalendar());
                selectWeekTextView.setText(Integer.toString(--currentWeek) + getResources().getString(R.string.week));
                break;
            case R.id.next_week_button:
                firstDayCurrentWeek = firstDayCurrentWeek.plusDays(Calendar.DAY_OF_WEEK);
                mWeekView.goToDate(firstDayCurrentWeek.toGregorianCalendar());
                selectWeekTextView.setText(Integer.toString(++currentWeek) + getResources().getString(R.string.week));
                break;
            case R.id.calendar_item_close_button:
                panelAddEvent.setVisibility(View.GONE);
                break;
            case R.id.start_time_event:
                TimePickerDialog(START_TIME_EVENT);
                break;
            case R.id.duration_event:
                TimePickerDialog(DURATION_EVENT);
                break;
            case R.id.add_new_event_button:
                Calendar endTimeEvent = (Calendar) startTimeEvent.clone();
                int minute = durationEventMin % 60;
                int hour = (durationEventMin - minute) / 60;
                endTimeEvent.add(Calendar.HOUR, hour);
                endTimeEvent.add(Calendar.MINUTE, minute);
                String eventName = etNewEventName.getText().toString();
                String eventLocation = etNewEventLocation.getText().toString();
                WeekViewEvent event = new WeekViewEvent(1, eventName, startTimeEvent, endTimeEvent);
                event.setColor(getResources().getColor(R.color.event_color_01));
                events.add(event);
                mWeekView.notifyDatasetChanged();
                panelAddEvent.setVisibility(View.GONE);

                CalendarGetEventsQuery calendarGetEventsQuery = new CalendarGetEventsQuery();
//                calendarGetEventsQuery.params.name = eventName;
//                calendarGetEventsQuery.params.description = "description";
//                calendarGetEventsQuery.params.type = "reminder";
//                calendarGetEventsQuery.params.location = eventLocation;
//                calendarGetEventsQuery.params.start_date_time = 1;
//                calendarGetEventsQuery.params.end_date_time = 1;

                //ApiWrapper.query(calendarAddEventQuery, new OnAddEventResponce());
                break;
        }

    }

    private class OnAddEventResponce extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            String content = ApiWrapper.responseBodyToString(responseBody);
            LOGD(TAG, "onSuccess(): " + content);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        }
    }
}

//        String URL = "http://justup.me/";
//        WebView mWebView = (WebView) v.findViewById(R.id.root_webview);
//        mWebView.getSettings().setJavaScriptEnabled(true);
//        mWebView.getSettings().setLoadWithOverviewMode(true);
//        mWebView.getSettings().setUseWideViewPort(true);
//        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//        mWebView.setWebChromeClient(new WebChromeClient());
//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//        });
//        mWebView.setInitialScale(1);
//        mWebView.loadUrl(URL);
