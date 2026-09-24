package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {
    // Testing ke liye Google ki official test ID (Real publish ke waqt wapas replace kar sakte hain)
    private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    
    // Real ID: "ca-app-pub-4230427204236879/4360396847"

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false

    fun initialize(context: Context) {
        MobileAds.initialize(context) {
            loadInterstitial(context)
        }
    }

    fun loadInterstitial(context: Context) {
        if (interstitialAd != null || isLoading) return
        isLoading = true

        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            AD_UNIT_ID,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                    Log.d("AdManager", "Interstitial Ad Successfully Loaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                    Log.e("AdManager", "Ad Failed to load: ${error.message}")
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, onAdClosed: () -> Unit = {}) {
        activity.runOnUiThread {
            val ad = interstitialAd
            if (ad != null) {
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        interstitialAd = null
                        loadInterstitial(activity)
                        onAdClosed()
                    }

                    override fun onAdFailedToShowFullScreenContent(error: AdError) {
                        interstitialAd = null
                        loadInterstitial(activity)
                        onAdClosed()
                    }
                }
                ad.show(activity)
            } else {
                loadInterstitial(activity)
                onAdClosed()
            }
        }
    }
}
