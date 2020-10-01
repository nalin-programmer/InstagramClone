package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    Uri imageUri;
    String myUrl;
    StorageTask uploadTask;
    StorageReference storageReference;

    ImageView close,image_added;
    TextView post;
    EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_post );

        close = findViewById( R.id.close );
        image_added = findViewById( R.id.image_added );
        post = findViewById( R.id.post );
        description = findViewById( R.id.description );

        storageReference = FirebaseStorage.getInstance().getReference("posts");

        close.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( PostActivity.this,MainActivity.class ) );
                finish();
            }
        } );

        post.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        } );

        CropImage.activity()
                .setAspectRatio( 1,1 )
                .start(PostActivity.this);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType( contentResolver.getType( uri ) );
    }

    private void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog( this );
        progressDialog.setMessage( "posting" );
        progressDialog.show();

        if(imageUri!= null){
            final StorageReference filereference = storageReference.child( System.currentTimeMillis()
            +"."+getFileExtension( imageUri ));
            uploadTask = filereference.putFile( imageUri );

            uploadTask = filereference.putFile( imageUri );
            uploadTask.continueWithTask( new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filereference.getDownloadUrl();
                }
            } ).addOnCompleteListener( new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String postid = reference.push().getKey();
                        //Log.i("username",postid);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put( "postid",postid );
                        hashMap.put( "postimage",myUrl);
                        hashMap.put("description",description.getText().toString());
                        hashMap.put( "publisher", FirebaseAuth.getInstance().getCurrentUser().getUid() );

                        reference.child( postid ).setValue( hashMap );

                        progressDialog.dismiss();

                        startActivity( new Intent( PostActivity.this,MainActivity.class ) );
                        finish();
                    }else{
                        Toast.makeText( PostActivity.this,"Failed",Toast.LENGTH_SHORT ).show();
                    }
                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( PostActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT ).show();
                }
            } );
        }else{
            Toast.makeText( PostActivity.this,"No Image selected!",Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult( data );
            imageUri = result.getUri();

            image_added.setImageURI( imageUri );
        }else {
            Toast.makeText( PostActivity.this,"Something went wrong!",Toast.LENGTH_SHORT ).show();
            startActivity( new Intent( PostActivity.this,MainActivity.class ) );
            finish();
        }
    }
}