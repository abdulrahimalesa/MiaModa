package com.mireab.miaamodaa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mireab.miaamodaa.Model.Products;
import com.mireab.miaamodaa.Prevalent.Prevalent;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    TextView numberOrderTxt;
    int totalnumberOrderTxt = 1;

    Button pd_add_to_cart_button;
    private ImageView productImage, plusBtn, minusBtn;
    private TextView productPrice, productDescription, productName;
    private String productID = "", state = "Normal";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);


        productID = getIntent().getStringExtra("pid");


        pd_add_to_cart_button = (Button) findViewById(R.id.pd_add_to_cart_button);
        productImage = (ImageView) findViewById(R.id.product_image_details);
        plusBtn = (ImageView) findViewById(R.id.plusBtn);
        minusBtn = (ImageView) findViewById(R.id.minusBtn);
        productName = (TextView) findViewById(R.id.product_name_details);
        productDescription = (TextView) findViewById(R.id.product_description_details);
        productPrice = (TextView) findViewById(R.id.product_price_details);

        numberOrderTxt = (TextView) findViewById(R.id.numberOrderTxt);


        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (totalnumberOrderTxt < 10){
                    totalnumberOrderTxt++;
                    numberOrderTxt.setText(String.valueOf(totalnumberOrderTxt));

                }
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (totalnumberOrderTxt > 0){
                    totalnumberOrderTxt--;
                    numberOrderTxt.setText(String.valueOf(totalnumberOrderTxt));
                }
            }

        });

        pd_add_to_cart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (state.equals("Order Placed") || state.equals("Order Shipped")){

                    Toast.makeText(ProductDetailsActivity.this, "you can add purchase more prodits, once your order is shipped or confirmed", Toast.LENGTH_LONG).show();
                }
                else {

                    addingToCartList();

                }
            }
        });

        getProdutDetails(productID);

    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();
    }

    private void addingToCartList() {

        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH : mm , ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", numberOrderTxt.getText().toString());
        cartMap.put("discount", "");

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                .child("Products").child(productID)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone())
                                    .child("Products").child(productID)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){
                                                Toast.makeText(ProductDetailsActivity.this, "Added to Cart List.", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                                startActivity(intent);

                                            }

                                        }
                                    });
                        }

                    }
                });



    }



    private void getProdutDetails(String productID) {

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    Products products = snapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productPrice.setText(products.getPrice());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CheckOrderState(){

        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhone());

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String shippngState = snapshot.child("state").getValue().toString();

                    if (shippngState.equals("shipped")){

                        state = "Ordder Shipped";

                    }

                    else if (shippngState.equals("not shipped")){

                        state = "Ordder Placed";

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}