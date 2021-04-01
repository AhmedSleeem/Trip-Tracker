package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ahmed.adel.sleeem.clowyy.triptracker.helpers.User;

public class Register extends AppCompatActivity {

    EditText nameTxt, emailTxt,passwordTxt;
    Button registerBtn;
    TextView login;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameTxt = findViewById(R.id.userNameTxt);
        emailTxt = findViewById(R.id.emailSignup);
        passwordTxt = findViewById(R.id.passwordSignup);
        login = findViewById(R.id.loginTxt);
        registerBtn = findViewById(R.id.registerBtn);

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameTxt.getText().toString();
                String email = emailTxt.getText().toString();
                String password = passwordTxt.getText().toString();

                registerAuthentication(name,email,password);
            }
        });

    }

    private void registerAuthentication(String name,String email, String password) {
        if(!name.isEmpty()) {
            if (!email.isEmpty()) {
                if (!password.isEmpty()) {
                    if (password.length() >= 6) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            String userID = task.getResult().getUser().getUid();
                                            User user = new User(nameTxt.getText().toString(), emailTxt.getText().toString());

                                            FirebaseDatabase.getInstance().getReference("users").child(userID).setValue(user);

                                            Toast.makeText(Register.this, getString(R.string.authenticationSucceded), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(Register.this, getString(R.string.authenticationFailed), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        passwordTxt.setError(getString(R.string.passwordErrorConditions));
                    }
                } else {
                    passwordTxt.setError(getString(R.string.enterpasswordMSG));
                }
            } else {
                emailTxt.setError(getString(R.string.enteremailMSG));
            }
        }
        else{
            nameTxt.setError(getString(R.string.enterusernameMSG));
        }


    }

}
