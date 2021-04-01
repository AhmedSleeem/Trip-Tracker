package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView logoImage;
    TextView sloganTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        logoImage=findViewById(R.id.userImage);

        sloganTxt = findViewById(R.id.userEmail);
        logoImage.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.side_anim));
        sloganTxt.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.bottom_anim));

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().getCurrentUser().reload();
        }

        new Handler().postDelayed(()->{
            if (new SessionManager(getApplicationContext()).isLoggedIn() && FirebaseAuth.getInstance().getCurrentUser() != null) {
                startActivity(new Intent(this, MainActivity.class));
            }else {
                startActivity(new Intent(this, Login.class));
            }

            finish();

        },3000);
    }
}