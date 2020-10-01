package com.example.instagramclone.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagramclone.Fragment.PostDetailFragment;
import com.example.instagramclone.Model.Post;
import com.example.instagramclone.R;

import java.util.List;

public class MyFotoAdapter extends RecyclerView.Adapter<MyFotoAdapter.ViewHolder>{

    private Context context;
    private List<Post> mPosts;

    public MyFotoAdapter(Context context, List<Post> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( context ).inflate( R.layout.fotos_item, parent,false );
        return new MyFotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Post post = mPosts.get( position );

        Glide.with( context ).load( post.getPostimage() ).into( holder.post_image );

        holder.post_image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences( "PREFS", Context.MODE_PRIVATE ).edit();
                editor.putString( "postid",post.getPostid() );
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                        new PostDetailFragment() ).commit();
            }
        } );

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super( itemView );

            post_image = itemView.findViewById( R.id.post_image );
        }
    }
}
