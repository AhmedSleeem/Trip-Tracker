package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import ahmed.adel.sleeem.clowyy.triptracker.fragments.DatePickerFragment;
import ahmed.adel.sleeem.clowyy.triptracker.fragments.TimePickerFragment;

public class TripActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    EditText txtTripName, txtStartPoint, txtEndPoint, txtNotes;
    RadioButton rbOneWay, rbRoundTrip;

    Button timeBtn,dateBtn;

    Dialog roundTripDialog;

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
}