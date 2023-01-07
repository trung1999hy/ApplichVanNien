package com.example.universalcalendar.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<V: ViewDataBinding> : AppCompatActivity() {

    private lateinit var mViewDataBinding: V

    val binding: V get() = mViewDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getLayoutId() != 0) {
            mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
            initView()
        }
    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun initView()

}