package com.example.instagramclone.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.Adapter.PostAdapter;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {

    String postid;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_post_detail, container, false );

        SharedPreferences preferences =getContext().getSharedPreferences( "PREFS", Context.MODE_PRIVATE );
        postid = preferences.getString( "postid","none" );

        recyclerView = view.findViewById( R.id.recycle_view );
        recyclerView.setHasFixedSize( true );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getContext() );
        recyclerView.setLayoutManager( linearLayoutManager );

        postList = new ArrayList<>();
        postAdapter = new PostAdapter( getContext() , postList );
        recyclerView.setAdapter( postAdapter );

        readPost();

        return view;
    }

    private void readPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child( postid );

        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postList.clear();
                Post post = snapshot.getValue( Post.class );
                postList.add( post );

                postAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
}