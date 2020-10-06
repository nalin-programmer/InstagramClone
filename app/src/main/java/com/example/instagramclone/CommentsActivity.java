package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Adapter.CommonAdapter;
import com.example.instagramclone.Model.Comment;
import com.example.instagramclone.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommonAdapter commentAdapter;
    private List<Comment> commentList;

    EditText addcomment;
    ImageView image_profile;
    TextView post;

    String postid;
    String publisherid;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_comments );

        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setTitle( "Comments" );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );

        Intent intent = getIntent();
        postid = intent.getStringExtra( "postid" );
        publisherid = intent.getStringExtra( "publisherid" );

        recyclerView = findViewById( R.id.recycle_view );
        recyclerView.setHasFixedSize( true );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( this );
        recyclerView.setLayoutManager( linearLayoutManager );
        commentList = new ArrayList<>(  );
        commentAdapter = new CommonAdapter(this,commentList,postid);
        recyclerView.setAdapter( commentAdapter );



        addcomment = findViewById( R.id.add_comment );
        image_profile = findViewById( R.id.image_profile );
        post = findViewById( R.id.post );

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        post.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addcomment.getText().toString().equals( "" )){
                    Toast.makeText( CommentsActivity.this,"You can't send empty comment",Toast.LENGTH_SHORT ).show();
                }else {
                    addComment();
                }
            }
        } );
        getImage();
        readComments();
    }

    private void addComment(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child( postid );

        String commentid = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put( "comment",addcomment.getText().toString() );
        hashMap.put( "publisher", firebaseUser.getUid() );
        hashMap.put( "commentid", commentid );


        reference.child(commentid).setValue( hashMap );

        addNotifications();

        addcomment.setText( "" );
    }

    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child( publisherid );

        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put( "userid", firebaseUser.getUid() );
        hashMap.put( "text", "commented: "+addcomment.getText().toString() );
        hashMap.put( "postid", postid );
        hashMap.put( "ispost", true );

        reference.push().setValue( hashMap );
    }

    private void getImage(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child( firebaseUser.getUid() );

        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue( User.class );
                Glide.with( getApplicationContext() ).load( user.getImageurl() ).into( image_profile );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child( postid );
        Log.i("postid",postid);
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                //Log.i("commentsAdapter",  commentAdapter.toString() );
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Comment comment = snapshot1.getValue(Comment.class);
                    //Log.i("comments",  commentList.toString() );
                    commentList.add( comment );
                }
                commentAdapter.notifyDataSetChanged();

                //Log.i("comments",  commentList.toString() );
               // Log.i("commentsAdapter",  commentAdapter.toString() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
}