package team.diamond.firebase2.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import team.diamond.firebase2.R;
import team.diamond.firebase2.models.ModelChat;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<ModelChat> chatList;
    String imageUrl;

    FirebaseUser fUser;

    //все что связано с временем
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String Date;


    //constructor WTF ?
    public AdapterChat(Context context, List<ModelChat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate layouts: row_chat_left.xml for receiver, row_chat_right.xml for sender
        if (i == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, viewGroup, false);
            return new MyHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, viewGroup, false);
            return new MyHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, final int i) { // error ?
        //get data
        String message = chatList.get(i).getMessage();
        String timeStamp = chatList.get(i).getMessage();

        //custom not original
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        Date = simpleDateFormat.format(calendar.getTime());

        //click to show delete dialog
        myHolder.messageLAyout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show delete message confirm dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this message ?");
                //delete button
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteMEssage(i);   // обрати внимание на  i  !!!!
                    }
                });
                //  cancel delete button
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // dismiss dialog
                        dialog.dismiss();
                    }
                });
                //create and show dialog
                builder.create().show();


            }
        });


        //set data
        myHolder.messageTv.setText(message);
        myHolder.timeTv.setText(Date);
        try {
            Picasso.get().load(imageUrl).into(myHolder.profileIv);
        } catch (Exception e) {

        }

        //set seen/delivered status of message
        if (i == chatList.size() - 1) {
            if (chatList.get(i).isSeen()) {
                myHolder.isSeenTv.setText("Seen");
            } else {
                myHolder.isSeenTv.setText("Delivered");
            }
        } else {
            myHolder.isSeenTv.setVisibility(View.GONE);
        }
    }

    private void deleteMEssage(int position) {   //  хз возможно тут у меня ошибка так как утут было исправление    i ??????  position
        String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();





        /* Logic
        Get timestamp of clicked message with all message in Cgats
        Compare the timestamp of the cliked message with all messages in Chats
        Where both values matches delete that message
         */
        String msgTimeStapm = chatList.get(position).getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStapm);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    /* if you want to allow sender to delete only his message then
                    compare sender value with current user;s uid
                    if they match means its the message of sender that is trying to delete
                     */
                    if (ds.child("sender").getValue().equals(myUID)) {
                              /* We can one two things here
                    1) Remove the message from Chats
                    2) Set the value of message "This message was delete..."
                    So do whatever you want
                       */


                        //you can use one of these two methods to delete, so choose whats suite you best



                        //1) Remove the message from Chats
                        ds.getRef().removeValue(); // now test this

                        //2)Set the value of message "This message was deleted...  //test this
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("message", "This message was deleted...");
//                        ds.getRef().updateChildren(hashMap);

                        Toast.makeText(context, "message deleted...", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "You can delete only your messages...", Toast.LENGTH_SHORT).show();

                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently singed user
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }


    }

    //view holder class

    class MyHolder extends RecyclerView.ViewHolder {

        //views
        ImageView profileIv;
        TextView messageTv, timeTv, isSeenTv;
        LinearLayout messageLAyout; // for click listener to show delete


        public MyHolder(@NonNull View itemView) {
            super(itemView);


            //init views
            profileIv = itemView.findViewById(R.id.profileIv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
            messageLAyout = itemView.findViewById(R.id.messageLayout);


        }
    }

}
