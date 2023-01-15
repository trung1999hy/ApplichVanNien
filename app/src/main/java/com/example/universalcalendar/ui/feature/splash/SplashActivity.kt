package com.example.universalcalendar.ui.feature.splash

import android.content.Intent
import android.os.Handler
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.ActivitySplashBinding
import com.example.universalcalendar.ui.HomeActivity
import com.example.universalcalendar.ui.base.BaseActivity


class SplashScreenActivity : BaseActivity<ActivitySplashBinding>() {


    override fun getLayoutId() = R.layout.activity_splash

    override fun initView() {
        Handler().postDelayed({
            val intent = Intent(this@SplashScreenActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000L)
    }

    override fun onBackPressed() {
        //prevent back press
    }

}