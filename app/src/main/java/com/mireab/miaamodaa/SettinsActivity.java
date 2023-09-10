package com.mireab.miaamodaa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.mireab.miaamodaa.Prevalent.Prevalent;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettinsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, addressEditText;
    private TextView profileChangeTextBtn, closeTextBtn, saveTextButton;

    private Uri imageUri;
    private String my = "";
    private StorageReference storageReference;
    private String checker = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settins);



        profileImageView = (CircleImageView) findViewById(R.id.settings_profile_image);
        fullNameEditText = (EditText) findViewById(R.id.settings_full_name);
        userPhoneEditText = (EditText) findViewById(R.id.settings_phone_number);
        addressEditText = (EditText) findViewById(R.id.settings_address);
        profileChangeTextBtn = (TextView) findViewById(R.id.profile_image_change_btn);
        closeTextBtn = (TextView) findViewById(R.id.close_settings_btn);
        saveTextButton = (TextView) findViewById(R.id.uptate_account_settings_btn);


        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, addressEditText);


        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checker.equals("clicked")){

                    userInfoSaved();

                }

                else {

                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checker = "clicked";

                /// CropImage.activity(imageUri)
                //  .setAspectRatio(1,1)
                //       .start(SettinsActivity.this);

            }
        });
    }

    private void updateOnlyUserInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name",fullNameEditText.getText().toString());
        userMap.put("address",addressEditText.getText().toString());
        userMap.put("phoneOrder",userPhoneEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);


        startActivity(new Intent(SettinsActivity.this,HomeActivity.class));
        Toast.makeText(SettinsActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //  if (requestCode==CropImage.)
    }

    private void userInfoSaved() {

        if (TextUtils.isEmpty(fullNameEditText.getText().toString())){

            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(addressEditText.getText().toString())){

            Toast.makeText(this, "Name is address.", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(userPhoneEditText.getText().toString())){

            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        }

        else if (checker.equals("clicked")){

            uploadImage();
        }


    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update profile");
        progressDialog.setMessage("Please wait while we are updating your account inform");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (fullNameEditText !=null){

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("name",fullNameEditText.getText().toString());
            userMap.put("address",addressEditText.getText().toString());
            userMap.put("phoneOrder",userPhoneEditText.getText().toString());
            userMap.put("image", my );
            ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

            progressDialog.dismiss();

            startActivity(new Intent(SettinsActivity.this,HomeActivity.class));
            Toast.makeText(SettinsActivity.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
            finish();



            //  final StorageReference fileRef = storageP
        }


        else {
            progressDialog.dismiss();
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }



    }

    private void userInfoDisplay(CircleImageView profileImageView, EditText fullNameEditText, EditText userPhoneEditText, EditText addressEditText) {

        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    if (snapshot.child("name").exists()){

                        try {

                            //     String image = snapshot.child("image").getValue().toString();
                            String name = snapshot.child("name").getValue().toString();
                            String phone = snapshot.child("phone").getValue().toString();
                            String address = snapshot.child("address").getValue().toString();

                            //   Picasso.get().load(image).into(profileImageView);
                            fullNameEditText.setText(name);
                            userPhoneEditText.setText(phone);
                            addressEditText.setText(address);

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}