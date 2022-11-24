package team.diamond.firebase2;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import team.diamond.firebase2.adapters.AdapterUsers;
import team.diamond.firebase2.models.ModelUsers;


public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUsers> usersList;

    FirebaseAuth firebaseAuth;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public UsersFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        //init
        firebaseAuth = FirebaseAuth.getInstance();
        //init recyclerview
        recyclerView = view.findViewById(R.id.users_recyclerView);
        //set it's properties(характеристики)
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //init user list
        usersList = new ArrayList<>();

        //getAll users
        getAllUsers();

        return view;
    }

    private void getAllUsers() {
        //get current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get patch of database named "Users" containing users ifo
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from patch
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelUsers modelUsers = ds.getValue(ModelUsers.class);

                    //get all users except currently signed in user
                    if (!modelUsers.getUid().equals(fUser.getUid())) {
                        usersList.add(modelUsers);
                    }

                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(), usersList);
                    //set adapter
                    recyclerView.setAdapter(adapterUsers);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void searchUsers(String query) {

        //get current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        //get patch of database named "Users" containing users ifo
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from patch
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelUsers modelUsers = ds.getValue(ModelUsers.class);

                    /*Conditions to fulfil search:
                    1) User not current user
                    2)The user name or email contains text entered in SearchView (case insensitive)
                     */

                    //get all Searched users except currently signed in user
                    if (!modelUsers.getUid().equals(fUser.getUid())) {

                        if (modelUsers.getName().toLowerCase().contains(query.toLowerCase(Locale.ROOT)) ||
                                modelUsers.getEmail().toLowerCase().contains(query.toLowerCase())) {
                            usersList.add(modelUsers);

                        }

                    }

                    //adapter
                    adapterUsers = new AdapterUsers(getActivity(), usersList);
                    //refresh adapter
                    adapterUsers.notifyDataSetChanged();
                    //set adapter
                    recyclerView.setAdapter(adapterUsers);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkUserStatus() {
        // get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is single in stay here

            //set email of logged in user
            // mProfileTv.setText(user.getEmail());

        } else {
            //user not single in, go to main activity
            startActivity(new Intent(getActivity(), team.diamond.firebase2.MainActivity.class));
            getActivity().finish();
        }
    }

//    @Override
//    public void onCreate(@NonNull Bundle savedInstanceState) {
//        setHasOptionsMenu(true);// to show menu option in fragment ERROR
//        super.onCreate(savedInstanceState);
//    }


    /*inflate options menu*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflating menu
        inflater.inflate(R.menu.menu_main, menu);

        //Search view
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //called when user press search button from keyboard
                //if search query is not empty then search
                if (!TextUtils.isEmpty(s.trim())) {
                    //search text contains text, search it
                    searchUsers(s);
                } else {
                    //search text empty, get all users
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //called whenever user press any  single letter
                //if search query is not empty then search
                if (!TextUtils.isEmpty(s.trim())) {
                    //search text contains text, search it
                    searchUsers(s);
                } else {
                    //search text empty, get all users
                    getAllUsers();
                }

                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }


    /*handle menu item clicks*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }

        return super.onOptionsItemSelected(item);
    }


}