package me.justup.upme.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import me.justup.upme.R;
import me.justup.upme.weekview.WeekView;
import me.justup.upme.weekview.WeekViewEvent;

import static me.justup.upme.utils.LogUtils.makeLogTag;


public class CalendarFragment extends Fragment implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EmptyViewClickListener {
    private static final String TAG = makeLogTag(CalendarFragment.class);

    private WeekView mWeekView;
    private Dialog dialogSteTimeCalendar;
    List<WeekViewEvent> events;

    RelativeLayout panelAddEvent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        events = new ArrayList<>();

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 3);
        startTime.set(Calendar.MINUTE, 0);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR, 1);
        WeekViewEvent event = new WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_01));
        events.add(event);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        DateTime jodaDateTimeObject = new DateTime();
        TextView currentDateTextView = (TextView) v.findViewById(R.id.current_date_textView);
        currentDateTextView.setText(jodaDateTimeObject.toString("MMMM d, yyyy", new Locale("ru")));
        TextView selectMonthTextView = (TextView) v.findViewById(R.id.select_month_textView);
        selectMonthTextView.setText(jodaDateTimeObject.toString("MMMM yyyy", new Locale("ru")));
//        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
//        DateTime dt = formatter.parseDateTime(string);



        panelAddEvent = (RelativeLayout) v.findViewById(R.id.panel_add_event);
        Button calendarItemCloseButton = (Button) v.findViewById(R.id.calendar_item_close_button);
        calendarItemCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                panelAddEvent.setVisibility(View.GONE);
            }
        });

        TextView startTimeEvent = (TextView) v.findViewById(R.id.start_time_event);

        startTimeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNumberPickerAllVideoDialog(true);
            }
        });

        TextView durationEvent = (TextView) v.findViewById(R.id.duration_event);

        durationEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNumberPickerAllVideoDialog(true);
            }
        });

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) v.findViewById(R.id.weekView);
        mWeekView.setNumberOfVisibleDays(7);
        // Lets change some dimensions to best fit the view.
        mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);


        // Set long press listener for events.
        //mWeekView.setEventLongPressListener(this);

        mWeekView.setEmptyViewClickListener(this);

        return v;
    }




    //////////////// DIALOG ////////////////
    public void showNumberPickerAllVideoDialog(final boolean isInTextView) {
        dialogSteTimeCalendar = new Dialog(getActivity());
        dialogSteTimeCalendar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSteTimeCalendar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogSteTimeCalendar.setContentView(R.layout.dialog_time_picker);
        Button buttonOk = (Button) dialogSteTimeCalendar.findViewById(R.id.buttonDialogCalendarTimeOk);
        final NumberPicker numberPickerHours = (NumberPicker) dialogSteTimeCalendar.findViewById(R.id.numberPickerHours);
        final NumberPicker numberPickerMinutes = (NumberPicker) dialogSteTimeCalendar.findViewById(R.id.numberPickerMinutes);
        numberPickerHours.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerMinutes.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setNumberPickerTextColor(numberPickerHours, Color.WHITE);
        setNumberPickerTextColor(numberPickerMinutes, Color.WHITE);
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

        numberPickerHours.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
//                if (newValue == videoDurationHours && numberPickerMinutes.getValue() == videoDurationMinutes) {
//                    numberPickerSeconds.setMaxValue(videoDurationSecondsDuration);
//                } else {
//                    numberPickerSeconds.setMaxValue(videoDurationSeconds);
//                }
            }
        });

        numberPickerMinutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
//                if (newValue == videoDurationMinutes && numberPickerHours.getValue() == videoDurationHours) {
//                    numberPickerSeconds.setMaxValue(videoDurationSecondsDuration);
//                } else {
//                    numberPickerSeconds.setMaxValue(videoDurationSeconds);
//                }
            }
        });

//        if (isInTextView && textViewMiniFragCalendarInValue.length() > 1) {
//            int position = (int) Utils.convertBookmarkOffsetToLong(textViewMiniFragCalendarInValue.getText().toString());
//            int hours = (position / (1000 * 60 * 60));
//            int minutes = ((position % (1000 * 60 * 60)) / (1000 * 60));
//            int seconds = (((position % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
//            numberPickerHours.setValue(hours);
//            numberPickerMinutes.setValue(minutes);
//        }
//
//        if (!isInTextView && textViewMiniFragCalendarOutValue.length() > 1) {
//            int position = (int) Utils.convertBookmarkOffsetToLong(textViewMiniFragCalendarOutValue.getText().toString());
//            int hours = (position / (1000 * 60 * 60));
//            int minutes = ((position % (1000 * 60 * 60)) / (1000 * 60));
//            int seconds = (((position % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
//            numberPickerHours.setValue(hours);
//            numberPickerMinutes.setValue(minutes);
//        }

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String convertedTime = convertTimeToString(numberPickerHours.getValue(), numberPickerMinutes.getValue(), numberPickerSeconds.getValue());
                if (isInTextView) {

                    dialogSteTimeCalendar.dismiss();
                } else {

                    dialogSteTimeCalendar.dismiss();
                }
            }
        });
        dialogSteTimeCalendar.show();
    }

    public static String convertTimeToString(int hours, int minutes, int seconds) {
        StringBuffer buf = new StringBuffer();
        buf.append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));
        return buf.toString();
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
                    Log.w("setNumberPickerTextColor", e);
                }
            }
        }
        return false;
    }
    ///////////////////////////////////////



    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(getActivity(), "onEventClick: event.getName() - " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewClicked(Calendar time) {

        panelAddEvent.setVisibility(View.VISIBLE);


        //Toast.makeText(getActivity(), "onEmptyViewClicked: time - " + time, Toast.LENGTH_SHORT).show();
//        Toast.makeText(getActivity(), getEventTitle(time), Toast.LENGTH_SHORT).show();
//        Calendar startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, time.get(Calendar.DAY_OF_MONTH));
//        startTime.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
//        startTime.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
//        startTime.set(Calendar.MONTH, time.get(Calendar.MONTH));
//        startTime.set(Calendar.YEAR, time.get(Calendar.YEAR));
//        Calendar endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 1);
//        //endTime.set(Calendar.MONTH, time.get(Calendar.MONTH));
//        // endTime.set(Calendar.MINUTE, 0);
//        WeekViewEvent event = new WeekViewEvent(0, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_03));
//        events.add(event);
//        mWeekView.notifyDatasetChanged();


    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        return events;
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
