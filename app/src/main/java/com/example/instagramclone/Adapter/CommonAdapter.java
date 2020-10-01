package com.example.instagramclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.MainActivity;
import com.example.instagramclone.Model.Comment;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommonAdapter extends RecyclerView.Adapter<CommonAdapter.ViewHolder>{

    private Context mContext;
    private List<Comment> mComment;

    private FirebaseUser firebaseUser;

    public CommonAdapter(Context mContext, List<Comment> mComment) {
        this.mContext = mContext;
        this.mComment = mComment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( mContext ).inflate( R.layout.comment_item, parent, false );
        return new CommonAdapter.ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mComment.get( position );

        holder.comment.setText( comment.getComment() );
        getUserId( holder.image_profile,holder.username,comment.getPublisher() );

        holder.comment.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent( mContext, MainActivity.class );
                intent.putExtra( "publisherid", comment.getPublisher() );
                mContext.startActivity( intent );
            }
        } );

        holder.image_profile.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent( mContext, MainActivity.class );
                intent.putExtra( "publisherid", comment.getPublisher() );
                mContext.startActivity( intent );
            }
        } );

    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile;
        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );

            image_profile = itemView.findViewById( R.id.image_profile );
            username = itemView.findViewById( R.id.username );
            comment = itemView.findViewById( R.id.comment );
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( publisherid );

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

    private void getUserId(final ImageView imageView, final TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( publisherid );

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

}
