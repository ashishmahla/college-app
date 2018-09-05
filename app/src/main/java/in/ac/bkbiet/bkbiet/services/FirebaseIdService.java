package in.ac.bkbiet.bkbiet.services;

import com.google.firebase.iid.FirebaseInstanceIdService;

import in.ac.bkbiet.bkbiet.utils.Statics;

/**
 * FIS Created by Ashish on 9/6/2017.
 */

public class FirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        Statics.refreshUserDeviceToken();
    }
}