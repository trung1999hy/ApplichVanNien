package com.example.universalcalendar.ui

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.ActivityHomeBinding
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
            R.id.day_fragment -> {}
            R.id.month_fragment -> {}
            R.id.event_fragment-> {}
            R.id.setting_fragment -> {}
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

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return true
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            R.id.day_fragment -> finish()
            else -> navController.navigateUp()
        }
    }

}