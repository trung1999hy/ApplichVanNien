package com.example.universalcalendar.widgets

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.example.universalcalendar.R
import com.example.universalcalendar.common.Constant
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarItemView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import kotlin.properties.Delegates

class CircleBottomNavigation : BottomNavigationView {
    private var currentNavigationItemId = -1
    private var currentCircleId = -1
    private val menuGroupId = View.generateViewId()
    private var enableColor = Color.WHITE
    private var textColor by Delegates.notNull<Int>()
    private var disableColor =
        ContextCompat.getColor(
            context,
            com.google.android.material.R.color.material_on_surface_emphasis_medium
        )
    var backgroundShape = Shape.CIRCLE
    private var customBackgroundDrawable = -1
    private lateinit var rootLayoutView: RelativeLayout

    enum class Shape(val value: Int) {
        CIRCLE(0),
        ROUNDED(1);

        companion object {
            private val VALUES = values()
            fun getByValue(value: Int) = VALUES.firstOrNull { it.value == value }
        }

    }

    var darkIcon = false
        set(value) {
            field = value
            setEnableColor()
        }
    var circleColor = R.color.orange

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet? = null) {
        getColors(attrs)
        getBackgroundDrawable(attrs)
        setUpRootLayoutView()
        setUpListener()
        setUpClipping()
        selectFirstItem()
    }

    private fun getColors(attrs: AttributeSet?) {
        circleColor = getAttributesColorOrDefault(attrs)
        textColor = context.resources.getColor(R.color.orange)
    }

    private fun getAttributesColorOrDefault(attrs: AttributeSet?): Int {
        var color: Int

        context.theme.obtainStyledAttributes(
            attrs, R.styleable.CircleBottomNavigation, 0, 0
        ).apply {
            try {
                color = getInteger(
                    R.styleable.CircleBottomNavigation_circleColor,
                    ContextCompat.getColor(
                        context,
                        com.google.android.material.R.color.design_default_color_primary
                    )
                )
            } finally {
                setEnableColor()
            }
        }
        return color
    }

    private fun getBackgroundDrawable(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs, R.styleable.CircleBottomNavigation, 0, 0
        ).apply {
            try {
                backgroundShape = Shape.getByValue(
                    getInt(
                        R.styleable.CircleBottomNavigation_backgroundShape,
                        0
                    )
                )!!
                customBackgroundDrawable = getResourceId(
                    R.styleable.CircleBottomNavigation_customBackgroundShape,
                    -1
                )
            } finally {
                recycle()
            }
        }
    }

    private fun setUpRootLayoutView() {
        val menuViewGroup = getChildAt(Constant.INDEX_DEFAULT) as BottomNavigationMenuView
        menuViewGroup.id = menuGroupId
        rootLayoutView = RelativeLayout(context)
        removeView(menuViewGroup)
        rootLayoutView.addView(menuViewGroup)
        addView(rootLayoutView)
    }

    private fun setUpListener() {
        setOnItemSelectedListener {
            return@setOnItemSelectedListener animationBottomIcon(it.itemId)
        }
    }

    private fun setUpClipping() {
        viewTreeObserver.addOnGlobalLayoutListener {
            clipChildren = false
            rootLayoutView.clipChildren = false
            findViewById<BottomNavigationMenuView>(menuGroupId).clipChildren = false
            disableClipParents(this)
        }
    }

    private fun disableClipParents(view: View) {
        if (view is ViewGroup) {
            view.clipChildren = false
        }

        if (view.parent is View) {
            disableClipParents(view.parent as View)
        }
    }


    private fun setEnableColor() {
        enableColor = if (darkIcon) Color.BLACK else Color.WHITE
    }

    private fun selectFirstItem() {
        if (rootLayoutView.childCount > 0 &&
            ((rootLayoutView.getChildAt(Constant.INDEX_DEFAULT) as BottomNavigationMenuView).childCount > 0)
        ) {
            val navigationItemView =
                ((rootLayoutView.getChildAt(Constant.INDEX_DEFAULT) as BottomNavigationMenuView)
                    .getChildAt(Constant.INDEX_DEFAULT) as NavigationBarItemView)

            navigationItemView.viewTreeObserver.addOnGlobalLayoutListener {
                animationBottomIcon(selectedItemId)
            }
        }
    }

    private fun animationBottomIcon(itemId: Int): Boolean {
        if (itemId != currentNavigationItemId) {
            val itemView = getNavigationBarItemView(itemId)
            val icon = getAppCompatImageView(itemView)
            disableClipParents(icon)
            val subtext = getSubtextView(itemView)
            val animatorSet = AnimatorSet()

            setSubtextStyle(subtext)

            //navigate previous selection out
            if (currentNavigationItemId != -1) {
                val currentItemView = getNavigationBarItemView(currentNavigationItemId)
                val currentView = getAppCompatImageView(currentItemView)
                val oldCircle = rootLayoutView.findViewById<ImageView>(currentCircleId)

                currentView.drawable.setTint(Color.BLACK)

                animatorSet.playTogether(
                    buildTranslateIconAnimator(currentView, -(height / 4).toFloat(), 0f),
                    buildTranslateCircleAnimator(oldCircle, -(height / 4).toFloat(), 0f),
                    buildTintAnimator(currentView, enableColor, disableColor)
                )
                oldCircle.animate()
                    .alpha(0F)
                    .duration = 500

                GlobalScope.launch {
                    delay(500)
                    withContext(Dispatchers.Main) {
                        rootLayoutView.removeView(oldCircle)
                    }
                }
            }
            //navigate previous selection out
            val circleView = buildBackgroundCircle()
            currentCircleId = circleView.id

            rootLayoutView.addView(circleView)
            findViewById<BottomNavigationMenuView>(menuGroupId).bringToFront()

            setCircleSizeAndPosition(
                circleView,
                subtext.height,
                icon.width * 2,
                itemView.x + itemView.width / 2 - icon.width
            )

            animatorSet.playTogether(
                buildTranslateIconAnimator(icon, 0f, -(height / 4).toFloat()),
                buildTranslateCircleAnimator(circleView, 0f, -(height / 4).toFloat()),
                buildTintAnimator(icon, disableColor, enableColor)
            )

            circleView.animate()
                .alpha(1F)
                .duration = 500
            currentNavigationItemId = itemId
            animatorSet.start()
        }
        return true
    }

    private fun buildBackgroundCircle(): ImageView {
        val circleView = ImageView(context)
        circleView.id = generateViewId()
        circleView.alpha = 0F

        if (customBackgroundDrawable == -1) {
            val backgroundShapeDrawable = when (backgroundShape) {
                Shape.CIRCLE -> ContextCompat.getDrawable(context, R.drawable.bg_orange_circle)
                Shape.ROUNDED -> ContextCompat.getDrawable(context, R.drawable.bg_orange_rectangle)
            }
            backgroundShapeDrawable?.setTint(circleColor)
            circleView.setImageDrawable(backgroundShapeDrawable)
        } else {
            val drawable = ContextCompat.getDrawable(context, customBackgroundDrawable)
            drawable?.setTint(circleColor)
            circleView.setImageDrawable(drawable)
        }
        return circleView
    }

    private fun getNavigationBarItemView(itemId: Int): NavigationBarItemView {
        return findViewById(itemId)
    }

    private fun getAppCompatImageView(itemView: NavigationBarItemView): AppCompatImageView {
        return itemView.findViewById(
            com.google.android.material.R.id.navigation_bar_item_icon_view
        )
    }

    private fun getSubtextView(itemView: NavigationBarItemView): TextView {
        return itemView.findViewById(
            com.google.android.material.R.id.navigation_bar_item_large_label_view
        )
    }

    private fun setSubtextStyle(textView: TextView) {
        textView.setTypeface(textView.typeface, Typeface.BOLD)
        textView.setTextColor(textColor)
    }

    private fun buildTranslateIconAnimator(
        currentView: View,
        from: Float,
        to: Float
    ): ObjectAnimator {
        return ObjectAnimator.ofFloat(
            currentView,
            "translationY",
            from, to
        ).setDuration(500)
    }

    private fun buildTranslateCircleAnimator(oldCircle: View, from: Float, to: Float):
            ObjectAnimator {
        return ObjectAnimator.ofFloat(
            oldCircle,
            "translationY",
            from, to
        ).setDuration(500)
    }

    private fun buildTintAnimator(currentView: AppCompatImageView, from: Int, to: Int):
            ValueAnimator {
        val animateTint = ValueAnimator.ofArgb(from, to)
        animateTint.duration = 500
        animateTint.addUpdateListener {
            currentView.drawable.setTint(it.animatedValue as Int)
        }

        return animateTint
    }

    private fun setCircleSizeAndPosition(
        circleView: ImageView,
        paddingBottom: Int,
        size: Int,
        x: Float
    ) {
        val params = circleView.layoutParams
        circleView.setPadding(0, 0, 0, paddingBottom / 3)
        params.width = size
        params.height = size
        circleView.layoutParams = params
        circleView.x = x
    }
}