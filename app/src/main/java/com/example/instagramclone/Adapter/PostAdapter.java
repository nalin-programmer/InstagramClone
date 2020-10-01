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
import com.example.instagramclone.CommentsActivity;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get( position );

        Glide.with( mComtext ).load( post.getPostimage() ).into( holder.post_image);

        if(post.getDescription().equals( "" )){
            holder.description.setVisibility( View.GONE );
        }else{
            holder.description.setVisibility( View.VISIBLE );
            holder.description.setText( post.getDescription() );
            getComments( post.getPostid(),holder.comments );
        }

        publisherInfo( holder.image_profile,holder.username,holder.publisher,post.getPublisher() );
        isLiked( post.getPostid(),holder.like );
        nrLikes( holder.likes, post.getPostid());
        getComments( post.getPostid(),holder.comments );

        holder.like.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals( "like" )){
                    FirebaseDatabase.getInstance().getReference().child( "Likes" ).child( post.getPostid() )
                            .child( firebaseUser.getUid() ).setValue( true );
                }else {
                    FirebaseDatabase.getInstance().getReference().child( "Likes" ).child( post.getPostid() )
                            .child( firebaseUser.getUid() ).removeValue();
                }
            }
        } );

        holder.comment.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( mComtext, CommentsActivity.class );
                intent.putExtra( "postid",post.getPostid() );
                intent.putExtra( "publisherid",post.getPublisher() );
                mComtext.startActivity( intent );
            }
        } );

        holder.comments.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( mComtext, CommentsActivity.class );
                intent.putExtra( "postid",post.getPostid() );
                intent.putExtra( "publisherid",post.getPublisher() );
                mComtext.startActivity( intent );
            }
        } );
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

    private void getComments(String postid, final TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child( "Comments" ).child( postid );

        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText( "View All "+ snapshot.getChildrenCount() + " Comments" );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void isLiked(String postid, final ImageView imageView){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child( "Likes" )
                .child( postid );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child( firebaseUser.getUid() ).exists()){
                    imageView.setImageResource( R.drawable.ic_liked );
                    imageView.setTag( "liked" );
                }else{
                    imageView.setImageResource( R.drawable.ic_like );
                    imageView.setTag( "like" );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void nrLikes(final TextView likes, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child( "Likes" )
                .child( postid );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText( snapshot.getChildrenCount() + " likes" );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private  void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child( userid );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue( User.class );
                Glide.with( mComtext ).load( user.getImageurl() ).into( image_profile );
                username.setText( user.getUsername() );
                publisher.setText( user.getUsername() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

}
