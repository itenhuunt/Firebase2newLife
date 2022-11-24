package team.diamond.firebase2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;

    EditText mEmailEt, mPasswordEt;
    TextView notHaveAccountTv, mRecoverPassw;
    Button mLoginBtn;
    SignInButton mGoogleLogin;

    // declare an instance of Firebase
    private FirebaseAuth mAuth;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
        // активирует кнопку назад в title bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //config google Sing In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        hooks();

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input data
                String email = mEmailEt.getText().toString();
                String passw = mPasswordEt.getText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //invalid email pattern set error;
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                } else {
                    //valid email pattern
                    loginUser(email, passw);
                }

            }
        });
        //not have account textview click
        notHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, MainActivity.class));
                finish();
            }
        });
        //recover password
        mRecoverPassw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        //handle google login
        mGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //begin google login process
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);


            }
        });


        //init progress dialog
        pd = new ProgressDialog(this);


    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recjver Password");

        //set layout liner layout
        LinearLayout linearLayout = new LinearLayout(this);
        //view to set in dialog
        EditText emailEt = new EditText(this);
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        emailEt.setMinEms(21);


        linearLayout.addView(emailEt);
        linearLayout.setPadding(10, 10, 10, 10);

        builder.setView(linearLayout);


        //buttons recover
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input email
                String email = emailEt.getText().toString().trim();
                beginRecovery(email);
            }
        });
        //buttons cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dismiss dialog
                dialog.dismiss();

            }
        });

        //show dialog
        builder.create().show();

    }

    private void beginRecovery(String email) {
        //show prorgess dialog
        pd.setMessage("Sending email...");
        pd.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();// зачем тут писать ?
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Email sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this, "Failed...", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss(); // зачем тут писать ?
                //get and show proper error message
                Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loginUser(String email, String passw) {
        mAuth.signInWithEmailAndPassword(email, passw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //dismiss progress dialog
                            pd.dismiss();

                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            //user is logged in, so start LoginActivity
                            startActivity(new Intent(Login.this, dashboard.class));
                            finish();

                        } else {
                            //dismiss progress dialog
                            pd.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //dismiss progress dialog
                        pd.dismiss();
                        // error, get and show error message
                        Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void hooks() {
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        notHaveAccountTv = findViewById(R.id.not_have_accountTv);
        mLoginBtn = findViewById(R.id.LoginBtn);
        mRecoverPassw = findViewById(R.id.recoverTv);
        mGoogleLogin = findViewById(R.id.googleBtn);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Result returned from is...
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    protected void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            //if user is signing in first time then and show user info from google account
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {

                                //get user email and uid auth
                                String email = user.getEmail();
                                String uid = user.getUid();
                                //when user is registered store user info in firebase realtime data base too
                                //using HashMap
                                HashMap<Object, String> hashMap = new HashMap<>();
                                //put info in hasmap
                                hashMap.put("email", email);
                                hashMap.put("uid", uid);
                                hashMap.put("name", ""); // will add alter e/g/ edit profile
                                hashMap.put("onlineStatus", "online");
                                hashMap.put("typingTo", "noOne");
                                hashMap.put("phone", "");
                                hashMap.put("image", "");
                                hashMap.put("cover", "");

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                //patch to store user data named "Users"
                                DatabaseReference reference = database.getReference("Users");
                                //put data whit in hashmap in database
                                reference.child(uid).setValue(hashMap);
                            }

                            //show user email in  toast
                            Toast.makeText(Login.this, "" + user.getEmail(), Toast.LENGTH_SHORT).show();
                            //go to profile activity after logged in
                            startActivity(new Intent(Login.this, dashboard.class));
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Login Failed...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


}