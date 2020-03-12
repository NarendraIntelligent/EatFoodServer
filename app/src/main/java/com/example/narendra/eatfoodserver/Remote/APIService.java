package com.example.narendra.eatfoodserver.Remote;

import com.example.narendra.eatfoodserver.Model.DataMessage;
import com.example.narendra.eatfoodserver.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by narendra on 3/13/2018.
 */

public interface APIService {
    @Headers({

            "Content-Type:application/json",
            "Authorization:key=AAAAoKr6FHs:APA91bFin05kWeXR-ioyzi1vDs0JkHiegD7njN8cQHWxZcikVXmtth9e_UHxQkb3TBFkzYxsOwnMYVZw-ElgHOdrYfKHb_sppuKOGAgtPbct7K5J2Uf7nBLXz9vB5ujFpmhSny_S57Zj"
    })


    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);

}
