package team.diamond.firebase2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    EditText mEmailEt, mPasswordEt;
    Button mRegisterBtn;
    TextView mHaveAccountTv;


    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        hooks();

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");


        //Actionbar and its title
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        // активирует кнопку назад в title bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();
                //validate
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // set error and focus to email edittext
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                } else if (password.length() < 6) {
                    // set error and focus to password edittext
                    mPasswordEt.setError("Password length at least 6 characters");
                    mPasswordEt.setFocusable(true);
                } else {
                    registerUser(email, password);
                }

            }
        });

        mHaveAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, team.diamond.firebase2.Login.class));
                finish();
            }
        });


    }

    private void registerUser(String email, String password) {
        // email and password pattern is valid show progress dialog and start registering user
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //single in success, dismiss and start register activity
                            progressDialog.dismiss();

                            FirebaseUser user = mAuth.getCurrentUser();
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


                            Toast.makeText(Register.this, "Registred...\n", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this, dashboard.class));
                            finish();
                        } else
                            // if sing in fails, display a message  to the user
                            progressDialog.dismiss();
                        Toast.makeText(Register.this, "Aut failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void hooks() {
        mEmailEt = findViewById(R.id.emailEt);
        mPasswordEt = findViewById(R.id.passwordEt);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mHaveAccountTv = findViewById(R.id.have_accountTv);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void onBackPressed() {
        try {
            Intent intent = new Intent(Register.this, MainActivity.class); // try = попытка
            startActivity(intent);
            finish();
        } catch (Exception e) {  // catch = ловить
        }
    }


}