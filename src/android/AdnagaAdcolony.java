package com.adnaga;

import org.apache.cordova.*;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyAd;
import com.jirbo.adcolony.AdColonyAdAvailabilityListener;
import com.jirbo.adcolony.AdColonyAdListener;
import com.jirbo.adcolony.AdColonyVideoAd;

public class AdnagaAdcolony implements IPlugin {
    private static final String LOG_TAG = "Adnaga-Adcolony";

    private Adnaga _adnaga;

    public String getNetworkName() {
        return "adcolony";
    }

    public void init(String pid, Adnaga adnaga) {
        _adnaga = adnaga;
        Log.w(LOG_TAG, "adcolony ads is enabled. initing. appId_zoneId=" + pid);
        if ((pid != null) && (!"".equals(pid)) && (!"null".equals(pid))) {
            String[] tokens = pid.split("_");
            AdColony.configure(_adnaga.getActivity(), "version:1.0,store:google", tokens[0] /* appid */, tokens[1] /* zoneid */);
            AdColony.addAdAvailabilityListener(new AdColonyListener());
        }
        Log.i(LOG_TAG, "admaga-adcolony inited");
    }

    public void loadAds(final String pid) {
        Log.e(LOG_TAG, "admaga-adcolony is autoReload, so loadAds should not be called");
    }

    public void showAds(final CallbackContext callbackContext) {
        Log.i(LOG_TAG, "Trying to show adcolony ads");
        AdColonyVideoAd ad = new AdColonyVideoAd().withListener(new AdColonyListener());
        ad.show();
    }

    public void onPause() {
        AdColony.pause();
        Log.i(LOG_TAG, "Called AdColony.pause()");
    }

    public void onResume() {
        AdColony.resume(_adnaga.getActivity());
        Log.i(LOG_TAG, "Called AdColony.resume(activity)");
    }

    private class AdColonyListener implements AdColonyAdAvailabilityListener, AdColonyAdListener {
        @Override
        public void onAdColonyAdAvailabilityChange(boolean b, String s) {
            Log.i(LOG_TAG, "adcolony AdAvailabilityChange " + b + " " + s);
            if (b) {
                _adnaga.sendAdsEventToJs("adcolony", "READY", "");
            }
        }

        @Override
        public void onAdColonyAdAttemptFinished(AdColonyAd ad)
        {
            // Can use the ad object to determine information about the ad attempt:
            // ad.shown();
            // ad.notShown();
            // ad.canceled();
            // ad.noFill();
            // ad.skipped();
            if (ad.shown()) {
                _adnaga.sendAdsEventToJs("adcolony", "FINISH", "");
            }
        }

        @Override
        public void onAdColonyAdStarted(AdColonyAd ad)
        {
            _adnaga.sendAdsEventToJs("adcolony", "START", "");
        }
    }
}