package com.example.ecommreceapp25_5_2022.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecommreceapp25_5_2022.Adapters.CategoryAdapter;
import com.example.ecommreceapp25_5_2022.Adapters.ProductAdapter;
import com.example.ecommreceapp25_5_2022.Models.Category;
import com.example.ecommreceapp25_5_2022.Models.Product;
import com.example.ecommreceapp25_5_2022.Utilities.Constant;
import com.example.ecommreceapp25_5_2022.databinding.ActivityMainBinding;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    CategoryAdapter categoryAdapter;
    ArrayList<Category> categories;

    ProductAdapter productAdapter;
    ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.searchBar.setOnSearchActionListener(new SimpleOnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {


                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query",text.toString());
                startActivity(intent);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        initCategories();
        initProducts();
        initSlider();

    }

    private void initSlider() {
        /*
        binding.carousel.addData(new CarouselItem("https://img.freepik.com/free-psd/digital-marketing-facebook-banner-template_237398-233.jpg?w=2000","Some caption here"));
        binding.carousel.addData(new CarouselItem("https://img.freepik.com/free-psd/headphone-brand-product-sale-facebook-cover-banner_161103-93.jpg?w=2000","Some caption here"));
        binding.carousel.addData(new CarouselItem("https://images.remotehub.com/d42c62669a7711eb91397e038280fee0/original_thumb/ec1eb042.jpg?version=1618112516","Some caption here"));
*/
        getRecentOffers();

    }


    void initCategories(){


        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this,categories);
        //categories.add(new Category("spspspps","oifsauoif","#FF018786","ofidufios",2));

        getCategories();


        GridLayoutManager layoutManager  = new GridLayoutManager(this,4);
        binding.categoriesList.setLayoutManager(layoutManager);
        binding.categoriesList.setAdapter(categoryAdapter);
    }

    void getCategories(){

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest  request = new StringRequest(Request.Method.GET, Constant.GET_CATEGORIES_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.e("err",response);
                    JSONObject mainObj = new JSONObject(response);
                    if (mainObj.getString("status").equals("success")){
                        JSONArray categoriesArray =  mainObj.getJSONArray("categories");
                        for (int i = 0; i < categoriesArray.length(); i++){
                            JSONObject object = categoriesArray.getJSONObject(i);

                            Category category = new Category(
                                    object.getString("name"),
                                    Constant.CATEGORIES_IMAGE_URL + object.getString("icon"),
                                    object.getString("color"),
                                    object.getString("brief"),
                                    object.getInt("id")
                            );
                            categories.add(category);
                        }
                        categoryAdapter.notifyDataSetChanged();
                    }
                    else {

                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    void getRecentProduct(){
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constant.GET_PRODUCTS_URL + "?count=10";
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

    void getRecentOffers(){
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, Constant.GET_OFFERS_URL, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONArray offerArray = object.getJSONArray("news_infos");
                    for (int i = 0; i < offerArray.length(); i++){
                        JSONObject childObj = offerArray.getJSONObject(i);
                        binding.carousel.addData(
                                new CarouselItem(
                                        Constant.NEWS_IMAGE_URL + childObj.getString("image"),
                                        childObj.getString("title")
                                )
                        );
                    }
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }, error -> {

        });
        queue.add(request);
    }

    void initProducts(){
        products = new ArrayList<>();

        productAdapter = new ProductAdapter(this,products);

        getRecentProduct();
/*
        products.add(new Product("korean short cowboy shorts","https://media.istockphoto.com/photos/male-coat-isolated-on-the-white-picture-id163208487?k=20&m=163208487&s=612x612&w=0&h=TZ5XnBejf_EAnGjMPfsRf3zu-8G9DYHIFyTiyrnwFms=","",34999,12,1,1));
        products.add(new Product("korean short cowboy shorts","https://media.istockphoto.com/photos/male-coat-isolated-on-the-white-picture-id163208487?k=20&m=163208487&s=612x612&w=0&h=TZ5XnBejf_EAnGjMPfsRf3zu-8G9DYHIFyTiyrnwFms=","",34999,12,1,1));
        products.add(new Product("korean short cowboy shorts","https://media.istockphoto.com/photos/male-coat-isolated-on-the-white-picture-id163208487?k=20&m=163208487&s=612x612&w=0&h=TZ5XnBejf_EAnGjMPfsRf3zu-8G9DYHIFyTiyrnwFms=","",34999,12,1,1));
        products.add(new Product("korean short cowboy shorts","https://media.istockphoto.com/photos/male-coat-isolated-on-the-white-picture-id163208487?k=20&m=163208487&s=612x612&w=0&h=TZ5XnBejf_EAnGjMPfsRf3zu-8G9DYHIFyTiyrnwFms=","",34999,12,1,1));
        products.add(new Product("korean short cowboy shorts","https://media.istockphoto.com/photos/male-coat-isolated-on-the-white-picture-id163208487?k=20&m=163208487&s=612x612&w=0&h=TZ5XnBejf_EAnGjMPfsRf3zu-8G9DYHIFyTiyrnwFms=","",34999,12,1,1));
        products.add(new Product("korean short cowboy shorts","https://media.istockphoto.com/photos/male-coat-isolated-on-the-white-picture-id163208487?k=20&m=163208487&s=612x612&w=0&h=TZ5XnBejf_EAnGjMPfsRf3zu-8G9DYHIFyTiyrnwFms=","",34999,12,1,1));
        products.add(new Product("korean short cowboy shorts","https://media.istockphoto.com/photos/male-coat-isolated-on-the-white-picture-id163208487?k=20&m=163208487&s=612x612&w=0&h=TZ5XnBejf_EAnGjMPfsRf3zu-8G9DYHIFyTiyrnwFms=","",34999,12,1,1));
        products.add(new Product("korean short cowboy shorts","https://media.istockphoto.com/photos/male-coat-isolated-on-the-white-picture-id163208487?k=20&m=163208487&s=612x612&w=0&h=TZ5XnBejf_EAnGjMPfsRf3zu-8G9DYHIFyTiyrnwFms=","",34999,12,1,1));
*/

        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        binding.productList.setLayoutManager(layoutManager);
        binding.productList.setAdapter(productAdapter);
    }
}