package com.example.universalcalendar.ui.base

import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.universalcalendar.ui.HomeActivity

abstract class BaseFragment<B : ViewDataBinding, V : ViewModel> : Fragment() {

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mViewDataBinding: B
    private lateinit var mViewModel: V

    val binding: B get() = mViewDataBinding
    val viewModel: V get() = mViewModel

    private var isBackable: Boolean = false

    protected abstract fun getViewModelClass(): Class<V>

    protected abstract fun getLayoutId(): Int

    protected abstract fun initView()

    protected abstract fun initData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModelFactory = ViewModelFactory()
        mViewModel = ViewModelProvider(this, viewModelFactory)[getViewModelClass()]
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        mViewDataBinding.lifecycleOwner = this
        mViewDataBinding.executePendingBindings()
        return mViewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initActionKeyBack(view)
        initAdapter()
        initAction()
        initData()
    }

    protected open fun initAction() {}

    protected open fun initAdapter() {}

    protected fun getWidthScreen(): Int {
        return resources.displayMetrics.widthPixels
    }

    private fun initActionKeyBack(view: View?) {
        //Animation T5 block out when back press is 730 (ANIMATE_DELAY_BACK_DURATION)
        //To ensure animation T5 end => post delay 1000L to change value of isBackab;e
        Handler().postDelayed({
            isBackable = true
        }, 1000L)

        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        view?.setOnKeyListener { _, keyCode, keyEvent ->
            if (isBackable) {
                when (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_DOWN) {
                    true -> {
                        handleBack()
                        (activity as HomeActivity).onBackPressed()
                        true
                    }
                    else -> false
                }
            } else
                true
        }
    }

    open fun handleBack() {}

    class ViewModelFactory : ViewModelProvider.Factory {
        override fun <V : ViewModel> create(modelClass: Class<V>): V {
            return modelClass.getConstructor().newInstance()
        }
    }

}