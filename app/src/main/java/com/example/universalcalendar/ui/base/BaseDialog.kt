package com.example.universalcalendar.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

abstract class BaseDialog : DialogFragment() {

    // protected var bundle = Bundle()
    private lateinit var dialogBuilder: AlertDialog.Builder
    var callback: () -> Unit = {}
    var dialogBackPress: () -> Unit = {}
    var dialogDestroy : (() -> Unit)? = null
    // private var dialogPosition: DialogPosition? = DialogPosition.DEFAULT


    // open fun put(param: Params): BaseDialog {
    //     (param as Params).let {
    //         dialogPosition = it.dialogPosition
    //     }
    //     arguments = bundle
    //     return this
    // }

    abstract fun shows(fm: FragmentManager): BaseDialog

    abstract fun createCustomView(): View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogBuilder = AlertDialog.Builder(requireContext()).setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK){
                dialogBackPress.invoke()
            }
            false
        }
        dialogBuilder.setView(createCustomView())
        return dialogBuilder.create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }

    fun dismissDialog(){
        dismiss()
    }

    override fun onDestroy() {
        dialogDestroy?.invoke()
        super.onDestroy()
    }

    // enum class DialogPosition {
    //     DEFAULT, BOTTOM, FILL
    // }

    // abstract class Params(var dialogPosition: DialogPosition? = DialogPosition.DEFAULT)

}