package com.example.universalcalendar.ui

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.universalcalendar.R
import com.example.universalcalendar.ui.fragment.DayCalendarFragment
import com.example.universalcalendar.ui.fragment.EventFragment
import com.example.universalcalendar.ui.fragment.MonthCalendarFragment
import com.example.universalcalendar.ui.fragment.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity() {
    private val dayCalendarFragment = DayCalendarFragment()
    private val monthCalendarFragment = MonthCalendarFragment()
    private val eventFragment = EventFragment()
    private val settingFragment = SettingFragment()
    private val manager = supportFragmentManager

    private var activeFragment: Fragment = dayCalendarFragment

    private val navItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            return@OnNavigationItemSelectedListener when (item.itemId) {
                R.id.ic_calendar_daily -> {
                    manager.beginTransaction().hide(activeFragment).show(dayCalendarFragment)
                        .commit()
                    activeFragment = dayCalendarFragment
                    true
                }

                R.id.ic_calendar_monthly -> {
                    manager.beginTransaction().hide(activeFragment).show(monthCalendarFragment)
                        .commit()
                    activeFragment = monthCalendarFragment
                    true
                }

                R.id.ic_event -> {
                    manager.beginTransaction().hide(activeFragment).show(eventFragment)
                        .commit()
                    activeFragment = eventFragment
                    true
                }

                R.id.ic_setting -> {
                    manager.beginTransaction().hide(activeFragment).show(settingFragment)
                        .commit()
                    activeFragment = settingFragment
                    true
                }

                else -> false
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bottom_nav.circleColor = getColor(R.color.orange)
        }
    }

    private fun initView() {
        bottom_nav.setOnNavigationItemSelectedListener(navItemSelectedListener)
        manager.beginTransaction().add(R.id.frame_container, dayCalendarFragment, "1").commit()
        manager.beginTransaction().add(R.id.frame_container, monthCalendarFragment, "2")
            .hide(monthCalendarFragment).commit()
        manager.beginTransaction().add(R.id.frame_container, eventFragment, "3")
            .hide(eventFragment).commit()
        manager.beginTransaction().add(R.id.frame_container, settingFragment, "4")
            .hide(settingFragment).commit()
    }
}