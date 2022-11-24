package team.diamond.firebase2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class dashboard extends AppCompatActivity {


    FirebaseAuth firebaseAuth;

    ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        //Actionbar and its title
        actionBar = getSupportActionBar();
        actionBar.setTitle("Profile");

        //init
        firebaseAuth = FirebaseAuth.getInstance();

        //Bottom navigation
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //home fragment transaction (default, on star)
        actionBar.setTitle("Home"); // change actionbar title
        team.diamond.firebase2.HomeFragment fragment1 = new team.diamond.firebase2.HomeFragment();
        FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content, fragment1, "");
        ft1.commit();


    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    //handle item clicks
                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            //home fragment transaction
                            actionBar.setTitle("Home"); // change actionbar title
                            team.diamond.firebase2.HomeFragment fragment1 = new team.diamond.firebase2.HomeFragment();
                            FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content, fragment1, "");
                            ft1.commit();
                            return true;
                        case R.id.nav_profile:
                            //profile fragment transaction
                            actionBar.setTitle("Profile"); // change actionbar title
                            team.diamond.firebase2.ProfileFragment fragment2 = new team.diamond.firebase2.ProfileFragment();
                            FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content, fragment2, "");
                            ft2.commit();
                            return true;
                        case R.id.nav_users:
                            //users fragment transaction
                            actionBar.setTitle("Users"); // change actionbar title
                            team.diamond.firebase2.UsersFragment fragment3 = new team.diamond.firebase2.UsersFragment();
                            FragmentTransaction ft3 = getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content, fragment3, "");
                            ft3.commit();
                            return true;
                        case R.id.nav_chat:
                            //chat fragment transaction
                            actionBar.setTitle("Chats"); // change actionbar title
                            team.diamond.firebase2.Chat_listFragment fragment4 = new team.diamond.firebase2.Chat_listFragment();
                            FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content, fragment4, "");
                            ft4.commit();
                            return true;
                    }
                    return false;
                }
            };

    private void checkUserStatus() {
        // get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is single in stay here

            //set email of logged in user
            // mProfileTv.setText(user.getEmail());

        } else {
            //user not single in, go to main activity
            startActivity(new Intent(dashboard.this, team.diamond.firebase2.MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        //check on start of app
        checkUserStatus();
        super.onStart();
    }



}