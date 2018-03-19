package com.gradius.gradius.Common;

import com.gradius.gradius.Remote.IGoogleAPI;
import com.gradius.gradius.Remote.RetrofitClient;

/**
 * Created by User on 15/03/2018.
 */

public class Common {
    public static final String baseURL = "https://maps.googleapis.com";
    public static IGoogleAPI getGoogleAPI()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }
}
