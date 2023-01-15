package com.example.universalcalendar.ui.dialog

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.universalcalendar.R
import com.example.universalcalendar.extensions.DateUtils
import com.example.universalcalendar.extensions.SharePreference
import com.example.universalcalendar.extensions.click
import com.example.universalcalendar.model.Event
import com.example.universalcalendar.model.User
import com.example.universalcalendar.ui.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_user_login.view.*
import java.util.*


class UserDialog : BaseDialog() {

    companion object {
        fun newInstance() = UserDialog()
    }

    private lateinit var mView: View
    var userLoginCallback: (user: User) -> Unit = {}


    override fun createCustomView(): View {
        mView = View.inflate(context, R.layout.dialog_user_login, null)
        initView()
        return mView
    }

    private fun initView() {
        mView.ic_login_close?.click { dismissDialog() }
        mView.ic_login_accept?.click { saveUserInfor() }
        mView.tv_login_name?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mView.tv_login_name_error?.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        mView.tv_login_date_of_birth?.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private val ddmmyyyy = "________"
            private val cal = Calendar.getInstance()
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() != current) {
                    var clean = p0.toString().replace("[^\\d.]|\\.".toRegex(), "")
                    val cleanC = current.replace("[^\\d.]|\\.".toRegex(), "")

                    val cl = clean.length
                    var sel = cl
                    var i = 2
                    while (i <= cl && i < 6) {
                        sel++
                        i += 2
                    }
                    if (clean == cleanC) sel--
                    if (clean.length < 8) {
                        clean += ddmmyyyy.substring(clean.length)
                    } else {
                        var day = clean.substring(0, 2).toInt()
                        var mon = clean.substring(2, 4).toInt()
                        var year = clean.substring(4, 8).toInt()
                        mon = if (mon < 1) 1 else if (mon > 12) 12 else mon
                        cal[Calendar.MONTH] = mon - 1
                        year = if (year < 1900) 1900 else if (year > 2100) 2100 else year
                        cal[Calendar.YEAR] = year
                        day = if (day > cal.getActualMaximum(Calendar.DATE)) cal.getActualMaximum(
                            Calendar.DATE
                        ) else day
                        clean = String.format("%02d%02d%02d", day, mon, year)
                    }

                    clean = String.format(
                        "%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8)
                    )

                    sel = if (sel < 0) 0 else sel
                    current = clean
                    mView.tv_login_date_of_birth?.setText(current)
                    mView.tv_login_date_of_birth?.setSelection(if (sel < current.length) sel else current.length)
                }
                mView.tv_login_dob_error?.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        mView.tv_login_email?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                mView.tv_login_email_error?.visibility = View.GONE
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        val userInfo = SharePreference.getInstance().getUserInformation()
        if (userInfo != null) {
            mView.tv_login_name?.setText(userInfo.name)
            mView.tv_login_date_of_birth?.setText(DateUtils.convertDateToString(
                DateUtils.convertStringToDate(DateUtils.DATE_LOCALE_FORMAT_2, userInfo.dateOfBirth),
                DateUtils.DOB_FORMAT
            ))
            mView.tv_login_email?.setText(userInfo.email)
        }
    }

    private fun saveUserInfor() {
        if (mView.tv_login_name?.text?.isEmpty() == true) {
            mView.tv_login_name_error?.visibility = View.VISIBLE
        } else if(mView.tv_login_date_of_birth?.text?.isEmpty() == true ||
            mView.tv_login_date_of_birth?.text?.toString()?.contains("_") == true) {
            mView.tv_login_dob_error?.visibility = View.VISIBLE
        } else if (mView.tv_login_email?.text?.isEmpty() == true) {
            mView.tv_login_email_error?.visibility = View.VISIBLE
        } else {
            val userName = mView.tv_login_name?.text.toString()
            val userDob =
                mView.tv_login_date_of_birth?.text.toString().replace("[^\\d.]|\\.".toRegex(), "")
            val userDobFormat = DateUtils.convertDateToString(
                DateUtils.convertStringToDate(DateUtils.DOB_USER_FORMAT, userDob),
                DateUtils.DATE_LOCALE_FORMAT_2
            )
            val userEmail = mView.tv_login_email?.text.toString()
            val prefs = SharePreference.getInstance()
            val user = User(userName, userDobFormat, userEmail)
            prefs.saveUserInformation(user)
            prefs.saveEvent(Event(
                id = userDob.toInt(),
                daySolar = userDob.substring(0, 2).toInt(),
                monthSolar = userDob.substring(2, 4).toInt(),
                yearSolar = userDob.substring(4, 8).toInt(),
                title = "Ngày sinh nhật",
                timeStart = userDob,
                timeEnd = userDob
            ))
            userLoginCallback.invoke(user)
            dismissDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun shows(fm: FragmentManager): BaseDialog {
        show(fm, UserDialog::class.java.name)
        return this
    }

}