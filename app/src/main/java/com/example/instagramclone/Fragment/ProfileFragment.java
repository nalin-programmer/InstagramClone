package com.example.instagramclone.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Adapter.MyFotoAdapter;
import com.example.instagramclone.EditProfileActivity;
import com.example.instagramclone.FollowersActivity;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.Model.User;
import com.example.instagramclone.OptionsActivity;
import com.example.instagramclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {

    ImageView image_Profile, options;
    TextView posts,followers,following,fullname,bio,username;
    Button edit_profile;

    private List<String> mySaves;

    RecyclerView recyclerView_saves;
    MyFotoAdapter myFotoAdapter_saves;
    List<Post> postList_saves;

    RecyclerView recyclerView;
    MyFotoAdapter myFotoAdapter;
    List<Post> postList;

    FirebaseUser firebaseUser;
    String profileid;

    ImageButton my_fotos, saved_fotos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_profile, container, false );

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Log.i( "check","123321" );
        SharedPreferences prefs = getContext().getSharedPreferences( "PREFS", Context.MODE_PRIVATE );
        // here here
        profileid = prefs.getString( "profileid",  firebaseUser.getUid() );
        //firebaseUser.getUid()
        image_Profile = view.findViewById( R.id.image_profile );
        options = view.findViewById( R.id.options );
        posts = view.findViewById( R.id.posts );
        followers = view.findViewById( R.id.followers );
        following = view.findViewById( R.id.following );
        fullname = view.findViewById( R.id.fullname );
        bio = view.findViewById( R.id.bio );
        username = view.findViewById( R.id.username );
        edit_profile = view.findViewById( R.id.edit_profile );
        my_fotos = view.findViewById( R.id.my_fotos );
        saved_fotos = view.findViewById( R.id.saved_fotos );

        recyclerView = view.findViewById( R.id.recycle_view );
        recyclerView.setHasFixedSize( true );
        LinearLayoutManager linearLayoutManager = new GridLayoutManager( getContext(),3 );
        recyclerView.setLayoutManager( linearLayoutManager );
        postList = new ArrayList<>();
        myFotoAdapter = new MyFotoAdapter( getContext(), postList );
        recyclerView.setAdapter( myFotoAdapter );

        recyclerView_saves = view.findViewById( R.id.recycle_view_save );
        recyclerView_saves.setHasFixedSize( true );
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager( getContext(),3 );
        recyclerView_saves.setLayoutManager( linearLayoutManager_saves );
        postList_saves = new ArrayList<>();
        myFotoAdapter_saves = new MyFotoAdapter( getContext(), postList_saves );
        recyclerView_saves.setAdapter( myFotoAdapter_saves );

        recyclerView.setVisibility( View.VISIBLE );
        recyclerView_saves.setVisibility( View.GONE );

        //Log.i("profileidPF", profileid);
        userInfo();
        getFollowers();
        getNrPosts();
        myFotos();
        mysaves();
       // Log.i("profileidPF", profileid);
        if(profileid.equals( firebaseUser.getUid() )){
            edit_profile.setText( "Edit Profile" );
        }else{
           checkFollow();
           saved_fotos.setVisibility( View.GONE );
        }

        edit_profile.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = edit_profile.getText().toString();

                if(btn.equals( "Edit Profile" )){
                    startActivity( new Intent( getContext(), EditProfileActivity.class ) );
                }else if(btn.equals( "follow" )){
                    FirebaseDatabase.getInstance().getReference().child( "Follow" ).child( firebaseUser.getUid() )
                            .child( "following" ).child( profileid ).setValue( true );
                    FirebaseDatabase.getInstance().getReference().child( "Follow" ).child( profileid )
                            .child( "followers" ).child( firebaseUser.getUid() ).setValue( true );
                    addNotifications();

                }else if(btn.equals( "following" )){
                    FirebaseDatabase.getInstance().getReference().child( "Follow" ).child( firebaseUser.getUid() )
                            .child( "following" ).child( profileid ).removeValue();
                    FirebaseDatabase.getInstance().getReference().child( "Follow" ).child( profileid )
                            .child( "followers" ).child( firebaseUser.getUid() ).removeValue();
                }
            }
        } );

        options.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getContext(), OptionsActivity.class );
                startActivity( intent );
            }
        } );

        my_fotos.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility( View.VISIBLE );
                recyclerView_saves.setVisibility( View.GONE );
            }
        } );

        saved_fotos.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility( View.GONE );
                recyclerView_saves.setVisibility( View.VISIBLE );
            }
        } );

        followers.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getContext(), FollowersActivity.class );
                intent.putExtra( "id",profileid );
                intent.putExtra( "title","followers" );
                Log.i("followers","followers");
                startActivity( intent );
            }
        } );

        following.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getContext(), FollowersActivity.class );
                intent.putExtra( "id",profileid );
                intent.putExtra( "title","following" );
                startActivity( intent );
            }
        } );

        return view;
    }

    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child( profileid );

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put( "userid", firebaseUser.getUid() );
        hashMap.put( "text", "started following you" );
        hashMap.put( "postid", "" );
        hashMap.put( "ispost", false );

        reference.push().setValue( hashMap );
    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child( profileid );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getContext() == null){
                    return;
                }

                User user = snapshot.getValue( User.class );

                Glide.with(getContext()).load( user.getImageurl() ).into( image_Profile );
                username.setText( user.getUsername() );
                fullname.setText( user.getFullname() );
                bio.setText( user.getBio() );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child( "Follow" ).child( firebaseUser.getUid() ).child( "following" );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child( profileid ).exists()){
                    edit_profile.setText( "following" );
                }else{
                    edit_profile.setText( "follow" );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child( "Follow" ).child( profileid ).child( "followers" );

        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText( ""+snapshot.getChildrenCount() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child( "Follow" ).child( profileid ).child( "following" );

        reference1.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText( ""+snapshot.getChildrenCount() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post = snapshot.getValue( Post.class );
                    if(post.getPublisher().equals( profileid )){
                        i++;
                    }
                }

                posts.setText( ""+i );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void myFotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Post post = snapshot1.getValue( Post.class );
                    if(post.getPublisher().equals( profileid )){
                        postList.add( post );
                    }
                }
                Collections.reverse( postList );
                myFotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void mysaves(){
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
                .child( firebaseUser.getUid() );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    mySaves.add( snapshot1.getKey() );
                }
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
    private void readSaves(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList_saves.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    Post post = snapshot1.getValue( Post.class );

                    for(String id : mySaves){
                        if(post.getPostid().equals( id )){
                            postList_saves.add( post );
                        }
                    }
                }
                myFotoAdapter_saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

    }
}