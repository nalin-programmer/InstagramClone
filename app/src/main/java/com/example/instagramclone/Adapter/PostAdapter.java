package com.example.instagramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Model.Post;
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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context mComtext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mComtext, List<Post> mPost) {
        this.mComtext = mComtext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( mComtext ).inflate( R.layout.post_item,parent,false );
        return new PostAdapter.ViewHolder( view );
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get( position );

        Glide.with( mComtext ).load( post.getPostimage() ).into( holder.post_image);

        if(post.getDescription().equals( "" )){
            holder.description.setVisibility( View.GONE );
        }else{
            holder.description.setVisibility( View.VISIBLE );
            holder.description.setText( post.getDescription() );
        }

        publisherInfo( holder.image_profile,holder.username,holder.publisher,post.getPublisher() );

    }
    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile,post_image,like,comment,save;
        public TextView username, likes, publisher, description, comments;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );

            image_profile=itemView.findViewById( R.id.image_profile );
            post_image=itemView.findViewById( R.id.post_image );
            like=itemView.findViewById( R.id.like );
            comment=itemView.findViewById( R.id.comment );
            save=itemView.findViewById( R.id.save );
            username=itemView.findViewById( R.id.username );
            likes=itemView.findViewById( R.id.likes );
            publisher=itemView.findViewById( R.id.publisher );
            description=itemView.findViewById( R.id.description );
            comments=itemView.findViewById( R.id.comments );

        }

    }

    private  void publisherInfo(final ImageView image_profile, TextView username, final TextView publisher, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child( userid );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue( User.class );
                Glide.with( mComtext ).load( user.getImageurl() ).into( image_profile );
                publisher.setText( user.getUsername() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

}
