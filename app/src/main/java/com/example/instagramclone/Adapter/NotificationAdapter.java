package com.example.instagramclone.Adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Fragment.PostDetailFragment;
import com.example.instagramclone.Fragment.ProfileFragment;
import com.example.instagramclone.Model.Notification;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context mContext, List<Notification> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( mContext ).inflate( R.layout.notification_item,parent,false );
        return new NotificationAdapter.ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Notification notification = mNotification.get( position );

        holder.text.setText( notification.getText() );

        getUserInfo( holder.image_profile,holder.username,notification.getUserid() );

        if(notification.isIspost()){
            holder.post_image.setVisibility( View.VISIBLE );
            getPostImage( holder.post_image,notification.getPostid() );
        }else{
            holder.post_image.setVisibility( View.GONE );
        }

        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.isIspost()){
                    SharedPreferences.Editor editor = mContext.getSharedPreferences( "PREFS",Context.MODE_PRIVATE ).edit();
                    editor.putString( "postid",notification.getPostid() ).commit();
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                            new PostDetailFragment() );
                }else{
                    SharedPreferences.Editor editor = mContext.getSharedPreferences( "PREFS",Context.MODE_PRIVATE ).edit();
                    editor.putString( "profileid",notification.getUserid() );
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                            new ProfileFragment() ).commit();
                }
            }
        } );

    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile,post_image;
        public TextView username, text;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );

            image_profile=itemView.findViewById( R.id.image_profile );
            post_image=itemView.findViewById( R.id.post_image );
            username=itemView.findViewById( R.id.username );
            text=itemView.findViewById( R.id.comment );


        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username,String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child( publisherid );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue( User.class );
                Glide.with( mContext ).load( user.getImageurl() ).into( imageView );
                username.setText( user.getUsername() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void getPostImage(final ImageView imageView, final String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child( postid );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue( Post.class );
                Glide.with( mContext ).load( post.getPostimage() ).into( imageView );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

}
