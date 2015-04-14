package me.justup.upme.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import me.justup.upme.MainActivity;
import me.justup.upme.R;
import me.justup.upme.db.DBAdapter;
import me.justup.upme.db.DBHelper;
import me.justup.upme.dialogs.ChooseReferralDialog;
import me.justup.upme.entity.CalendarAddEventQuery;
import me.justup.upme.entity.CalendarRemoveEventQuery;
import me.justup.upme.entity.CalendarUpdateEventQuery;
import me.justup.upme.entity.PersonBriefcaseEntity;
import me.justup.upme.http.HttpIntentService;
import me.justup.upme.utils.AppPreferences;
import me.justup.upme.utils.CommonUtils;
import me.justup.upme.weekview.WeekView;
import me.justup.upme.weekview.WeekViewEvent;

import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_DESCRIPTION;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_END_DATETIME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_LOCATION;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_NAME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_OWNER_ID;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_SERVER_ID;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_SHARED_WITH;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_START_DATETIME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_TABLE_NAME;
import static me.justup.upme.db.DBHelper.EVENT_CALENDAR_TYPE;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_IMG;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_NAME;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_PARENT_ID;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_SERVER_ID;
import static me.justup.upme.db.DBHelper.MAIL_CONTACT_TABLE_NAME;
import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.makeLogTag;


public class CalendarFragment extends Fragment implements View.OnClickListener, WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EmptyViewClickListener {
    private static final String TAG = makeLogTag(CalendarFragment.class);

    private static final int START_TIME_EVENT = 1;
    private static final int DURATION_EVENT = 2;
    private static final int REMIND_EVENT = 3;

    private WeekView mWeekView;
    private Dialog dialogSteTimeCalendar;
    private Dialog dialogInfoEvent;
    private List<WeekViewEvent> events = new ArrayList<>();

    private RelativeLayout panelAddEvent;
    private LinearLayout chooseDateLayout;
    private TextView startDateEvent;
    private TextView tvStartTimeEvent;
    private TextView tvDurationEvent;
    private TextView tvRemindEvent;
    private EditText etNewEventName;
    private EditText etNewEventLocation;
    private EditText etNewEventDescription;

    private Calendar startTimeEvent;
    private int durationEventMin;
    private int remindEventMin;

    private TextView selectWeekTextView;
    private TextView selectMonthTextView;

    private final DateTime currentDate = new DateTime();
    private int currentWeek;
    public static LocalDateTime firstDayCurrentWeek;

    // private DBAdapter mDBAdapter;
    // private DBHelper mDBHelper;
    private BroadcastReceiver receiver;
    private Spinner mCalendartypesSpinner;
    private SQLiteDatabase database;
    private Button addNewEventButton;

    private List<PersonBriefcaseEntity> listPerson;

    private static String[] months = new String[]{"ЯНВАРЬ", "ФЕВРАЛЬ", "МАРТ", "АПРЕЛЬ", "МАЙ", "ИЮНЬ", "ИЮЛЬ", "АВГУСТ", "СЕНТЯБРЬ", "ОКТЯБРЬ", "НОЯБРЬ", "ДЕКАБРЬ"};

    private boolean isEventNeedUpdate = false;
    private long currentEventId;

    private AppPreferences mAppPreferences = null; // Never call getContext here. Make produce NullPointerException!
    private int mCurrentUserId;

    private ArrayList<Integer> listSharedId = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mAppPreferences = new AppPreferences(getActivity().getApplicationContext());
        mCurrentUserId = mAppPreferences.getUserId();

        if (database != null) {
            if (!database.isOpen()) {
                database = DBAdapter.getInstance().openDatabase();
            }
        } else {
            database = DBAdapter.getInstance().openDatabase();
        }

        String selectQuery = "SELECT * FROM " + MAIL_CONTACT_TABLE_NAME;
        listPerson = fillPersonsFromCursor(database.rawQuery(selectQuery, null));
        firstDayCurrentWeek = new LocalDateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withDayOfWeek(DateTimeConstants.MONDAY);
        currentWeek = firstDayCurrentWeek.getWeekOfWeekyear();
    }

    private List<PersonBriefcaseEntity> fillPersonsFromCursor(Cursor cursorPersons) {
        ArrayList<PersonBriefcaseEntity> personsList = new ArrayList<>();
        for (cursorPersons.moveToFirst(); !cursorPersons.isAfterLast(); cursorPersons.moveToNext()) {
            PersonBriefcaseEntity personBriefcaseEntity = new PersonBriefcaseEntity();
            personBriefcaseEntity.setId(cursorPersons.getInt(cursorPersons.getColumnIndex(MAIL_CONTACT_SERVER_ID)));
            personBriefcaseEntity.setParentId(cursorPersons.getInt(cursorPersons.getColumnIndex(MAIL_CONTACT_PARENT_ID)));
            personBriefcaseEntity.setName(cursorPersons.getString(cursorPersons.getColumnIndex(MAIL_CONTACT_NAME)));
            personBriefcaseEntity.setPhoto(cursorPersons.getString(cursorPersons.getColumnIndex(MAIL_CONTACT_IMG)));
            personsList.add(personBriefcaseEntity);
        }

        cursorPersons.close();
        return personsList;
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                listEventsForWeek(firstDayCurrentWeek);
            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter(DBAdapter.CALENDAR_SQL_BROADCAST_INTENT));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        mWeekView = (WeekView) v.findViewById(R.id.weekView);

        TextView currentDateTextView = (TextView) v.findViewById(R.id.current_date_textView);
        currentDateTextView.setText(currentDate.toString("d MMMM yyyy", new Locale("ru")));
        selectMonthTextView = (TextView) v.findViewById(R.id.select_month_textView);
        String strMonthYear = String.format("%s %d", months[currentDate.getMonthOfYear()], currentDate.getYear());
        selectMonthTextView.setText(strMonthYear);
        selectWeekTextView = (TextView) v.findViewById(R.id.select_week_textView);
        selectWeekTextView.setText(Integer.toString(currentWeek) + getResources().getString(R.string.week));

        final Button chooseReferralButton = (Button) v.findViewById(R.id.choose_referral_button);
        chooseReferralButton.setOnClickListener(this);
        mCalendartypesSpinner = (Spinner) v.findViewById(R.id.calendar_fragment_types_spinner);
        mCalendartypesSpinner.setAdapter(new ArrayAdapter<>(CalendarFragment.this.getActivity(), R.layout.calendar_spinner_item, CalendarEventTypes.values()));
        mCalendartypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (CalendarEventTypes.values()[position]) {
                    case REMINDER:
                        chooseReferralButton.setVisibility(View.GONE);
                        listSharedId.clear();
                        break;
                    case TASK:
                        chooseReferralButton.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button previousWeekButton = (Button) v.findViewById(R.id.previous_week_button);
        previousWeekButton.setOnClickListener(this);
        Button nextWeekButton = (Button) v.findViewById(R.id.next_week_button);
        nextWeekButton.setOnClickListener(this);
        Button addButton = (Button) v.findViewById(R.id.add_button);
        addButton.setOnClickListener(this);

        /////////////////////////////// RIGHT PANEL ////////////////////////////////////////////////

        panelAddEvent = (RelativeLayout) v.findViewById(R.id.panel_add_event);

        Button calendarItemCloseButton = (Button) v.findViewById(R.id.calendar_item_close_button);
        calendarItemCloseButton.setOnClickListener(this);

        chooseDateLayout = (LinearLayout) v.findViewById(R.id.choose_date_layout);
        startDateEvent = (TextView) v.findViewById(R.id.start_date_event);
        startDateEvent.setText(currentDate.toString("dd/MM/yyyy"));
        startDateEvent.setOnClickListener(this);
        tvStartTimeEvent = (TextView) v.findViewById(R.id.start_time_event);
        tvStartTimeEvent.setOnClickListener(this);
        tvDurationEvent = (TextView) v.findViewById(R.id.duration_event);
        tvDurationEvent.setOnClickListener(this);
        tvRemindEvent = (TextView) v.findViewById(R.id.remind_event);
        tvRemindEvent.setOnClickListener(this);
        etNewEventName = (EditText) v.findViewById(R.id.new_event_name);
        etNewEventLocation = (EditText) v.findViewById(R.id.new_event_location);
        etNewEventDescription = (EditText) v.findViewById(R.id.new_event_description);
        addNewEventButton = (Button) v.findViewById(R.id.add_new_event_button);
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
        firstDayCurrentWeek = new LocalDateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withDayOfWeek(DateTimeConstants.MONDAY);
        currentWeek = firstDayCurrentWeek.getWeekOfWeekyear();
        listEventsForWeek(firstDayCurrentWeek);
    }

    private void listEventsForWeek(LocalDateTime startWeek) {
        String startTime = Long.toString(startWeek.toDateTime(DateTimeZone.UTC).getMillis() / 1000);
        LocalDateTime lastDayCurrentWeek = startWeek.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59).withDayOfWeek(DateTimeConstants.SUNDAY);
        String endTime = Long.toString(lastDayCurrentWeek.toDateTime(DateTimeZone.UTC).getMillis() / 1000);

        events.clear();

        String selectQueryEvents = "SELECT * FROM " + EVENT_CALENDAR_TABLE_NAME + " WHERE start_datetime <= " + endTime + " AND end_datetime >=" + startTime;
        Cursor cursorEvents = database.rawQuery(selectQueryEvents, null);
        for (cursorEvents.moveToFirst(); !cursorEvents.isAfterLast(); cursorEvents.moveToNext()) {
            long id = cursorEvents.getInt(cursorEvents.getColumnIndex(EVENT_CALENDAR_SERVER_ID));
            String name = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_NAME));
            String description = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_DESCRIPTION));
            String type = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_TYPE));
            String ownerId = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_OWNER_ID));
            String startDatetime = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_START_DATETIME)) + "000";
            String endDatetime = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_END_DATETIME)) + "000";
            String location = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_LOCATION));
            String sharedWith = cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_CALENDAR_SHARED_WITH));

            Calendar startTimeCalendar = Calendar.getInstance();
            startTimeCalendar.setTimeInMillis(Long.parseLong(startDatetime));
            Calendar endTimeCalendar = Calendar.getInstance();
            endTimeCalendar.setTimeInMillis(Long.parseLong(endDatetime));

//            WeekViewEvent eventElement = new WeekViewEvent(id, name, startTimeCalendar, endTimeCalendar);
            WeekViewEvent eventElement = new WeekViewEvent(id, name, description, type, Integer.parseInt(ownerId), startTimeCalendar, endTimeCalendar, location, sharedWith);
            events.add(eventElement);
        }
        mWeekView.notifyDatasetChanged();
        cursorEvents.close();
    }

    /////////////////////////////// DIALOG ///////////////////////////////

    public void TimePickerDialog(final int typeDialog) {
        dialogSteTimeCalendar = new Dialog(getActivity());
        dialogSteTimeCalendar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSteTimeCalendar.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogSteTimeCalendar.setContentView(R.layout.dialog_time_picker);
        Button buttonOk = (Button) dialogSteTimeCalendar.findViewById(R.id.buttonDialogCalendarTimeOk);
        final TextView textTitleDialogTime = (TextView) dialogSteTimeCalendar.findViewById(R.id.textTitleDialogTime);
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
            textTitleDialogTime.setText("Установите начальное время события");
            numberPickerHours.setValue(startTimeEvent.get(Calendar.HOUR_OF_DAY));
            numberPickerMinutes.setValue(startTimeEvent.get(Calendar.MINUTE));
        } else if (typeDialog == DURATION_EVENT) {
            textTitleDialogTime.setText("Установите продолжительность события");
            int minute = durationEventMin % 60;
            int hour = (durationEventMin - minute) / 60;
            numberPickerHours.setValue(hour);
            numberPickerMinutes.setValue(minute);
        } else if (typeDialog == REMIND_EVENT) {
            textTitleDialogTime.setText("Установите интервал");
            int minute = remindEventMin % 60;
            int hour = (remindEventMin - minute) / 60;
            numberPickerHours.setValue(hour);
            numberPickerMinutes.setValue(minute);
        }
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String convertedTime = convertTimeToString(numberPickerHours.getValue(), numberPickerMinutes.getValue());
                if (typeDialog == START_TIME_EVENT) {
                    startTimeEvent.set(Calendar.HOUR_OF_DAY, numberPickerHours.getValue());
                    startTimeEvent.set(Calendar.MINUTE, numberPickerMinutes.getValue());
                    tvStartTimeEvent.setText(convertedTime);
                    dialogSteTimeCalendar.dismiss();
                } else if (typeDialog == DURATION_EVENT) {
                    durationEventMin = numberPickerHours.getValue() * 60 + numberPickerMinutes.getValue();
                    tvDurationEvent.setText(convertedTime);
                    dialogSteTimeCalendar.dismiss();
                } else if (typeDialog == REMIND_EVENT) {
                    remindEventMin = numberPickerHours.getValue() * 60 + numberPickerMinutes.getValue();
                    tvRemindEvent.setText(convertedTime);
                    dialogSteTimeCalendar.dismiss();
                }
            }
        });
        dialogSteTimeCalendar.show();
    }

    public void InfoDialog(final WeekViewEvent event) {
        dialogInfoEvent = new Dialog(getActivity());
        dialogInfoEvent.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogInfoEvent.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogInfoEvent.setContentView(R.layout.dialog_info_event);
        final TextView eventNameTextView = (TextView) dialogInfoEvent.findViewById(R.id.event_name_textView);
        eventNameTextView.setText(event.getName());

        final TextView eventDescriptionTextView = (TextView) dialogInfoEvent.findViewById(R.id.event_description_textView);
        eventDescriptionTextView.setText(event.getDescription());


        String textOwner = "";
        if (mCurrentUserId == event.getOwnerId()) {
            textOwner = "мое событие";

            String nameSharedWith = "";
            String[] array = event.getmSharedWith().split(",");
            Set<String> mySet = new HashSet<>(Arrays.asList(array));
            for (PersonBriefcaseEntity itemPerson : listPerson) {
                if (mySet.contains(String.valueOf(itemPerson.getId())))
                    nameSharedWith += itemPerson.getName() + " ";
            }
            final TextView eventSharedwithTextView = (TextView) dialogInfoEvent.findViewById(R.id.event_sharedwith_textView);
            eventSharedwithTextView.setVisibility(View.VISIBLE);
            eventSharedwithTextView.setText("Расшаренно: \n" + nameSharedWith);
        } else
            for (PersonBriefcaseEntity itemPerson : listPerson)
                if (itemPerson.getId() == mCurrentUserId) {
                    textOwner = "событие назначенно " + itemPerson.getName();
                    break;
                }
        final TextView eventOwnerTextView = (TextView) dialogInfoEvent.findViewById(R.id.event_owner_textView);
        eventOwnerTextView.setText("Владелец: " + textOwner);

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", new Locale("ru"));
        String strStartTime = sdf.format(event.getStartTime().getTime());
        String strEndTime = sdf.format(event.getEndTime().getTime());


        final TextView eventTimeStartEndTextView = (TextView) dialogInfoEvent.findViewById(R.id.event_time_start_end_textView);
        eventTimeStartEndTextView.setText(strStartTime + " - " + strEndTime);

        final TextView eventLocationTextView = (TextView) dialogInfoEvent.findViewById(R.id.event_location_textView);
        eventLocationTextView.setText("Место события: " + event.getLocation());

        final Button cancelButton = (Button) dialogInfoEvent.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInfoEvent.dismiss();
            }
        });
        final Button editButton = (Button) dialogInfoEvent.findViewById(R.id.edit_button);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEventNeedUpdate = true;
                //if (!isEventNeedUpdate) {
                addNewEventButton.setText("Изменить");
                currentEventId = event.getmId();
                Calendar time = event.getStartTime();
                startTimeEvent = time;

                Animation mFragmentSliderFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fragment_item_slide_fade_in);
                panelAddEvent.setVisibility(View.VISIBLE);
                panelAddEvent.startAnimation(mFragmentSliderFadeIn);
                chooseDateLayout.setVisibility(View.VISIBLE);

                startDateEvent.setText(String.format("%02d/%02d/%d", time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.MONTH) + 1, time.get(Calendar.YEAR)));
                tvStartTimeEvent.setText(String.format("%02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE)));

                durationEventMin = (int) (event.getEndTime().getTimeInMillis() - time.getTimeInMillis()) / (1000 * 60);
                int minute = durationEventMin % 60;
                int hour = (durationEventMin - minute) / 60;
                tvDurationEvent.setText(convertTimeToString(hour, minute));

                etNewEventName.setText(event.getName());
                etNewEventDescription.setText(event.getDescription());
                etNewEventLocation.setText(event.getLocation());

                listSharedId.clear();
                if (event.getmSharedWith().equals(""))
                    mCalendartypesSpinner.setSelection(0);
                else {
                    mCalendartypesSpinner.setSelection(1);
                    String[] array = event.getmSharedWith().split(",");
                    for (String i : array)
                        listSharedId.add(Integer.parseInt(i));
                }
                dialogInfoEvent.dismiss();
            }
        });
        final Button deleteButton = (Button) dialogInfoEvent.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteConfirmation(event.getmId());

            }
        });

        if (event.getOwnerId() != mCurrentUserId) {
            deleteButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
        }
        dialogInfoEvent.show();
    }


    public void DeleteConfirmation(final long id) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Удалить событие ?");
        //ad.setMessage("сообщение");
        ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //Toast.makeText(getActivity(), "Удалено", Toast.LENGTH_LONG).show();
                CalendarRemoveEventQuery calendarRemoveEventQuery = new CalendarRemoveEventQuery();
                calendarRemoveEventQuery.params.id = Long.toString(id);
                database.delete(DBHelper.EVENT_CALENDAR_TABLE_NAME, "server_id = ?", new String[]{Long.toString(id)});
                ((MainActivity) getActivity()).startHttpIntent(calendarRemoveEventQuery, HttpIntentService.CALENDAR_REMOVE_EVENT);
                dialogInfoEvent.cancel();
            }
        });
        ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                //Toast.makeText(getActivity(), "Отмена удаления", Toast.LENGTH_LONG).show();
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                // ничего не выбрано
            }
        });
        ad.show();
    }

    public void DatePickerDialog(String strDate) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_date_picker, null, false);
        final DatePicker myDatePicker = (DatePicker) view.findViewById(R.id.myDatePicker);
        myDatePicker.setCalendarViewShown(true);
        Calendar chooseDate = convertStringToDate(strDate);
        myDatePicker.updateDate(chooseDate.get(Calendar.YEAR), chooseDate.get(Calendar.MONTH), chooseDate.get(Calendar.DAY_OF_MONTH));
        new AlertDialog.Builder(getActivity()).setView(view)
                .setTitle("Выберите дату")
                .setPositiveButton("Установить дату", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        int day = myDatePicker.getDayOfMonth();
                        int month = myDatePicker.getMonth();
                        int year = myDatePicker.getYear();
                        startTimeEvent.set(Calendar.DAY_OF_MONTH, day);
                        startTimeEvent.set(Calendar.MONTH, month);
                        startTimeEvent.set(Calendar.YEAR, year);
                        startDateEvent.setText(String.format("%02d/%02d/%d", day, month + 1, year));
                        dialog.cancel();
                    }
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static String convertTimeToString(int hours, int minutes) {
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes);
    }

    public static Calendar convertStringToDate(String strDate) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("ru"));
        try {
            cal.setTime(sdf.parse(strDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal;
    }


    //////////////////////////////////////////////////////////////////

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        InfoDialog(event);
    }

    @Override
    public void onEmptyViewClicked(Calendar time) {
        chooseDateLayout.setVisibility(View.GONE);
        CreateLayoutForNewEvent(time);
    }

    private void CreateLayoutForNewEvent(Calendar time) {
        isEventNeedUpdate = false;
        addNewEventButton.setText("Добавить");
        startTimeEvent = time;

        Animation mFragmentSliderFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fragment_item_slide_fade_in);
        panelAddEvent.setVisibility(View.VISIBLE);
        panelAddEvent.startAnimation(mFragmentSliderFadeIn);

        startDateEvent.setText(String.format("%02d/%02d/%d", time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.MONTH) + 1, time.get(Calendar.YEAR)));
        tvStartTimeEvent.setText(String.format("%02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE)));

        durationEventMin = 30;
        tvDurationEvent.setText("00:30");

        remindEventMin = 0;
        tvRemindEvent.setText("00:00");

        etNewEventName.setText("");
        etNewEventDescription.setText("");
        etNewEventLocation.setText("");

        mCalendartypesSpinner.setSelection(0);
        listSharedId.clear();
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        LOGD("TAG", "List<WeekViewEvent>");
        return events;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_button:
                chooseDateLayout.setVisibility(View.VISIBLE);
                CreateLayoutForNewEvent(Calendar.getInstance());
                break;
            case R.id.previous_week_button:
                firstDayCurrentWeek = firstDayCurrentWeek.minusDays(Calendar.DAY_OF_WEEK);
                LOGD("TAG1", "------ previous_week_button " + firstDayCurrentWeek.toString());
                mWeekView.goToDate(firstDayCurrentWeek.toDateTime(DateTimeZone.UTC).toGregorianCalendar());
                selectWeekTextView.setText(Integer.toString(currentWeek == 1 ? currentWeek = 52 : --currentWeek) + getResources().getString(R.string.week));
                String strMonthYearPrev = String.format("%s %d", months[firstDayCurrentWeek.getMonthOfYear() - 1], firstDayCurrentWeek.getYear());
                selectMonthTextView.setText(strMonthYearPrev);
                listEventsForWeek(firstDayCurrentWeek);
                ((MainActivity) getActivity()).startHttpIntent(MainActivity.getEventCalendarQuery(firstDayCurrentWeek), HttpIntentService.CALENDAR_PART);
                break;
            case R.id.next_week_button:
                firstDayCurrentWeek = firstDayCurrentWeek.plusDays(Calendar.DAY_OF_WEEK);
                LOGD("TAG1", "------ previous_week_button " + firstDayCurrentWeek.toString());
                mWeekView.goToDate(firstDayCurrentWeek.toDateTime(DateTimeZone.UTC).toGregorianCalendar());
                selectWeekTextView.setText(Integer.toString(currentWeek == 52 ? currentWeek = 1 : ++currentWeek) + getResources().getString(R.string.week));
                String strMonthYearNext = String.format("%s %d", months[firstDayCurrentWeek.getMonthOfYear() - 1], firstDayCurrentWeek.getYear());
                selectMonthTextView.setText(strMonthYearNext);
                listEventsForWeek(firstDayCurrentWeek);
                ((MainActivity) getActivity()).startHttpIntent(MainActivity.getEventCalendarQuery(firstDayCurrentWeek), HttpIntentService.CALENDAR_PART);
                break;
            case R.id.calendar_item_close_button:
                panelAddEvent.setVisibility(View.GONE);
                break;
            case R.id.start_date_event:
                DatePickerDialog(startDateEvent.getText().toString());
                break;
            case R.id.start_time_event:
                TimePickerDialog(START_TIME_EVENT);
                break;
            case R.id.duration_event:
                TimePickerDialog(DURATION_EVENT);
                break;
            case R.id.remind_event:
                TimePickerDialog(REMIND_EVENT);
                break;
            case R.id.choose_referral_button:
                ChooseReferralDialog chooseReferralDialog = ChooseReferralDialog.newInstance(listSharedId);
                chooseReferralDialog.show(getChildFragmentManager(), "choose_referral_dialog");
                break;
            case R.id.add_new_event_button:
                if (startTimeEvent.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
                    Toast.makeText(getActivity(), "Событие не может быть установлено в прошлом", Toast.LENGTH_SHORT).show();
                    break;
                }

                Calendar endTimeEvent = (Calendar) startTimeEvent.clone();
                String eventName = etNewEventName.getText().toString();
                String eventDescription = etNewEventDescription.getText().toString();
                String eventLocation = etNewEventLocation.getText().toString();
                int minute = durationEventMin % 60;
                int hour = (durationEventMin - minute) / 60;
                if (hour == 0 && minute == 0) {
                    Toast.makeText(getActivity(), "Не установленна продолжительность события", Toast.LENGTH_SHORT).show();
                    break;
                }

                endTimeEvent.add(Calendar.HOUR_OF_DAY, hour);
                endTimeEvent.add(Calendar.MINUTE, minute);
                if (!isEventNeedUpdate) {
                    addNewEventButton.setText("Добавить");
                    CalendarAddEventQuery calendarAddEventsQuery = new CalendarAddEventQuery();
                    calendarAddEventsQuery.params.name = CommonUtils.convertToUTF8(eventName);
                    calendarAddEventsQuery.params.description = CommonUtils.convertToUTF8(eventDescription);
                    calendarAddEventsQuery.params.type = mCalendartypesSpinner.getSelectedItem().toString();
                    calendarAddEventsQuery.params.location = CommonUtils.convertToUTF8(eventLocation);
                    calendarAddEventsQuery.params.start = String.valueOf(startTimeEvent.getTimeInMillis() / 1000);
                    calendarAddEventsQuery.params.end = String.valueOf(endTimeEvent.getTimeInMillis() / 1000);
                    // !!! calendarAddEventsQuery.params.duration = durationEventMin;
                    if (listSharedId.size() == 0)
                        calendarAddEventsQuery.params.shared_with = "";
                    else {
                        String sharedWith = listSharedId.toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");
                        calendarAddEventsQuery.params.shared_with = sharedWith;
                    }

                    ((MainActivity) getActivity()).startHttpIntent(calendarAddEventsQuery, HttpIntentService.CALENDAR_ADD_EVENT);
                    panelAddEvent.setVisibility(View.GONE);

                } else {
                    addNewEventButton.setText("Обновить");

                    CalendarUpdateEventQuery calendarUpdateEventQuery = new CalendarUpdateEventQuery();
                    calendarUpdateEventQuery.params.id = currentEventId;
                    calendarUpdateEventQuery.params.name = CommonUtils.convertToUTF8(eventName);
                    calendarUpdateEventQuery.params.description = CommonUtils.convertToUTF8(eventDescription);
                    calendarUpdateEventQuery.params.type = mCalendartypesSpinner.getSelectedItem().toString();
                    calendarUpdateEventQuery.params.location = CommonUtils.convertToUTF8(eventLocation);
                    calendarUpdateEventQuery.params.start = String.valueOf(startTimeEvent.getTimeInMillis() / 1000);
                    calendarUpdateEventQuery.params.end = String.valueOf(endTimeEvent.getTimeInMillis() / 1000);
                    calendarUpdateEventQuery.params.shared_with = listSharedId.toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");
                    LOGE("pavel", calendarUpdateEventQuery.toString());
                    ((MainActivity) getActivity()).startHttpIntent(calendarUpdateEventQuery, HttpIntentService.CALENDAR_UPDATE_EVENT);
                    isEventNeedUpdate = false;
                    panelAddEvent.setVisibility(View.GONE);
                }


                break;
        }
    }

    private enum CalendarEventTypes {
        REMINDER("reminder"),
        TASK("task");

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
    }

    @Override
    public void onDestroy() {
        DBAdapter.getInstance().closeDatabase();
        super.onDestroy();
    }

    public void setPersonIdForNewEvent(ArrayList<Integer> listId) {
        listSharedId = listId;
    }
}

