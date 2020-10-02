package com.example.instagramclone.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagramclone.Adapter.NotificationAdapter;
import com.example.instagramclone.Model.Notification;
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
import java.util.List;


public class NotificationFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate( R.layout.fragment_notification, container, false );
        
        recyclerView = view.findViewById( R.id.recycle_view );
        recyclerView.setHasFixedSize( true );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getContext() );
        recyclerView.setLayoutManager( linearLayoutManager );
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter( getContext(),notificationList );
        recyclerView.setAdapter( notificationAdapter );
        
        readNotification();

        return view;
    }

    private void readNotification() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child( firebaseUser.getUid() );
        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    Notification notification = snapshot1.getValue( Notification.class );
                    notificationList.add( notification );
                }

                Collections.reverse( notificationList );
                notificationAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
}