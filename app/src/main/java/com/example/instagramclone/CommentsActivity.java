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

        recyclerView = findViewById( R.id.recycle_view );
        recyclerView.setHasFixedSize( true );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( this );
        recyclerView.setLayoutManager( linearLayoutManager );
        commentList = new ArrayList<>(  );
        commentAdapter = new CommonAdapter(this,commentList);
        recyclerView.setAdapter( commentAdapter );



        addcomment = findViewById( R.id.add_comment );
        image_profile = findViewById( R.id.image_profile );
        post = findViewById( R.id.post );

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        postid = intent.getStringExtra( "postid" );
        publisherid = intent.getStringExtra( "publisherid" );

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

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put( "comment",addcomment.getText().toString() );
        hashMap.put( "publisher", firebaseUser.getUid() );

        reference.push().setValue( hashMap );
        addcomment.setText( "" );
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
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Comment comment = snapshot1.getValue(Comment.class);
                    Log.i("comments", String.valueOf( comment ) );
                    commentList.add( comment );
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
}