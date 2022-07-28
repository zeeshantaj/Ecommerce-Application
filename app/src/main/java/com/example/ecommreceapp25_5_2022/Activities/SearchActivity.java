package com.example.ecommreceapp25_5_2022.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecommreceapp25_5_2022.Adapters.ProductAdapter;
import com.example.ecommreceapp25_5_2022.Models.Product;
import com.example.ecommreceapp25_5_2022.Utilities.Constant;
import com.example.ecommreceapp25_5_2022.databinding.ActivitySearchBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;


    ProductAdapter productAdapter;
    ArrayList<Product> products;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        products = new ArrayList<>();

        productAdapter = new ProductAdapter(this,products);

        String query = getIntent().getStringExtra("query");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(query);

        getRecentProduct(query);

        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        binding.productList.setLayoutManager(layoutManager);
        binding.productList.setAdapter(productAdapter);
    }

    void getRecentProduct(String query){
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constant.GET_PRODUCTS_URL + "?q=" + query;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                Log.e("err",response);
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")){
                    JSONArray productArray = object.getJSONArray("products");
                    for (int i = 0; i < productArray.length(); i++){
                        JSONObject childObj = productArray.getJSONObject(i);
                        Product product = new Product(
                                childObj.getString("name"),
                                Constant.PRODUCTS_IMAGE_URL + childObj.getString("image"),
                                childObj.getString("status"),
                                childObj.getDouble("price"),
                                childObj.getDouble("price_discount"),
                                childObj.getInt("stock"),
                                childObj.getInt("id")
                        );
                        products.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }

        }, error -> { });
        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}