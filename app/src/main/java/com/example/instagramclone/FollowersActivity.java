package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.example.instagramclone.Adapter.UserAdapter;
import com.example.instagramclone.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FollowersActivity extends AppCompatActivity {

    String id;
    String title;

    List<String> idList;

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_followers );

        Intent intent = getIntent();
        id = intent.getStringExtra( "id" );
        title = intent.getStringExtra( "title" );
        Log.i("title",title);
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setTitle( title );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        toolbar.setNavigationOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );

        recyclerView = findViewById( R.id.recycle_view );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        userList = new ArrayList<>();
        userAdapter = new UserAdapter( this, userList ,false);
        recyclerView.setAdapter( userAdapter );

        idList = new ArrayList<>();

        switch (title){
            case "likes":
                Log.i("likes","entered");
                getLikes();
                break;
            case "following":
                getFollowing();
                break;
            case "followers":
                getFollowers();
                break;
            case "views":
                getViews();
                break;
        }
    }

    private void getViews(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child( id ).child( getIntent().getStringExtra( "storyid" ) ).child( "views" );
        reference.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    idList.add( snapshot1.getKey() );
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void getLikes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes")
                .child( id );
        reference.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    idList.add( snapshot1.getKey() );
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void getFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child( id ).child( "following" );
        Log.i("checking",id.toString());
        reference.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    idList.add( snapshot1.getKey() );
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child( id ).child( "followers" );
        reference.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    idList.add( snapshot1.getKey() );
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    protected void showUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    User user = snapshot1.getValue( User.class );
                    for(String id : idList){
                        if(user.getId().equals( id )){
                            Log.i("userss",user.toString());
                            userList.add( user );
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
}