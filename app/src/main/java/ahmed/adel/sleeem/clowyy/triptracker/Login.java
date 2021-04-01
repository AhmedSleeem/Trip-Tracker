package ahmed.adel.sleeem.clowyy.triptracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import ahmed.adel.sleeem.clowyy.triptracker.helpers.User;


public class Login extends AppCompatActivity {

    EditText emailTxt, passwordTxt;
    Button login, twitterBtn;
    TextView register;

    private static final int RC_SIGN_IN = 1000;
    private FirebaseAuth mAuth;
    SignInButton googleBtn;
    GoogleSignInClient mGoogleSignInClient;

    SessionManager session;
    CallbackManager callbackManager;

    LoginButton facebookBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();
        session = new SessionManager(getApplicationContext());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        emailTxt = findViewById(R.id.emailEditTxt);
        passwordTxt = findViewById(R.id.passwordEditTxt);
        register = findViewById(R.id.registerTxt);
        login = findViewById(R.id.loginBtn);

        googleBtn = findViewById(R.id.googleBtn);
        facebookBtn = findViewById(R.id.btnFacebook);
        twitterBtn = findViewById(R.id.btnTwitter);

        facebookBtn.setReadPermissions(Arrays.asList("email"));

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTxt.getText().toString();
                String password = passwordTxt.getText().toString();
                loginAuthentication(email, password);
            }
        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInGoogle();
            }
        });

        facebookBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handelFacebookToken(loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(Login.this, "Failed to signIn", Toast.LENGTH_SHORT).show();
            }
        });

        twitterBtn.setOnClickListener(v -> {
            OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
            mAuth.startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            String userID = authResult.getUser().getUid();
                            String name = authResult.getUser().getDisplayName();
                            String username = authResult.getAdditionalUserInfo().getUsername();
                            String photoUrl = authResult.getUser().getPhotoUrl().toString();

                            session.createLoginSession(username, name, photoUrl);

                            FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.hasChild(userID)) {
                                        User user = new User(name, username);
                                        FirebaseDatabase.getInstance().getReference("users").child(userID).setValue(user);
                                    }

                                    Toast.makeText(Login.this, "Authentication succeeded with " + username, Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    }).addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        });
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void loginAuthentication(String email, String password) {
        if (!email.isEmpty()) {
            if (!password.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Login succeeded.", Toast.LENGTH_SHORT).show();

                                    FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String userID = task.getResult().getUser().getUid();

                                            User user = snapshot.child(userID).getValue(User.class);

                                            if(user != null) {
                                                String name = user.getName();
                                                String email = user.getEmail();

                                                session.createLoginSession(email, name, null);
                                            }
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            finish();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                } else {
                                    Toast.makeText(Login.this, "Wrong email or password !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                passwordTxt.setError("Please enter password!");
            }
        } else {
            emailTxt.setError("Please enter email!");
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Login succeeded.", Toast.LENGTH_SHORT).show();

                            String userID = task.getResult().getUser().getUid();
                            String name = task.getResult().getUser().getDisplayName();
                            String email = task.getResult().getUser().getEmail();
                            String photoUrl = task.getResult().getUser().getPhotoUrl().toString();

                            saveUserIntoFirebase(userID, name, email);

                            session.createLoginSession(email, name, photoUrl);

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(Login.this, "Failed to signIn", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handelFacebookToken(String token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Login succeeded.", Toast.LENGTH_SHORT).show();

                    String userID = task.getResult().getUser().getUid();
                    String name = task.getResult().getUser().getDisplayName();
                    String email = task.getResult().getUser().getEmail();
                    String photoUrl = task.getResult().getUser().getPhotoUrl().toString();

                    saveUserIntoFirebase(userID, name, email);

                    session.createLoginSession(email, name, photoUrl);

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();

                } else {
                    Toast.makeText(Login.this, "Failed to signIn", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void saveUserIntoFirebase(String userID, String name, String email) {
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild(userID)) {
                    User user = new User(name, email);
                    FirebaseDatabase.getInstance().getReference("users").child(userID).setValue(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}