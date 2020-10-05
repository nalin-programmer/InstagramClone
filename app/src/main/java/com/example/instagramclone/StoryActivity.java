package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Model.Story;
import com.example.instagramclone.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter = 0;
    long pressTime = 0L;
    long limit = 500L;

    StoriesProgressView storiesProgressView;
    ImageView image, story_photo;
    TextView story_username;

    List<String> images;
    List<String> storyids;
    String userid;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit<now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_story );

        storiesProgressView = findViewById( R.id.stories );
        image = findViewById( R.id.image );
        story_photo = findViewById( R.id.story_photo );
        story_username = findViewById( R.id.story_username );

        userid = getIntent().getStringExtra( "userid" );

        getStories( userid );
        userInfo( userid );

        View reverse = findViewById( R.id.reverse );
        reverse.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        } );
        reverse.setOnTouchListener( onTouchListener );

        View skip = findViewById( R.id.skip );
        reverse.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        } );
        skip.setOnTouchListener( onTouchListener );
    }

    @Override
    public void onNext() {
        Glide.with( getApplicationContext() ).load( images.get( ++counter ) ).into( image );

    }

    @Override
    public void onPrev() {
        if((counter -1) < 0)return;
        Glide.with( getApplicationContext() ).load( images.get( --counter ) ).into( image );
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(String userid){
        images = new ArrayList<>(  );
        storyids = new ArrayList<>(  );
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child( userid );
        reference.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                images.clear();
                storyids.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    Story story = snapshot1.getValue( Story.class );
                    long timecurrent = System.currentTimeMillis();
                    if(timecurrent > story.getTimestart() && timecurrent < story.getTimeend()){
                        images.add( story.getImageurl() );
                        storyids.add( story.getStoryid() );
                    }
                }

                storiesProgressView.setStoriesCount( images.size() );
                storiesProgressView.setStoryDuration( 5000L );
                storiesProgressView.setStoriesListener( StoryActivity.this );
                storiesProgressView.startStories(counter);

                Glide.with( getApplicationContext() ).load( images.get( counter ) )
                        .into( image );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void userInfo(final String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child( userid );
        reference.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue( User.class );
                Glide.with( getApplicationContext() ).load( user.getImageurl() ).into( story_photo );
                story_username.setText( user.getUsername() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
}