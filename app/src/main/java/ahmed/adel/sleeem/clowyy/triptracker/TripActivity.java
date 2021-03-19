package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class TripActivity extends AppCompatActivity {

    EditText txtTripName, txtStartPoint, txtEndPoint, txtNotes;
    RadioButton rbOneWay, rbRoundTrip;

    Dialog roundTripDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        initView();

        roundTripDialog = new Dialog(this);

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
    }
}