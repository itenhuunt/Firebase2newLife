package team.diamond.firebase2.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import team.diamond.firebase2.ChatActivity;
import team.diamond.firebase2.Chat_listFragment;
import team.diamond.firebase2.R;
import team.diamond.firebase2.models.ModelUsers;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    List<ModelUsers> usersList;

    //constructor
    public AdapterUsers(Context context, List<ModelUsers> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @androidx.annotation.NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull @androidx.annotation.NonNull ViewGroup viewGroup, int i) {
        //inflate layout  (row_user.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_card_user, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @androidx.annotation.NonNull MyHolder myHolder, int i) {
        //get data
        String hisUID = usersList.get(i).getUid();
        String userImage = usersList.get(i).getImage();
        String userName = usersList.get(i).getName();
        String userEmail = usersList.get(i).getEmail();

        //set data
        myHolder.mNameTv.setText(userName);
        myHolder.mEmailTv.setText(userEmail);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_default_img)
                    .into(myHolder.mAvatarIv);

        } catch (Exception e) {

        }


        //handle item click
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Toast.makeText(context, "" + userEmail, Toast.LENGTH_SHORT).show();

                /*Click user from user list to start chatting/messaging
                Start activity by putting UID of receiver
                we will use that UID to identify the we are gonna chat
                 */
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUID);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    // view holder class

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView mAvatarIv;
        TextView mNameTv, mEmailTv;


        public MyHolder(@NonNull @androidx.annotation.NonNull View itemView) {
            super(itemView);

            //init views
            mAvatarIv = itemView.findViewById(R.id.mAvatarIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);

        }

    }
}
