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

public class Register extends AppCompatActivity {

    EditText emailTxt,passwordTxt;
    Button registerBtn;
    TextView login;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailTxt = findViewById(R.id.emailSignup);
        passwordTxt = findViewById(R.id.passwordSignup);
        login = findViewById(R.id.loginTxt);
        registerBtn = findViewById(R.id.registerBtn);

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(Register.this, Login.class);
                startActivity(loginIntent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTxt.getText().toString();
                String password = passwordTxt.getText().toString();

                registerAuthentication(email,password);
            }
        });

    }

    private void registerAuthentication(String email, String password) {
        if(!email.isEmpty())
        {
            if(!password.isEmpty()) {
                if(password.length()>=6) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(Register.this, "Authentication Succeeded.",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Register.this, Login.class);
                                        startActivity(intent);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Register.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    passwordTxt.setError("Less than 6 characters!");
                }
            }
            else
            {
                passwordTxt.setError("Please enter password!");
            }
        }
        else
        {
            emailTxt.setError("Please enter email!");
        }

    }
}
