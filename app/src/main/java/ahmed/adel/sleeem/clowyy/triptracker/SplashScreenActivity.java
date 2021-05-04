package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView logoImage;
    TextView sloganTxt;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        logoImage=findViewById(R.id.userImage);

        sloganTxt = findViewById(R.id.appName);
        //logoImage.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.side_anim));
        sloganTxt.setAnimation(AnimationUtils.loadAnimation(getBaseContext(),R.anim.bottom_anim));

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().getCurrentUser().reload();
        }

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.MY_PREF, MODE_PRIVATE);
        String lang = sharedPreferences.getString(MainActivity.LANG_VALUE, "en");
        setLocale(this, lang);

        if(lang.equals("ar")){
            ((TextView)findViewById(R.id.appName)).setText("سفــــارى");
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setLocale(Activity activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}