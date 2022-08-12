package com.example.matecarapp.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.matecarapp.access.TravellerBookingAccess;

public class CancelReceiver extends BroadcastReceiver {
    private TravellerBookingAccess mTravellerBookingAccess;

    @Override
    public void onReceive(Context context, Intent intent) {

        String idTraveller = intent.getExtras().getString("idTraveller");
        mTravellerBookingAccess = new TravellerBookingAccess();
        mTravellerBookingAccess.updateStatus(idTraveller, "cancel");


        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);

    }
}
