package com.gradius.gradius.Common;

import android.location.Location;

import com.gradius.gradius.Model.User;
import com.gradius.gradius.Remote.FCMClient;
import com.gradius.gradius.Remote.IFCMService;
import com.gradius.gradius.Remote.IGoogleAPI;
import com.gradius.gradius.Remote.RetrofitClient;

/**
 * Created by User on 15/03/2018.
 */

public class Common {

    public static final String driver_tbl = "Drivers";
    public static final String user_driver_tbl = "DriversInfo";
    public static final String user_rider_tbl = "RidersInfo";
    public static final String pickup_request_tbl = "PickupRequest";
    public static final String token_tbl = "Tokens";

    public static User currentUser;

    public static Location mLastLocation=null;

    public static final String baseURL = "https://maps.googleapis.com";
    public static final String fcmURL = "https://fcm.googleapis.com";

    public static IGoogleAPI getGoogleAPI()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    public static IFCMService getFCMService()
    {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
}
