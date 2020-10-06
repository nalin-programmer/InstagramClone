package com.example.instagramclone.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagramclone.CommentsActivity;
import com.example.instagramclone.FollowersActivity;
import com.example.instagramclone.Fragment.PostDetailFragment;
import com.example.instagramclone.Fragment.ProfileFragment;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get( position );

        Glide.with( mComtext ).load( post.getPostimage() )
                .apply( new RequestOptions().placeholder( R.drawable.placeholder ) )
                .into( holder.post_image);

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
        isSaved( post.getPostid(),holder.save );


        holder.image_profile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mComtext.getSharedPreferences( "PREFS", Context.MODE_PRIVATE ).edit();
                editor.putString( "profileid",post.getPublisher() );
                editor.apply();

                ((FragmentActivity)mComtext).getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                        new ProfileFragment() ).commit();
            }
        } );

        holder.username.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mComtext.getSharedPreferences( "PREFS", Context.MODE_PRIVATE ).edit();
                editor.putString( "profileid",post.getPublisher() );
                editor.apply();

                ((FragmentActivity)mComtext).getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                        new ProfileFragment() ).commit();
            }
        } );

        holder.publisher.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mComtext.getSharedPreferences( "PREFS", Context.MODE_PRIVATE ).edit();
                editor.putString( "profileid",post.getPublisher() );
                editor.apply();

                ((FragmentActivity)mComtext).getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                        new ProfileFragment() ).commit();
            }
        } );

        holder.post_image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mComtext.getSharedPreferences( "PREFS", Context.MODE_PRIVATE ).edit();
                editor.putString( "postid",post.getPostid() );
                editor.apply();

                ((FragmentActivity)mComtext).getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                        new PostDetailFragment() ).commit();
            }
        } );

        holder.save.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.save.getTag().equals( "save" )){
                    FirebaseDatabase.getInstance().getReference().child( "Saves" ).child( firebaseUser.getUid() )
                            .child( post.getPostid() ).setValue( true );
                }else{
                    FirebaseDatabase.getInstance().getReference().child( "Saves" ).child( firebaseUser.getUid() )
                            .child( post.getPostid() ).removeValue();
                }
            }
        } );

        holder.like.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals( "like" )){
                    FirebaseDatabase.getInstance().getReference().child( "Likes" ).child( post.getPostid() )
                            .child( firebaseUser.getUid() ).setValue( true );
                    addNotifications( post.getPublisher(),post.getPostid() );
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

        holder.likes.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( mComtext, FollowersActivity.class );
                intent.putExtra( "id",post.getPostid() );
                intent.putExtra( "title","likes" );
                mComtext.startActivity( intent );
            }
        } );

        holder.more.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu( mComtext,v );
                popupMenu.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit:
                                editPost( post.getPostid() );
                                return true;
                            case R.id.delete:
                                Log.i("DeletePost","deletepost");
                                FirebaseDatabase.getInstance().getReference("Posts")
                                        .child( post.getPostid() ).removeValue()
                                        .addOnCompleteListener( new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText( mComtext,"Deleted!",Toast.LENGTH_SHORT ).show();
                                                }
                                            }
                                        } );
                                return true;
                            case R.id.report:
                                Toast.makeText( mComtext,"Report clicked!",Toast.LENGTH_SHORT ).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                } );
                popupMenu.inflate( R.menu.post_menu );
                if(!post.getPublisher().equals( firebaseUser.getUid() )){
                    popupMenu.getMenu().findItem( R.id.edit ).setVisible( false );
                    popupMenu.getMenu().findItem( R.id.delete ).setVisible( false );
                }
                popupMenu.show();
            }
        } );
    }
    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile,post_image,like,comment,save,more;
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
            more=itemView.findViewById( R.id.more );

        }

    }

    private void getComments(String postid, final TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child( postid );

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

    private void addNotifications(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child( "Notifications" ).child( userid );

        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put( "userid", firebaseUser.getUid() );
        hashMap.put( "text", "liked your post" );
        hashMap.put( "postid", postid );
        hashMap.put( "ispost", true );

        reference.push().setValue( hashMap );
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

    private void isSaved(final String postid, final ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child( "Saves" )
                .child( firebaseUser.getUid() );

        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child( postid ).exists()){
                    imageView.setImageResource( R.drawable.ic_save_black );
                    imageView.setTag( "saved" );
                }else{
                    imageView.setImageResource( R.drawable.ic_savee_black );
                    imageView.setTag( "save" );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

    }

    private void editPost(final String postid){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( mComtext );
        alertDialog.setTitle( "Edit Post" );

        final EditText editText = new EditText( mComtext );
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        editText.setLayoutParams( lp );
        alertDialog.setView( editText );

        getText( postid, editText );

        alertDialog.setPositiveButton( "Edit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("editText",editText.getText().toString());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put( "description", editText.getText().toString() );

                        FirebaseDatabase.getInstance().getReference("Posts")
                                .child( postid ).updateChildren( hashMap );
                    }
                } );
        alertDialog.setNegativeButton( "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                } );
        alertDialog.show();
    }

    private void getText(String postid, final EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child( postid );
        reference.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                editText.setText( snapshot.getValue( Post.class ).getDescription() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

}
