package com.example.narendra.eatfoodserver.Common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.format.DateFormat;

import com.example.narendra.eatfoodserver.Model.Request;
import com.example.narendra.eatfoodserver.Model.User;
import com.example.narendra.eatfoodserver.Remote.APIService;
import com.example.narendra.eatfoodserver.Remote.FCMRetrofitClient;
import com.example.narendra.eatfoodserver.Remote.IGeoCoordinates;
import com.example.narendra.eatfoodserver.Remote.RetrofitClient;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by narendra on 2/19/2018.
 */

public class Common {
    public static final String SHIPPERS_TABLE = "Shippers";
    public static final String ORDER_NEED_SHIP_TABLE="OrdersNeedShip";
    public static User currentuser;
    public static Request currentRequest;
    public static final String UPDATE="Update";

    public static String topicName="News";

    public static final String DELETE="Delete";
    public static String PHONE_TEXT="userPhone";

    public static final int PICK_IMAGE_REQUEST=71;
    public static final String baseUrl="https://maps.googleapis.com";
    public static final String fcmUrl="https://fcm.googleapis.com";

    public static String convertCodeToStatus(String code) {
        if (code.equals("0"))
            return "placed";
        else if (code.equals("1"))
            return "On my way";
        else if (code.equals("2"))
            return "Shipping";
        else
            return "Shipped";
    }

    public static APIService getFCMClient(){
        return FCMRetrofitClient.getClient(fcmUrl).create(APIService.class);
    }

    public static IGeoCoordinates getGeoCodeService(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }
    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight){
        Bitmap scaledBitmap =Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);
        float scaleX=newWidth/(float)bitmap.getWidth();
        float scaleY=newWidth/(float)bitmap.getHeight();
        float pivoteX=0,pivoteY=0;
        Matrix scaleMatrix=new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivoteX,pivoteY);
        Canvas canvas=new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;

    }
    public static String getDate(long time)
    {
        Calendar calendar=Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date=new StringBuilder(DateFormat.format("dd-MM-yyyy HH:mm",calendar).toString());
        return date.toString();
    }
}
