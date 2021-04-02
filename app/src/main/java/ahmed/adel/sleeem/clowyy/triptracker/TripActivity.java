package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDao;
import ahmed.adel.sleeem.clowyy.triptracker.database.model.TripDatabase;
import ahmed.adel.sleeem.clowyy.triptracker.fragments.DatePickerFragment;
import ahmed.adel.sleeem.clowyy.triptracker.fragments.TimePickerFragment;
import ahmed.adel.sleeem.clowyy.triptracker.helpers.TripExtraInfo;
import ahmed.adel.sleeem.clowyy.triptracker.service.MyService;
import ahmed.adel.sleeem.clowyy.triptracker.service.MyWorker;

public class TripActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    EditText txtTripName, txtStartPoint, txtEndPoint, txtRepeatingNumber;
    RadioButton rbOneWay, rbRoundTrip;

    Button timeBtn, dateBtn,addNotesBtn;
    TextView tripType;

    Dialog roundTripDialog, notesDialog;

    private Calendar calendar;
    private String calDate = "";
    Spinner repeatingSpinner;
    Switch swtchRepeat;
    String repeatingType = "", repeatingTypeRound = "";

    private TripDao tripDao;
    ListView lvNotes;


    Trip tripRounded;

    int minute;
    int hourOfDay;
    private String timeTxt = "";

    Button btnAddTrip;

    List<String> tripNotes, roundTripNotes;
    ArrayAdapter<CharSequence> repeatingAdapter;

    private String calDaterounded = "";
    private String timeTxtrounded = "";
    private int type;
    private boolean isEdit = false;
    String tripID = null;
    Trip trip;
    private Calendar calendar2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        initView();

        calendar2 = Calendar.getInstance();

        roundTripDialog = new Dialog(this);
        notesDialog = new Dialog(this);

       // tripID =getIntent().getStringExtra("tripID");

        roundTripDialog.setContentView(R.layout.round_trip);
        notesDialog.setContentView(R.layout.notes_dialog);
        roundTripDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.corner_view));
        notesDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.corner_view));

        tripDao = TripDatabase.getInstance(getBaseContext()).getTripDao();

        tripNotes = new ArrayList<>();
        roundTripNotes = new ArrayList<>();


        calendar = Calendar.getInstance();

        isEdit = getIntent().getBooleanExtra("isEdit",false);



        if (isEdit) {
            tripID = getIntent().getStringExtra("tripID");
            trip = tripDao.selectTripById(tripID);
            txtTripName.setText(trip.getTripTitle());
            txtStartPoint.setText(trip.getTripSource());
            txtEndPoint.setText(trip.getTripDestination());
            tripType.setVisibility(View.GONE);
            rbOneWay.setVisibility(View.GONE);
            rbRoundTrip.setVisibility(View.GONE);
            btnAddTrip.setText(getString(R.string.editbtn));
            addNotesBtn.setText(getString(R.string.editnotesBtn));
            addNotesBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_editnote, 0, 0, 0);
            if (trip.getTripRepeatingType().equals("")) {
                swtchRepeat.setChecked(false);
            } else {
                swtchRepeat.setChecked(true);
                repeatingSpinner.setVisibility(View.VISIBLE);
            }

            tripNotes = excludeNotes(trip.getTripNotes());
            if (tripNotes == null) {
                tripNotes = new ArrayList<>();
            }
            timeTxt = trip.getTripTime();
            calDate = trip.getTripDate();


        }

        findViewById(R.id.btnTripAdding).setOnClickListener(v -> {

            if (calDate.length() > 0 && timeTxt.length() > 0 && txtStartPoint.getText().length() > 0 && txtEndPoint.getText().length() > 0
                    && txtTripName.getText().length() > 0) {


                if (isEdit) {
                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    stopService(intent);
                }

                // Toast.makeText(getBaseContext(), "clicked", Toast.LENGTH_SHORT).show();
                StringBuilder oneWaysNote = new StringBuilder("");
                StringBuilder roundNote = new StringBuilder("");

                for (String note : tripNotes) oneWaysNote.append("0" + note + "Ω");

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        GoogleMapsManager googleMapsManager = GoogleMapsManager.getInstance(getApplicationContext());

                        String imgURL = googleMapsManager.getLocationImageURL(txtEndPoint.getText().toString());
                        TripExtraInfo tripExtraInfo = googleMapsManager.getTripExtraInfo(txtStartPoint.getText().toString(), txtEndPoint.getText().toString());

                        if (tripExtraInfo == null) {
                            tripExtraInfo = new TripExtraInfo("N/A", "N/A");
                        }




                        trip = new Trip(UUID.randomUUID().toString(),txtStartPoint.getText().toString(), txtTripName.getText().toString(), txtEndPoint.getText().toString(),
                                rbRoundTrip.isChecked(), swtchRepeat.isChecked() ? repeatingType : "", oneWaysNote.toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                calDate, timeTxt, imgURL, false, tripExtraInfo.getDistance(), tripExtraInfo.getDuration(),
                                tripExtraInfo.getAvgSpeed());

                        if (tripID!=null)trip.setTripId(tripID);


                        if (isEdit) {
                            Log.i("Edit Trip", "run: "+isEdit);
                            tripDao.updateTrip(trip);
                        } else {
                            Log.i("insert Trip", "run: "+isEdit);
                            tripDao.insertTrip(trip);
                        }

                        Data inputData = new Data.Builder()
                                .putString("Title", trip.getTripTitle())
                                .putString("Source", trip.getTripSource())
                                .putString("Destination", trip.getTripDestination())
                                .putString("Date", trip.getTripId()).build();


                        Calendar calendarmsd = Calendar.getInstance();
                        long nowMillis = calendarmsd.getTimeInMillis();
                        long diff = calendar.getTimeInMillis() - nowMillis;


                        WorkRequest uploadWorkRequest =
                                new OneTimeWorkRequest.Builder(MyWorker.class)
                                        .setInputData(inputData)
                                        .setInitialDelay(diff, TimeUnit.MILLISECONDS)
                                        .build();
                        WorkManager.getInstance(getApplication()).enqueue(uploadWorkRequest);

                        if(tripRounded!=null){

                            tripDao.insertTrip(tripRounded);

                            Data inputData2 = new Data.Builder()
                                    .putString("Title", trip.getTripTitle())
                                    .putString("Source", trip.getTripSource())
                                    .putString("Destination", trip.getTripDestination())
                                    .putString("Date", trip.getTripId()).build();



                            Calendar calendarmsd2 = Calendar.getInstance();
                            long nowMillis2 = calendarmsd2.getTimeInMillis();
                            long diff2 = calendar2.getTimeInMillis() - nowMillis2;


                            WorkRequest uploadWorkRequest2 =
                                    new OneTimeWorkRequest.Builder(MyWorker.class)
                                            .setInputData(inputData2)
                                            .setInitialDelay(diff2, TimeUnit.MILLISECONDS)
                                            .build();
                            WorkManager.getInstance(getApplication()).enqueue(uploadWorkRequest2);

                        }




                        //finish();

                    }
                }).start();

                finish();
//                Intent intent = new Intent(this, MyService.class);
//                PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
//                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pintent);

//                Intent intent = new Intent(this, MyService.class);
//                PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
//                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                //alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pintent);
/*
                long rrr = 0;
                String Type= "";
                Log.i("TAGUU", "onCreate: TYPE "+repeatingType);
                switch (repeatingType) {
                    case "day1":
                        Type="day";
                        rrr = AlarmManager.INTERVAL_DAY;
                        break;
                    case "week1":
                        rrr = AlarmManager.INTERVAL_DAY * 7;
                        Type="week";
                        break;
                    case "hour1":
                        rrr = AlarmManager.INTERVAL_HOUR;
                        Type="hour";
                        break;
                    case "month1":
                        rrr = AlarmManager.INTERVAL_DAY * 30;
                        Type="month";
                        break;
                    default:
                        rrr = 0;
                }
                /*
 */
//                SystemClock.elapsedRealtime() + mCalendar.getTimeInMillis();


             /*
                }
                else{

                    TimeUnit timeUnit = TimeUnit.DAYS;
                    if (Type.equals("hour"))timeUnit=TimeUnit.HOURS;
                    else if (Type.equals("month"))timeUnit=TimeUnit.DAYS;

                    PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(MyWorker.class, Integer.parseInt(txtRepeatingNumber.getText().toString()),
                            timeUnit).setInputData(inputData)
                            .setInitialDelay(diff, TimeUnit.SECONDS)
                            .build();
                    WorkManager.getInstance(getApplication()).enqueue(periodicWork);

                }

              */



/*
                if (tripRounded != null) {
                    tripDao.insertTrip(tripRounded);

                    inputData = new Data.Builder()
                            .putString("data", tripRounded.getTripDate())
                            .build();

                    long rrr2 = 0;
                    switch (repeatingType) {
                        case "day1":
                            Type="day";
                            rrr2 = AlarmManager.INTERVAL_DAY;
                            break;
                        case "week1":
                            rrr2 = AlarmManager.INTERVAL_DAY * 7;
                            Type="week";
                            break;
                        case "hour1":
                            rrr2 = AlarmManager.INTERVAL_HOUR;
                            Type="hour";
                            break;
                        case "month1":
                            rrr2 = AlarmManager.INTERVAL_DAY * 30;
                            Type="month";
                            break;
                        default:
                            rrr2 = 0;
                    }

*/

//                if(!swtchRepeat.isChecked()) {
//                    calendarmsd = Calendar.getInstance();
//                    nowMillis = calendarmsd.getTimeInMillis();
//                    diff = calendar.getTimeInMillis() - nowMillis;
//
//                    uploadWorkRequest =
//                            new OneTimeWorkRequest.Builder(MyWorker.class)
//                                    .setInputData(inputData)
//                                    .setInitialDelay(diff, TimeUnit.SECONDS)
//                                    .build();
//                    WorkManager.getInstance(getApplication()).enqueue(uploadWorkRequest);
//                }
//                  else{
//
//                        TimeUnit timeUnit = TimeUnit.DAYS;
//                        if (Type.equals("hour"))timeUnit=TimeUnit.HOURS;
//                        else if (Type.equals("month"))timeUnit=TimeUnit.DAYS;
//
//                        PeriodicWorkRequest periodicWork = new PeriodicWorkRequest.Builder(MyWorker.class, Integer.parseInt(txtRepeatingNumber.getText().toString()),
//                                timeUnit).setInputData(inputData)
//                                .setInitialDelay(diff, TimeUnit.SECONDS)
//                                .build();
//                        WorkManager.getInstance(getApplication()).enqueue(periodicWork);
//
//                    }


                //}


                finish();

            } else
                Toast.makeText(getBaseContext(), getString(R.string.completeFieldsMSG), Toast.LENGTH_LONG).show();


        });


        timeBtn.setOnClickListener(v -> {

            DialogFragment timePickerFragment = new TimePickerFragment();
            timePickerFragment.show(getSupportFragmentManager(), "time Packer");
        });

        dateBtn.setOnClickListener(v -> {
            DialogFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.show(getSupportFragmentManager(), "date Picker");
        });

        rbRoundTrip.setOnClickListener(v -> {
            type = 5;
            showTripDialog();
        });


        txtStartPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(TripActivity.this);
                startActivityForResult(intent, 100);
            }
        });

        txtEndPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(TripActivity.this);
                startActivityForResult(intent, 110);
            }
        });


        addNotesBtn.setOnClickListener(v -> {
            showNotesDialog(tripNotes);
        });
    }

    private void showNotesDialog(List<String> notes) {
        lvNotes = notesDialog.findViewById(R.id.lvShowNotes);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);

        lvNotes.setAdapter(stringArrayAdapter);

        lvNotes.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(TripActivity.this);
            builder.setTitle(getString(R.string.deleteMSGtitle));
            builder.setMessage(getString(R.string.deleteMSG));

            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    notes.remove(position);
                    stringArrayAdapter.notifyDataSetChanged();
                }
            });

            builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        });

        EditText txtNote = notesDialog.findViewById(R.id.txtNote);
        notesDialog.findViewById(R.id.btnAddNote).setOnClickListener(v -> {
            if (txtNote.getText().toString().length() > 1) {
                notes.add(txtNote.getText().toString());
                stringArrayAdapter.notifyDataSetChanged();
                txtNote.setText("");
            }
        });

        notesDialog.findViewById(R.id.btnShowNotesOK).setOnClickListener(v -> {
            if (txtNote.getText().toString().length() > 1) {
                notes.add(txtNote.getText().toString());
                txtNote.setText("");
            }
            notesDialog.dismiss();
        });

        notesDialog.show();

    }

    private void showDeleteDialog(List<String> notesList, int position){
        //initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        //set title
        builder.setTitle(getString(R.string.deleteMSGtitle));
        //set message
        builder.setMessage(getString(R.string.deleteMSG));
        //positive yes button
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                notesList.remove(position);
                lvNotes.getAdapter().notify();
            }
        });
        //negative no button
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showTripDialog() {
        EditText txtBackTripName, txtBackStartPoint, txtBackEndPoint, txtRepeatingNumberRound;
        Switch swtchRepeatingRound;
        Spinner repeatingSpinnerRound;


        txtBackTripName = roundTripDialog.findViewById(R.id.txtBackTripName);
        txtBackStartPoint = roundTripDialog.findViewById(R.id.txtBackStartPoint);
        txtBackEndPoint = roundTripDialog.findViewById(R.id.txtBackEndPoint);
        txtRepeatingNumberRound = roundTripDialog.findViewById(R.id.txtRepeatingNumberRound);
        swtchRepeatingRound = roundTripDialog.findViewById(R.id.swtchRepeatingRound);
        repeatingSpinnerRound = roundTripDialog.findViewById(R.id.repeatingSpinnerRound);

        txtBackTripName.setText(txtEndPoint.getText().toString());
        txtBackStartPoint.setText(txtEndPoint.getText().toString());
        txtBackEndPoint.setText(txtStartPoint.getText().toString());
        Button dateBtnrounded = roundTripDialog.findViewById(R.id.dateBtnRound);
        Button timeBtnRounded = roundTripDialog.findViewById(R.id.timerBtnRound);

        timeBtnRounded.setOnClickListener(v -> {

            DialogFragment timePickerFragment = new TimePickerFragment();
            timePickerFragment.show(getSupportFragmentManager(), "time Packer");
        });

        dateBtnrounded.setOnClickListener(v -> {
            DialogFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.show(getSupportFragmentManager(), "date Picker");
        });


        repeatingSpinnerRound.setAdapter(repeatingAdapter);

        repeatingSpinnerRound.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                repeatingTypeRound = Arrays.asList(getResources().getStringArray(R.array.repeating_array)).get(position).toLowerCase() + "1";
                // Toast.makeText(TripActivity.this, repeatingType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        swtchRepeatingRound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                txtRepeatingNumberRound.setVisibility(View.VISIBLE);
                repeatingSpinnerRound.setVisibility(View.VISIBLE);
            } else {
                txtRepeatingNumberRound.setVisibility(View.INVISIBLE);
                repeatingSpinnerRound.setVisibility(View.INVISIBLE);
            }
        });

        roundTripDialog.findViewById(R.id.btnDone).setOnClickListener(v -> {
            Toast.makeText(getBaseContext(), "done clicked", Toast.LENGTH_SHORT).show();

            if (calDaterounded.length() > 0 && timeTxtrounded.length() > 0 && txtBackTripName.getText().length() > 0 && txtBackStartPoint.getText().length() > 0
                    && txtBackEndPoint.getText().length() > 0) {

                Toast.makeText(getBaseContext(), "done clicked inside if", Toast.LENGTH_SHORT).show();
                StringBuilder roundNote = new StringBuilder("");
                for (String note : roundTripNotes) roundNote.append("0" + note + ",");



                new Thread(()-> {
                    GoogleMapsManager googleMapsManager = GoogleMapsManager.getInstance(getApplicationContext());

                    String imgURL = googleMapsManager.getLocationImageURL(txtEndPoint.getText().toString());
                    TripExtraInfo tripExtraInfo = googleMapsManager.getTripExtraInfo(txtStartPoint.getText().toString(), txtEndPoint.getText().toString());

                    if (tripExtraInfo == null) {
                        tripExtraInfo = new TripExtraInfo("N/A", "N/A");
                    }
                    tripRounded = new Trip(UUID.randomUUID().toString(),txtBackStartPoint.getText().toString(), txtBackTripName.getText().toString(), txtBackEndPoint.getText().toString(),
                            false, swtchRepeatingRound.isChecked() ? repeatingTypeRound : "", roundNote.toString(), FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            calDaterounded, timeTxtrounded, imgURL, false, tripExtraInfo.getDistance(), tripExtraInfo.getDuration(),
                            tripExtraInfo.getAvgSpeed());
                    type = 3;
                    roundTripDialog.dismiss();
                }).start();
            }


        });

        roundTripDialog.findViewById(R.id.btnAddRoundNotes).setOnClickListener(v -> {
            showNotesDialog(roundTripNotes);
        });

        roundTripDialog.findViewById(R.id.btnCancel).setOnClickListener(v -> {
            rbOneWay.setChecked(true);
            type = 3;
            roundTripDialog.dismiss();
        });


        roundTripDialog.findViewById(R.id.timerBtnRound).setOnClickListener(v -> {
            DialogFragment timePickerFragment = new TimePickerFragment();
            timePickerFragment.show(getSupportFragmentManager(), "time Packer");
        });

        roundTripDialog.findViewById(R.id.dateBtnRound).setOnClickListener(v -> {
            DialogFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.show(getSupportFragmentManager(), "date Picker");
        });

        roundTripDialog.setOnCancelListener(dialog -> {
            type = 3;
            rbOneWay.setChecked(true);
        });

        roundTripDialog.show();
    }

    private void initView() {
        txtTripName = findViewById(R.id.txtBackTripName);
        txtStartPoint = findViewById(R.id.txtBackStartPoint);
        txtEndPoint = findViewById(R.id.txtBackEndPoint);
        txtRepeatingNumber = findViewById(R.id.txtRepeatingNumber);
        swtchRepeat = findViewById(R.id.swtchRepeating);
        tripType = findViewById(R.id.tripTypeTxt);
        addNotesBtn = findViewById(R.id.btnAddNotes);

        repeatingSpinner = findViewById(R.id.repeatingSpinner);
        repeatingAdapter = ArrayAdapter.createFromResource(this, R.array.repeating_array, android.R.layout.simple_spinner_item);
        repeatingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeatingSpinner.setAdapter(repeatingAdapter);

        repeatingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                repeatingType = Arrays.asList(getResources().getStringArray(R.array.repeating_array)).get(position).toLowerCase() + "1";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        swtchRepeat.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                //type = 5;
                txtRepeatingNumber.setVisibility(View.VISIBLE);
                repeatingSpinner.setVisibility(View.VISIBLE);
            } else {
                //type = 3;
                txtRepeatingNumber.setVisibility(View.INVISIBLE);
                repeatingSpinner.setVisibility(View.INVISIBLE);
            }
        });

        btnAddTrip = findViewById(R.id.btnTripAdding);

        rbOneWay = findViewById(R.id.rbOneWay);
        rbRoundTrip = findViewById(R.id.rbRoundTrip);

        timeBtn = findViewById(R.id.timerBtn);
        dateBtn = findViewById(R.id.dateBtn);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        txtStartPoint.setFocusable(false);
        txtEndPoint.setFocusable(false);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        if (type != 5) {
            this.hourOfDay = hourOfDay;
            this.minute = minute;
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.SECOND, 0);
            timeTxt = String.valueOf(hourOfDay) + " : " + String.valueOf(minute);
        }
        else{

            calendar2.set(Calendar.MINUTE, minute);
            calendar2.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar2.set(Calendar.SECOND, 0);

            timeTxtrounded = String.valueOf(hourOfDay) + " : " + String.valueOf(minute);
        }

    }

    //handle date setting
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        String date = "";
        if(type!=5) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
             date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
            calDate = date;
        }
        else {

            calendar2.set(Calendar.YEAR, year);
            calendar2.set(Calendar.MONTH, month);
            calendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
           date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
            calDaterounded = date;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            txtStartPoint.setText(place.getAddress());
        } else if (requestCode == 110 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            txtEndPoint.setText(place.getAddress());
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    List<String> excludeNotes(String note) {
        if (note.equals("")) {
            return null;
        }
        String[] strings = note.split("Ω");
        List<String> result = new ArrayList<>();
        for (int indx = 0; indx < strings.length; ++indx) {
            result.add(strings[indx].substring(1));
        }

        return result;
    }
}