package com.goyo.grocery_goyo.Adapters;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static butterknife.ButterKnife.findById;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.goyo.grocery.R;
import com.goyo.grocery_goyo.Activity.HomeActivity;
import com.goyo.grocery_goyo.Activity.ResturantProfile;
import com.goyo.grocery_goyo.model.RestaurantsTimings;
import com.goyo.grocery_goyo.model.restaurantModel;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CustomResturantAdapter extends BaseAdapter {
    Context context;
    Holder h1;
    private List<restaurantModel> x;
    private List<RestaurantsTimings> resTimings;
    private ArrayList<restaurantModel> arrayRestaurantModel;
    restaurantModel resturant;
    RestaurantsTimings resTime;
    //Created Shared Preferences at app level to store resturant_id of particular resturant
    private final String PREF_NAME = "Resturant_id";
    public static Double MinOrder;
    SharedPreferences settings;
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
    Date currentLocalTime = cal.getTime();
    DateFormat sdf = new SimpleDateFormat("hh:mm:ss");
    public CustomResturantAdapter(HomeActivity activity, List<restaurantModel> xyz, final List<RestaurantsTimings> resTimings) {
        context = activity;
        x = xyz;
        this.resTimings = resTimings;
        this.arrayRestaurantModel = new ArrayList<restaurantModel>();
        this.arrayRestaurantModel.addAll(x);
        //Created Listener of resturant list because resturant_id can be easily availaible from the webservice
        HomeActivity.resturant_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                TextView txtResturant = (TextView) view.findViewById(R.id.txtResturantName);
                Intent io = new Intent(context, ResturantProfile.class);
                io.putExtra("resturantName", txtResturant.getText().toString());
                settings = context.getSharedPreferences("PREF_NAME", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("Resturant_id", resturant.restid);
                MinOrder = (Double) x.get(position).min_order;
                try {
                    ValidateDate(resTimings.get(position).o1,resTimings.get(position).c1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Toast.makeText(context,resTimings.get(position).o1,Toast.LENGTH_LONG).show();
                editor.commit();
                context.startActivity(io);
            }
        });
    }
    public int getCount() {
        return x.size();
    }

    public class Holder {
        public
        ImageView imgDisplay;
        TextView txtRname, txtRtype, txtVegNonVeg, txtRating, txtDeliveryTime, txtMinimumOrderValue;
        TextView txtMoriningTime, txtEvening;
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        h1 = new Holder();
        //View rowView;
        LayoutInflater mInflater = null;
        if (convertView == null) {
            mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.layout_list_resturant, null);

        }
        h1.imgDisplay = (ImageView) convertView.findViewById(R.id.imgResturantDisplay);
        h1.txtRname = (TextView) convertView.findViewById(R.id.txtResturantName);
        h1.txtRtype = (TextView) convertView.findViewById(R.id.txtResturantType);
        h1.txtVegNonVeg = (TextView) convertView.findViewById(R.id.txtExpenseCategory);
        h1.txtRating = (TextView) convertView.findViewById(R.id.txtRating);
        h1.txtDeliveryTime = (TextView) convertView.findViewById(R.id.txtDeliveryTime);
        h1.txtMinimumOrderValue = (TextView) convertView.findViewById(R.id.txtSetMinimumPrice);
        h1.txtMoriningTime = (TextView) convertView.findViewById(R.id.txtMorningTime);
        h1.txtEvening = (TextView) convertView.findViewById(R.id.txtEveningTime);
        resturant = x.get(position);
        resTime = resTimings.get(position);
        h1.txtRname.setText(x.get(position).getRestname());
        h1.txtRtype.setText(x.get(position).getAdr());
        //Below is the code
        if (x.get(position).getResttype().equals("Veg")) {
            h1.txtVegNonVeg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_veg_resturant_icon, 0, 0, 0);
        } else if (x.get(position).getResttype().equals("Veg/Nonveg")) {
            h1.txtVegNonVeg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.veg_icon_nonveg_icon, 0, 0, 0);
        }
        h1.txtRating.setText(x.get(position).getRating());
        h1.txtMinimumOrderValue.setText(String.valueOf("Minimum Order" + "  " + "₹" + x.get(position).getMin_order()));
        h1.txtDeliveryTime.setText("20");
        h1.txtMoriningTime.setText("Morining Time:"+resTime.getO1()+"AM"+"  TO  "+resTime.getC1()+"AM");
        h1.txtEvening.setText("Evening Time:"+resTime.getO2()+"PM"+"  TO  "+resTime.getC2()+"PM");
        // GetRestaurantTimings(position);
        return convertView;
    }
    public void ValidateDate(String date1,String date2) throws ParseException {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        Date currentLocalTime = cal.getTime();
        Date t1=parseDate(date1);
        DateFormat date = new SimpleDateFormat("hh:mm");
      // you can get seconds by adding  "...:ss" to it
        date.setTimeZone(android.icu.util.TimeZone.getTimeZone("GMT+5:30"));
        String localTime = date.format(currentLocalTime);
        Toast.makeText(context,localTime,Toast.LENGTH_LONG).show();
    }
  private  Date parseDate(String date)
    {
        try
        {
            return  sdf.parse(date);
        }
        catch (java.text.ParseException e)
        {
             return  new Date(0);
        }
    }
    //Code to implement Search Functionality for the search of Restaurants in that particular area
    public void filter(String charText)
    {
        charText = charText.toString().toLowerCase(Locale.getDefault());
        x.clear();
        if (charText.length() == 0) {
            x.addAll(arrayRestaurantModel);
        } else {

            for (restaurantModel res : arrayRestaurantModel) {
                if (res.getRestname().toLowerCase(Locale.getDefault()).contains(charText)) {
                    x.add(res);
                }
            }
        }
        notifyDataSetChanged();
    }
}
//Android code to call web api to get the timings of the restaurants and set it in appropriate view.
/*
 private void GetRestaurantTimings(final int position) {
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
                        Gson gson = new Gson();
                          openingTime = result.get("data").getAsJsonArray().get(position).getAsJsonObject().get("tm").getAsJsonArray().get(0).getAsJsonObject().get("o1").getAsString();
                       closingTime = result.get("data").getAsJsonArray().get(position).getAsJsonObject().get("tm").getAsJsonArray().get(0).getAsJsonObject().get("c1").getAsString();
                          h1.txtMoriningTime.setText("Morning Time:"+openingTime+"AM"+" TO "+closingTime+"AM");
                        openingTime1 = result.get("data").getAsJsonArray().get(position).getAsJsonObject().get("tm").getAsJsonArray().get(0).getAsJsonObject().get("o2").getAsString();
                         closingTime1 = result.get("data").getAsJsonArray().get(position).getAsJsonObject().get("tm").getAsJsonArray().get(0).getAsJsonObject().get("c2").getAsString();
                         h1.txtEvening.setText("Evening Time:"+openingTime1+"PM"+" TO "+closingTime1+"PM");
                    }
                });
     }
}
*/
