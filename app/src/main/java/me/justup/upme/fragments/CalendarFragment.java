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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.db.DBHelper;
import me.justup.upme.entity.ArticleShortCommentEntity;
import me.justup.upme.entity.CalendarAddEventQuery;
import me.justup.upme.entity.EventEntity;
import me.justup.upme.http.ApiWrapper;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.AppContext;
import me.justup.upme.weekview.WeekView;
import me.justup.upme.weekview.WeekViewEvent;

import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_DESCRIPTION;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_END_DATETIME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_LOCATION;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_NAME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_SERVER_ID;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_START_DATETIME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_TABLE_NAME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_TYPE;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
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
    private List<WeekViewEvent> events = new ArrayList<>();

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
    private LocalDateTime firstDayCurrentWeek;

    private DBAdapter mDBAdapter;
    private DBHelper mDBHelper;
    private BroadcastReceiver receiver;
    private Spinner mCalendartypesSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDBHelper = new DBHelper(AppContext.getAppContext());
        mDBAdapter = new DBAdapter(AppContext.getAppContext());
        mDBAdapter.open();

        firstDayCurrentWeek = new LocalDateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withDayOfWeek(DateTimeConstants.MONDAY);
        currentWeek = firstDayCurrentWeek.getWeekOfWeekyear();

    }

    @Override
    public void onResume() {
        super.onResume();
        LOGI(TAG, "RegisterRecNewsFeed");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                listEventsForWeek(firstDayCurrentWeek);
            }
        };
        LocalBroadcastManager.getInstance(CalendarFragment.this.getActivity()).registerReceiver(receiver, new IntentFilter(DBAdapter.CALENDAR_SQL_BROADCAST_INTENT));
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

        mCalendartypesSpinner = (Spinner) v.findViewById(R.id.calendar_fragment_types_spinner);
        mCalendartypesSpinner.setAdapter(new ArrayAdapter<>(CalendarFragment.this.getActivity(), R.layout.calendar_spinner_item, CalendarEventTypes.values()));

        Button previousWeekButton = (Button) v.findViewById(R.id.previous_week_button);
        previousWeekButton.setOnClickListener(this);
        Button nextWeekButton = (Button) v.findViewById(R.id.next_week_button);
        nextWeekButton.setOnClickListener(this);

        /////////////////////////////// RIGHT PANEL ////////////////////////////////////////////////

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

        /////////////////////////////// CALENDAR ///////////////////////////////////////////////////
        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEmptyViewClickListener(this);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listEventsForWeek(firstDayCurrentWeek);
    }

    private void listEventsForWeek(LocalDateTime startWeek) {

        String startTime = Long.toString(startWeek.toDateTime(DateTimeZone.UTC).getMillis() / 1000);
        LocalDateTime lastDayCurrentWeek = startWeek.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withDayOfWeek(DateTimeConstants.SUNDAY);
        String endTime = Long.toString(lastDayCurrentWeek.toDateTime(DateTimeZone.UTC).getMillis() / 1000);
        LOGD("TAG_listEventsForWeek", "startTime: " + startTime + " endTime: " + endTime);

        events.clear();
        String selectQueryEvents = "SELECT * FROM " + EVENT_CALENDAR_TABLE_NAME + " WHERE start_datetime <= " + endTime + " AND end_datetime >=" + startTime;
        Cursor cursorEvents = mDBHelper.getWritableDatabase().rawQuery(selectQueryEvents, null);
        for (cursorEvents.moveToFirst(); !cursorEvents.isAfterLast(); cursorEvents.moveToNext()) {
            long id = cursorEvents.getInt(cursorEvents.getColumnIndex(EVENT_CALENDAR_SERVER_ID));
            String name = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_NAME));
            String description = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_DESCRIPTION));
            String type = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_TYPE));
            String startDatetime = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_START_DATETIME)) + "000";
            String endDatetime = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_END_DATETIME)) + "000";
            String location = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_LOCATION));
            LOGD("TAG_listEventsForWeek", "startDatetime: " + startDatetime + " endDatetime: " + endDatetime);
            Calendar startTimeCalendar = Calendar.getInstance();
            startTimeCalendar.setTimeInMillis(Long.parseLong(startDatetime));
            Calendar endTimeCalendar = Calendar.getInstance();
            endTimeCalendar.setTimeInMillis(Long.parseLong(endDatetime));
            LOGD("TAG_listEventsForWeek", "startTimeCalendar: " + startTimeCalendar.toString() + " endTimeCalendar: " + endTimeCalendar.toString());
            WeekViewEvent eventElement = new WeekViewEvent(id, name, startTimeCalendar, endTimeCalendar);
            LOGD("TAG_listEventsForWeek", "eventElement: " + eventElement.toString());
            events.add(eventElement);
            mWeekView.notifyDatasetChanged();
        }
        cursorEvents.close();
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
        Animation mFragmentSliderFadeIn = AnimationUtils.loadAnimation(AppContext.getAppContext(), R.anim.fragment_item_slide_fade_in);
        panelAddEvent.setVisibility(View.VISIBLE);
        panelAddEvent.startAnimation(mFragmentSliderFadeIn);
        String stTime = String.format("%02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
        tvStartTimeEvent.setText(stTime);
        tvDurationEvent.setText("00:00");
        etNewEventName.setText("");
        etNewEventLocation.setText("");
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        LOGD("TAG", "List<WeekViewEvent>");
        return events;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previous_week_button:
                firstDayCurrentWeek = firstDayCurrentWeek.minusDays(Calendar.DAY_OF_WEEK);
                LOGD("TAG_", "firstDayCurrentWeek: " + firstDayCurrentWeek);
                mWeekView.goToDate(firstDayCurrentWeek.toDateTime(DateTimeZone.UTC).toGregorianCalendar());
                selectWeekTextView.setText(Integer.toString(--currentWeek) + getResources().getString(R.string.week));
                listEventsForWeek(firstDayCurrentWeek);
                ((MainActivity) getActivity()).startHttpIntent(MainActivity.getEventCalendarQuery(firstDayCurrentWeek), HttpIntentService.CALENDAR_PART);
                break;
            case R.id.next_week_button:
                firstDayCurrentWeek = firstDayCurrentWeek.plusDays(Calendar.DAY_OF_WEEK);
                LOGD("TAG_", "firstDayCurrentWeek: " + firstDayCurrentWeek);
                mWeekView.goToDate(firstDayCurrentWeek.toDateTime(DateTimeZone.UTC).toGregorianCalendar());
                selectWeekTextView.setText(Integer.toString(++currentWeek) + getResources().getString(R.string.week));
                listEventsForWeek(firstDayCurrentWeek);
                ((MainActivity) getActivity()).startHttpIntent(MainActivity.getEventCalendarQuery(firstDayCurrentWeek), HttpIntentService.CALENDAR_PART);
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
                panelAddEvent.setVisibility(View.GONE);

                Calendar endTimeEvent = (Calendar) startTimeEvent.clone();
                int minute = durationEventMin % 60;
                int hour = (durationEventMin - minute) / 60;
                endTimeEvent.add(Calendar.HOUR, hour);
                endTimeEvent.add(Calendar.MINUTE, minute);
                String eventName = etNewEventName.getText().toString();
                String eventLocation = etNewEventLocation.getText().toString();

                CalendarAddEventQuery calendarAddEventsQuery = new CalendarAddEventQuery();
                calendarAddEventsQuery.params.name = eventName;
                calendarAddEventsQuery.params.description = "description";
                calendarAddEventsQuery.params.type = mCalendartypesSpinner.getSelectedItem().toString();
                calendarAddEventsQuery.params.location = eventLocation;
                calendarAddEventsQuery.params.start_date_time = String.valueOf(startTimeEvent.getTimeInMillis()/1000);
                calendarAddEventsQuery.params.end_date_time = String.valueOf(endTimeEvent.getTimeInMillis()/1000);
                ((MainActivity) CalendarFragment.this.getActivity()).startHttpIntent(calendarAddEventsQuery, HttpIntentService.CALENDAR_ADD_EVENT);
                break;
        }

    }

    private enum CalendarEventTypes {
        REMINDER("reminder"),
        WEBINAR("webinar");

        private final String value;

        private CalendarEventTypes(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(CalendarFragment.this.getActivity()).unregisterReceiver(receiver);
        LOGI(TAG, "unregisterRecNewsFeed");
        mDBAdapter.close();
    }
}

