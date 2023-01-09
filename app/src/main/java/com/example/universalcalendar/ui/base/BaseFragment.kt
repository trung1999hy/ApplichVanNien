package com.example.universalcalendar.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseFragment<B : ViewDataBinding, V : ViewModel> : Fragment() {

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mViewDataBinding: B
    private lateinit var mViewModel: V

    val binding: B get() = mViewDataBinding
    val viewModel: V get() = mViewModel

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
        initData()
    }

    protected fun getWidthScreen(): Int {
        return resources.displayMetrics.widthPixels
    }

    class ViewModelFactory : ViewModelProvider.Factory {
        override fun <V : ViewModel> create(modelClass: Class<V>): V {
            return modelClass.getConstructor().newInstance()
        }
    }

}