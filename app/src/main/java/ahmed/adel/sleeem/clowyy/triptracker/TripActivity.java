package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.database.model.Trip;
import ahmed.adel.sleeem.clowyy.triptracker.fragments.DatePickerFragment;
import ahmed.adel.sleeem.clowyy.triptracker.fragments.TimePickerFragment;
import ahmed.adel.sleeem.clowyy.triptracker.managers.DialogAlert;
import ahmed.adel.sleeem.clowyy.triptracker.service.MyService;

public class TripActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    EditText txtTripName, txtStartPoint, txtEndPoint;
    RadioButton rbOneWay, rbRoundTrip;

    Button timeBtn,dateBtn;

    Dialog roundTripDialog, notesDialog;

    private Calendar calendar;
    private String calDate;

    int year;
    int month;
    int dayOfMonth;
    int minute;
    int hourOfDay;
    private String timeTxt;

    Button btnAddTrip;

    List<String> tripNotes, roundTripNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        initView();

        roundTripDialog = new Dialog(this);
        notesDialog = new Dialog(this);

        roundTripDialog.setContentView(R.layout.round_trip);
        notesDialog.setContentView(R.layout.notes_dialog);

        tripNotes = new ArrayList<>();
        roundTripNotes = new ArrayList<>();


        calendar = Calendar.getInstance();


        btnAddTrip.setOnClickListener(v -> {

            if(calDate.length()>0 && timeTxt.length()>0 &&  txtStartPoint.getText().length()>0 && txtEndPoint.getText().length()>0
            && txtTripName.getText().length()>0){
                StringBuilder oneWaysNote=new StringBuilder("");
                StringBuilder roundNote=new StringBuilder("");

                for(String note:tripNotes)oneWaysNote.append("0"+note+",");
                for(String note:roundTripNotes)roundNote.append("0"+note+",");

                //adding to room


//                Intent intent = new Intent(this, MyService.class);
//                PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
//                AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//                alarm.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), 30*1000, pintent);
//

            }else {
                Toast.makeText(getBaseContext(), "please complete all feilds", Toast.LENGTH_LONG).show();
            }


        });


       timeBtn.setOnClickListener(v->{

            DialogFragment timePickerFragment = new TimePickerFragment();
            timePickerFragment.show(getSupportFragmentManager(),"time Packer");
        });

        dateBtn.setOnClickListener(v->{
            DialogFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.show(getSupportFragmentManager(),"date Picker");
        });

        rbRoundTrip.setOnClickListener(v->{
            showTripDialog();
        });


        txtStartPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(TripActivity.this);
                startActivityForResult(intent,100);
            }
        });

        txtEndPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fieldList).build(TripActivity.this);
                startActivityForResult(intent,110);
            }
        });

        findViewById(R.id.btnAddTrip).setOnClickListener(v->{

        });

        findViewById(R.id.btnAddNotes).setOnClickListener(v->{
            showNotesDialog(tripNotes);
        });
    }

    private void showNotesDialog(List<String> notes) {
        ListView lvNotes = notesDialog.findViewById(R.id.lvNotes);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);

        lvNotes.setAdapter(stringArrayAdapter);

        lvNotes.setOnItemLongClickListener((parent, view, position, id) -> {
            Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
            return true;
        });

        EditText txtNote = notesDialog.findViewById(R.id.txtNote);
        notesDialog.findViewById(R.id.btnAddNote).setOnClickListener(v->{
            if(txtNote.getText().toString().length() > 1) {
                notes.add(txtNote.getText().toString());
                stringArrayAdapter.notifyDataSetChanged();
                txtNote.setText("");
            }
        });

        notesDialog.findViewById(R.id.btnNotesDone).setOnClickListener(v->{
            if(txtNote.getText().toString().length() > 1) {
                notes.add(txtNote.getText().toString());
                txtNote.setText("");
            }
            notesDialog.dismiss();
        });

        notesDialog.show();

    }

    private void showTripDialog(){
        EditText txtBackTripName, txtBackStartPoint, txtBackEndPoint;
        Button btnAddRoundNotes;

        txtBackTripName = roundTripDialog.findViewById(R.id.txtBackTripName);
        txtBackStartPoint = roundTripDialog.findViewById(R.id.txtBackStartPoint);
        txtBackEndPoint = roundTripDialog.findViewById(R.id.txtBackEndPoint);


        txtBackTripName.setText("Back from " + txtEndPoint.getText().toString());
        txtBackStartPoint.setText(txtEndPoint.getText().toString());
        txtBackEndPoint.setText(txtStartPoint.getText().toString());

        roundTripDialog.findViewById(R.id.btnDone).setOnClickListener(v->{

        });

        roundTripDialog.findViewById(R.id.btnAddRoundNotes).setOnClickListener(v -> {
            showNotesDialog(roundTripNotes);
        });

        roundTripDialog.findViewById(R.id.btnCancel).setOnClickListener(v->{ roundTripDialog.dismiss(); });


        roundTripDialog.findViewById(R.id.timerBtnRound).setOnClickListener(v->{
            DialogFragment timePickerFragment = new TimePickerFragment();
            timePickerFragment.show(getSupportFragmentManager(),"time Packer");
        });

        roundTripDialog.findViewById(R.id.dateBtnRound).setOnClickListener(v->{
            DialogFragment datePickerFragment = new DatePickerFragment();
            datePickerFragment.show(getSupportFragmentManager(),"date Picker");
        });

        roundTripDialog.show();
    }

    private void initView(){
        txtTripName = findViewById(R.id.txtBackTripName);
        txtStartPoint = findViewById(R.id.txtBackStartPoint);
        txtEndPoint = findViewById(R.id.txtBackEndPoint);
       // txtTripNote = findViewById(R.id.txtBackNotes);

        btnAddTrip = findViewById(R.id.btnAddTrip);

        rbOneWay = findViewById(R.id.rbOneWay);
        rbRoundTrip = findViewById(R.id.rbRoundTrip);

        timeBtn= findViewById(R.id.timerBtn);
        dateBtn= findViewById(R.id.dateBtn);

        Places.initialize(getApplicationContext(),getString(R.string.google_maps_key));
        txtStartPoint.setFocusable(false);
        txtEndPoint.setFocusable(false);
    }



    //handle time setting
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
       this.hourOfDay=hourOfDay;
        this.minute=minute;
        calendar.set(Calendar.MINUTE , minute);
        calendar.set(Calendar.HOUR_OF_DAY , hourOfDay);
        calendar.set(Calendar.SECOND , 0);
        timeTxt = String.valueOf(hourOfDay)+ " : " + String.valueOf(minute);
    }

    //handle date setting
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        TripActivity.this.year=year;
        TripActivity.this.month=month;
        TripActivity.this.dayOfMonth=dayOfMonth;
        calendar.set(Calendar.YEAR , year);
        calendar.set(Calendar.MONTH , month);
        calendar.set(Calendar.DAY_OF_MONTH , dayOfMonth);
        String date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        calDate = date;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK)
        {
            Place place = Autocomplete.getPlaceFromIntent(data);
            txtStartPoint.setText(place.getAddress());
        }
        else if(requestCode == 110 && resultCode == RESULT_OK)
        {
            Place place = Autocomplete.getPlaceFromIntent(data);
            txtEndPoint.setText(place.getAddress());
        }
        else if(resultCode == AutocompleteActivity.RESULT_ERROR)
        {
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void startAlarm(Calendar c, Trip curTrip) {

        AlarmManager alarmang = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), DialogAlert.class);
//        intent.putExtra("id",curTrip.getTripId());
//        intent.putExtra("reqCode",curTrip.getRequestCode()+"");
//        intent.putExtra("endPoint",curTrip.getEndPlaceName());
//        intent.putExtra("lati",curTrip.getEndLatitude()+"");
//        intent.putExtra("long",curTrip.getEndLongtude()+"");
//        intent.putExtra("notes",curTrip.getNotes());

        c.set(Calendar.YEAR , year);
        c.set(Calendar.MONTH , month);
        c.set(Calendar.DAY_OF_MONTH , dayOfMonth);
        c.set(Calendar.MINUTE , minute);
        Log.i("nasor",minute+"");
//        Log.i("nasor",curTrip.getMinute()+"");
        c.set(Calendar.HOUR_OF_DAY , hourOfDay);
        c.set(Calendar.SECOND , 0);

        PendingIntent pi =  PendingIntent.getBroadcast(this , PendingIntent.FLAG_CANCEL_CURRENT, intent , 0);
        alarmang.setRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pi);

    }
}