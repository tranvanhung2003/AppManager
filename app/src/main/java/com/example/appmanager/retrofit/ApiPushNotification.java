package com.example.appmanager.retrofit;

import com.example.appmanager.model.NotiResponse;
import com.example.appmanager.model.NotiSendData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiPushNotification {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAIhcTawE:APA91bGnA9KrxmvYk3ehpwoa65E5eZzjObM7NdovktJeIJTywnkzW33g8xxeFTN8ODNHhJreZ9ANcQDByOKxoI2fL352BFlLaNw4zQ1Dw6M3W38_eYGmV8IaoyZPo6wR_XbyDHUWfoZn"
            }
    )
    @POST("fcm/send")
    Observable<NotiResponse> sendNotification(@Body NotiSendData data);
}
