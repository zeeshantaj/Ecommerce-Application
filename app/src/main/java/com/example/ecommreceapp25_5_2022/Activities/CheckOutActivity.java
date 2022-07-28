package com.example.ecommreceapp25_5_2022.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecommreceapp25_5_2022.Adapters.CartAdapter;
import com.example.ecommreceapp25_5_2022.Models.Product;
import com.example.ecommreceapp25_5_2022.Utilities.Constant;
import com.example.ecommreceapp25_5_2022.databinding.ActivityCheckOutBinding;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CheckOutActivity extends AppCompatActivity {

    ActivityCheckOutBinding binding;
    ArrayList<Product> products;
    CartAdapter.CartListener cartListener;
    CartAdapter adapter;
    Cart cart = TinyCartHelper.getCart();

    ProgressDialog progressDialog;

    double totalPrice = 0;
    final int tax = 11;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        products = new ArrayList<>();
        adapter =new CartAdapter(this, products, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.subTotal.setText(String.format("PKR %.2f",cart.getTotalPrice()));

            }
        });



        for (Map.Entry<Item,Integer> item : cart.getAllItemsWithQty().entrySet()){
            Product product = (Product) item.getKey();
            int quantity = item.getValue();
            product.setQuantity(quantity);

            products.add(product);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,layoutManager.getOrientation());
        binding.cartList.setLayoutManager(new LinearLayoutManager(this));
        binding.cartList.addItemDecoration(itemDecoration);
        binding.cartList.setAdapter(adapter);


        binding.subTotal.setText(String.format("PKR %.2f",cart.getTotalPrice()));

        totalPrice = (cart.getTotalPrice().doubleValue() * tax / 100) + cart.getTotalPrice().doubleValue();
        binding.total.setText("PKR " + totalPrice);

        binding.checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processOrder();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void processOrder(){
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject productOrder = new JSONObject();
        JSONObject dataObject = new JSONObject();
        try {

            productOrder.put("buyer",binding.nameBox.getText().toString());
            productOrder.put("address",binding.addressBox.getText().toString());
            productOrder.put("email",binding.emailBox.getText().toString());
            productOrder.put("shipping","");
            productOrder.put("shipping_rate","0.0");
            productOrder.put("shipping_location","");
            productOrder.put("date_ship", Calendar.getInstance().getTimeInMillis());
            productOrder.put("phone",binding.phoneBox.getText().toString());
            productOrder.put("comment",binding.commentBox.getText().toString());
            productOrder.put("status","Waiting");
            productOrder.put("total_fees",totalPrice);
            productOrder.put("tax",tax);
            productOrder.put("serial","cab8cla4e4421a3b");
            productOrder.put("created_at", Calendar.getInstance().getTimeInMillis());
            productOrder.put("updated_at",Calendar.getInstance().getTimeInMillis());
            productOrder.put("last_update",Calendar.getInstance().getTimeInMillis());

            JSONArray product_order_detail = new JSONArray();
            for (Map.Entry<Item,Integer> item : cart.getAllItemsWithQty().entrySet()) {
                Product product = (Product) item.getKey();
                int quantity = item.getValue();
                product.setQuantity(quantity);


                JSONObject productObj = new JSONObject();

                productObj.put("product_id",product.getId());
                productObj.put("product_name",product.getName());
                productObj.put("amount",quantity);
                productObj.put("price_item",product.getPrice());
                product_order_detail.put(productObj);
            }

            dataObject.put("product_order",productOrder);
            dataObject.put("product_order_detail",product_order_detail);

            Log.e("errr",dataObject.toString());
        }
        catch (JSONException e){
            e.printStackTrace();

        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constant.POST_ORDER_URL, dataObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
             try {


                 if (response.getString("status").equals("success")) {
                     Toast.makeText(CheckOutActivity.this, "Success order", Toast.LENGTH_SHORT).show();

                     String orderNumber = response.getJSONObject("data").getString("code");
                     new AlertDialog.Builder(CheckOutActivity.this)
                             .setTitle("Order Successful")
                             .setCancelable(false)
                             .setMessage("Your Order Number is " + orderNumber)
                             .setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {

                                     Intent intent = new Intent(CheckOutActivity.this,PaymentActivity.class);
                                     intent.putExtra("orderCode",orderNumber);
                                     startActivity(intent);
                                 }
                             }).show();

                 } else {
                     new AlertDialog.Builder(CheckOutActivity.this)
                             .setTitle("Order Failed")
                             .setMessage("Something went wrong Please try again.")
                             .setCancelable(false)
                             .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {
                                 }
                             }).show();
                     Toast.makeText(CheckOutActivity.this, "Failed Order", Toast.LENGTH_SHORT).show();
                 }
                 progressDialog.dismiss();
                 Log.e("arr111", response.toString());

             }

             catch (Exception e){
                 e.printStackTrace();
             }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Security","secure_code");
                return headers;
            }
        };
        queue.add(request);
      /*  StringRequest request = new StringRequest(Request.Method.POST, Constant.POST_ORDER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressDialog.dismiss();
            }
        });
        queue.add(request);

       */
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}