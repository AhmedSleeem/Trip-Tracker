package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

public class Dialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
//        displayAlert("message");
    }


        private void displayAlert(String msg)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(msg).setCancelable(
                    false).setPositiveButton("start",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
}