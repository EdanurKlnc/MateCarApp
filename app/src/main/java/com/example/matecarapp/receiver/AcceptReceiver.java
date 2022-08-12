package com.example.matecarapp.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.matecarapp.access.AuthAccess;
import com.example.matecarapp.access.GeofireAccess;
import com.example.matecarapp.access.TravellerBookingAccess;
import com.example.matecarapp.activity.driver.MapDriverBookingActivity;

public class AcceptReceiver  extends BroadcastReceiver {

    private TravellerBookingAccess mTravellerBookingAccess;
    private GeofireAccess mGeofireAccess;
    private AuthAccess mAuthAccess;
    @Override
    public void onReceive(Context context, Intent intent) {
        mAuthAccess = new AuthAccess();
        mGeofireAccess = new GeofireAccess("active_driver");
        mGeofireAccess.removeLocation(mAuthAccess.getId());

        String idTraveller = intent.getExtras().getString("idTraveller");
        mTravellerBookingAccess = new TravellerBookingAccess();
        mTravellerBookingAccess.updateStatus(idTraveller,"accept");


        NotificationManager manager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);

        Intent intent1 = new Intent(context, MapDriverBookingActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        intent1.putExtra("idTraveller",idTraveller);

        context.startActivity(intent1);



    }
}
