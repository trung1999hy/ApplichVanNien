package com.example.universalcalendar.ui

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.ActivityHomeBinding
import com.example.universalcalendar.extensions.SharePreference
import com.example.universalcalendar.ui.base.BaseActivity
import com.example.universalcalendar.ui.feature.daycalendar.DayCalendarFragment
import com.example.universalcalendar.ui.feature.event.EventFragment
import com.example.universalcalendar.ui.feature.monthcalendar.MonthCalendarFragment
import com.example.universalcalendar.ui.feature.setting.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeActivity : BaseActivity<ActivityHomeBinding>(), NavController.OnDestinationChangedListener {

    private var dayCalendarFragment : DayCalendarFragment? = null
    private var monthCalendarFragment : MonthCalendarFragment? = null
    private var eventFragment : EventFragment? = null
    private var settingFragment : SettingFragment? = null
    private lateinit var navController: NavController

    private var activeFragment: Fragment?= dayCalendarFragment
    private val prefs = SharePreference()
    private var isLogOut = false

    private val navItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            return@OnNavigationItemSelectedListener when (item.itemId) {
                R.id.ic_calendar_daily -> {
                    navigateToDayCalendarScreen()
                    true
                }

                R.id.ic_calendar_monthly -> {
                    navigateToMonthCalendarScreen()
                    true
                }

                R.id.ic_event -> {
                    navigateToEventScreen()
                    true
                }

                R.id.ic_setting -> {
                    navigateToSettingScreen()
                    true
                }

                else -> false
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.bottomNav.circleColor = getColor(R.color.orange)
        }

    }

    override fun getLayoutId() = R.layout.activity_home

    override fun initView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener(this)
        binding.bottomNav.setOnItemSelectedListener(navItemSelectedListener)
        dayCalendarFragment =
            navHostFragment.childFragmentManager.findFragmentById(R.id.day_fragment) as DayCalendarFragment?
        monthCalendarFragment =
            navHostFragment.childFragmentManager.findFragmentById(R.id.month_fragment) as MonthCalendarFragment?
        eventFragment =
            navHostFragment.childFragmentManager.findFragmentById(R.id.event_fragment) as EventFragment?
        settingFragment =
            navHostFragment.childFragmentManager.findFragmentById(R.id.setting_fragment) as SettingFragment?
        binding.bottomNav.selectedItemId = R.id.ic_calendar_daily
    }
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        invalidateOptionsMenu()
        when (destination.id) {
            R.id.day_fragment -> {
                closeFlagToLogout(5000L)
                binding.bottomNav.menu.getItem(0).isChecked = true
                onShowBottonBar(true)
            }
            R.id.month_fragment -> {
                closeFlagToLogout(5000L)
                binding.bottomNav.menu.getItem(1).isChecked = true
                onShowBottonBar(true)
            }
            R.id.event_fragment-> {
                closeFlagToLogout(5000L)
                binding.bottomNav.menu.getItem(2).isChecked = true
                onShowBottonBar(true)
            }
            R.id.setting_fragment -> {
                closeFlagToLogout(5000L)
                binding.bottomNav.menu.getItem(3).isChecked = true
                onShowBottonBar(true)
            }
            R.id.event_register_fragment -> onShowBottonBar(false)
            else -> {}
        }
    }

    private fun navigateToDayCalendarScreen() {
        navController.navigate(R.id.day_fragment)
        activeFragment = dayCalendarFragment
    }

    private fun navigateToMonthCalendarScreen() {
        navController.navigate(R.id.month_fragment)
        activeFragment = monthCalendarFragment
    }

    private fun navigateToEventScreen() {
        navController.navigate(R.id.event_fragment)
        activeFragment = eventFragment
    }

    private fun navigateToSettingScreen() {
        navController.navigate(R.id.setting_fragment)
        activeFragment = settingFragment
    }

    fun navigateToEventSetupScreen() {
        navController.navigate(R.id.event_register_fragment)
    }

    fun onShowBottonBar(isShow: Boolean) {
        binding.bottomNav.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) {
            if (ev.action == MotionEvent.ACTION_DOWN) {
                val v = currentFocus
                if (v is EditText) {
                    val outRect = Rect()
                    v.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                        v.clearFocus()
                        val imm: InputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun closeFlagToLogout(duration: Long) {
        if (!isLogOut) return
        Handler(Looper.getMainLooper()).postDelayed({
            isLogOut = false
        }, duration)
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            R.id.day_fragment,
            R.id.month_fragment,
            R.id.event_fragment,
            R.id.setting_fragment -> {
                if (!isLogOut) {
                    isLogOut = true
                    Handler(Looper.getMainLooper()).postDelayed({
                        isLogOut = false
                    }, 5000L)
                    Toast.makeText(this, "Nhấp 1 lần nữa để thoát app !", Toast.LENGTH_SHORT).show()
                } else finish()
            }
            else -> navController.navigateUp()
        }
    }

}