package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.fragments.DatePickerFragment;
import ahmed.adel.sleeem.clowyy.triptracker.fragments.TimePickerFragment;

public class TripActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    EditText txtTripName, txtStartPoint, txtEndPoint, txtNotes;
    RadioButton rbOneWay, rbRoundTrip;

    Button timeBtn,dateBtn;

    Dialog roundTripDialog;
    private static final String API_KEY = "AIzaSyDVh2YvCYg-Mcjn-pfEIxeth4Ey9il9vFA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        initView();

        roundTripDialog = new Dialog(this);


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

    }

    private void showTripDialog(){
        roundTripDialog.setContentView(R.layout.round_trip);

        EditText txtBackTripName, txtBackStartPoint, txtBackEndPoint, txtBackNotes;

        txtBackTripName = roundTripDialog.findViewById(R.id.txtBackTripName);
        txtBackStartPoint = roundTripDialog.findViewById(R.id.txtBackStartPoint);
        txtBackEndPoint = roundTripDialog.findViewById(R.id.txtBackEndPoint);
        txtBackNotes = roundTripDialog.findViewById(R.id.txtBackNotes);

        txtBackTripName.setText("Back from " + txtEndPoint.getText().toString());
        txtBackStartPoint.setText(txtEndPoint.getText().toString());
        txtBackEndPoint.setText(txtStartPoint.getText().toString());

        roundTripDialog.findViewById(R.id.btnDone).setOnClickListener(v->{

        });

        roundTripDialog.findViewById(R.id.btnCancel).setOnClickListener(v->{ roundTripDialog.dismiss(); });


        roundTripDialog.show();
    }

    private void initView(){
        txtTripName = findViewById(R.id.txtBackTripName);
        txtStartPoint = findViewById(R.id.txtBackStartPoint);
        txtEndPoint = findViewById(R.id.txtBackEndPoint);
        txtNotes = findViewById(R.id.txtBackNotes);

        rbOneWay = findViewById(R.id.rbOneWay);
        rbRoundTrip = findViewById(R.id.rbRoundTrip);

        timeBtn= findViewById(R.id.timerBtn);
        dateBtn= findViewById(R.id.dateBtn);

        Places.initialize(getApplicationContext(),API_KEY);
        txtStartPoint.setFocusable(false);
        txtEndPoint.setFocusable(false);
    }



    //handle time setting
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Toast.makeText(getBaseContext(),"hour : "+hourOfDay +" minute = "+minute,Toast.LENGTH_LONG).show();
    }

    //handle date setting
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Toast.makeText(getBaseContext(),"year = "+year+" month = "+month+" day = "+dayOfMonth,Toast.LENGTH_LONG).show();
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
}