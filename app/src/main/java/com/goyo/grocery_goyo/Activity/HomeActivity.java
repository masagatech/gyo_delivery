package com.goyo.grocery_goyo.Activity;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.goyo.grocery.R;
import com.goyo.grocery_goyo.Adapters.CustomResturantAdapter;
import com.goyo.grocery_goyo.AppLocationService;
import com.goyo.grocery_goyo.Global.global;
import com.goyo.grocery_goyo.LocalDB.UserDbHelper;
import com.goyo.grocery_goyo.SearchLocation;
import com.goyo.grocery_goyo.model.RestaurantsTimings;
import com.goyo.grocery_goyo.model.restaurantModel;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
public class HomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final int PERMS_REQUEST_CODE=123;
    public static ListView resturant_list;
    public static String unique_id;
    private Button search,btn_refresh;
    private ImageView filterOption;
    public TextView txtLocation, txtLocDesc;
    private LinearLayout layout_location;
    private SearchView etSearchRestaurants;
    RestaurantsTimings resTime;
    Context context;
    public ActionBar action;
    String addressLine, newAddress;
    AppLocationService appLocationService;
    SharedPreferences settings;
    CustomResturantAdapter resturantAdapter = null;
    List<restaurantModel> myList;
    List<RestaurantsTimings> resTimings;
    public String c1, c2, o1, o2;
    Intent io;
    UserDbHelper userDbHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        unique_id=UUID.randomUUID().toString().replace("-","");
        context = this;

        if(hasPermission())
        {
           //Our App has permissions
        }
        else
        {
            //Our App Does not have permissions
            requestPermissions();
        }
        if(haveNetworkConnection(HomeActivity.this)==false)
        {
           // showInternetAlertDialog(HomeActivity.this).show();

            getSupportActionBar().hide();
            View view=getLayoutInflater().inflate(R.layout.layout_no_internet_resource_1,null);
            setContentView(view);
            Button btn=(Button)view.findViewById(R.id.btn_refresh);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(haveNetworkConnection(context)==true)
                    {
                        startActivity(new Intent(context,HomeActivity.class));
                    }
                }
            });
         }
        else
        {
            userDbHelper =new UserDbHelper(this);
            db=userDbHelper.getWritableDatabase();
            userDbHelper.DeleteDetails(db);
            userDbHelper.DeleteCartDetails(db);
            io = getIntent();
            newAddress = SearchLocation.address;
            appLocationService = new AppLocationService(this);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            filterOption = (ImageView) findViewById(R.id.imageFilter);
            filterOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), FilterScreen.class));
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            });
            etSearchRestaurants = (SearchView) findViewById(R.id.searchRestaurants);
            global.resturantNames = new ArrayList<>();
            resTimings = new ArrayList<RestaurantsTimings>();
            settings = context.getSharedPreferences("PREF_BILL", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("Total Amount", 0);
            editor.putInt("CurrentCart", 0);
            editor.commit();
            resturant_list = (ListView) findViewById(R.id.list_display_resturants);
            InitAppBar();
            //Helps to set the details of user current location
            if (appLocationService.canGetLocation()) {
                if (newAddress == null) {
                    addressLine = String.valueOf(appLocationService.getLocality(this));
                    txtLocation.setText(appLocationService.getAddressLineOne(this));
                    txtLocDesc.setText(appLocationService.getAddressLineTwo(this) + "," + appLocationService.getLocality(this));
                } else {
                    txtLocation.setText(io.getStringExtra("Area"));
                    txtLocDesc.setText(io.getStringExtra("AddressLine"));
                }
            } else {
                appLocationService.showSettingsAlert();
            }
            getRestaurant();
            etSearchRestaurants.setOnQueryTextListener(this);
        }
   }
    private void getRestaurant() {
        final JsonObject json = new JsonObject();
        json.addProperty("flag", "all");
        Ion.with(context)
                .load("http://35.154.230.244:8085/getRestaurantMaster")
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (result != null) {
                            Gson gson = new Gson();
                            myList = gson.fromJson(result.get("data"), new TypeToken<List<restaurantModel>>() {
                            }.getType());
                            for (int i = 0; i < myList.size(); i++) {
                                // Toast.makeText(context, result.get("data").getAsJsonArray().get(i).getAsJsonObject().get("tm").getAsJsonArray().get(0).getAsJsonObject().toString(), Toast.LENGTH_LONG).show();
                                // Toast.makeText(context, result.get("data").getAsJsonArray().get(i).getAsJsonObject().get("tm").getAsJsonArray().toString(), Toast.LENGTH_LONG).show();
                                // Toast.makeText(context, result.get("data").getAsJsonArray().get(i).getAsJsonObject().get("tm").getAsJsonArray().get(0).getAsJsonObject().get("c1").getAsString(), Toast.LENGTH_LONG).show();
                                c1 = result.get("data").getAsJsonArray().get(i).getAsJsonObject().get("tm").getAsJsonArray().get(0).getAsJsonObject().get("c1").getAsString();
                                c2 = result.get("data").getAsJsonArray().get(i).getAsJsonObject().get("tm").getAsJsonArray().get(0).getAsJsonObject().get("c2").getAsString();
                                o1 = result.get("data").getAsJsonArray().get(i).getAsJsonObject().get("tm").getAsJsonArray().get(0).getAsJsonObject().get("o1").getAsString();
                                o2 = result.get("data").getAsJsonArray().get(i).getAsJsonObject().get("tm").getAsJsonArray().get(0).getAsJsonObject().get("o2").getAsString();
                                resTime = new RestaurantsTimings(c1, c2, o1, o2);
                                resTimings.add(resTime);
                            }
                            resturantAdapter = new CustomResturantAdapter(HomeActivity.this, myList,resTimings);
                            resturant_list.setAdapter(resturantAdapter);
                        }
                    }
                });
    }
    //A method to get GPS provider
    public void InitAppBar() {
        ActionBar action = getSupportActionBar();
        action.setDisplayShowCustomEnabled(true);
        action.setCustomView(R.layout.layout_location_select);
        //Initializing both the textView to display current Location
        txtLocation = (TextView) action.getCustomView().findViewById(R.id.txtLocation);
        txtLocDesc = (TextView) action.getCustomView().findViewById(R.id.txtLocDesc);
        layout_location = (LinearLayout) action.getCustomView().findViewById(R.id.layout_location_select);
        //Creating Listener of Layout of action bar to go in next fragment for searching location
        layout_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchLocation.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
            resturantAdapter.filter(text);
        return false;
    }
    //getRestaurantMaster
    //flag = 'all'
    private  boolean haveNetworkConnection(Context context)
    {
        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo=cm.getActiveNetworkInfo();
        if(netinfo!=null && netinfo.isConnectedOrConnecting())
        {
            android.net.NetworkInfo wifi=cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile=cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if((mobile!=null && mobile.isConnectedOrConnecting()) || (wifi!=null && wifi.isConnectedOrConnecting()))
            {
              return true;
            }
            else
            {
                return false;
            }
        }
        else
        return false;
    }
    private AlertDialog.Builder showInternetAlertDialog(Context c)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Application Requires Internet Connection....")
                .setCancelable(false)
                .setPositiveButton("Connect to Wifi or Mobile Internet", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
              /*  .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.exit(1);
                    }
                });
              */return builder;
    }
    private boolean hasPermission()
    {
        int res=0;
        String permissions[]=new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        for(String params:permissions)
        {
            res=checkCallingOrSelfPermission(params);
            if(!(res== PackageManager.PERMISSION_GRANTED));
            {
                return false;
            }
        }
       return true;

    }
    private void requestPermissions()
    {
        String permissions[]=new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            requestPermissions(permissions,PERMS_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allowed=true;
        switch (requestCode)
        {
            case PERMS_REQUEST_CODE:
                for(int res:grantResults)
                {
                    // If User granted all the permissions
                    allowed=allowed && (res==PackageManager.PERMISSION_GRANTED);
                }
                break;

            default:
                //if user not granted permission
                allowed=false;
                break;
        }
        if(allowed)
        {
            //user granted all permissions we can perform our task
        }
        else
        {
            //we will give warning to user that they havent granted permissions
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
            {
                Toast.makeText(this,"Location permission denied.",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

