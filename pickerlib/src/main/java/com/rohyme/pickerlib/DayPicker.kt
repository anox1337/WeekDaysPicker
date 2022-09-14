package com.rohyme.pickerlib

import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*


class DayPicker : LinearLayout {

    private var daysList: ArrayList<DaysModel.Day> = getData<DaysModel>(context, "days_list").days
    private val textItemList = hashMapOf<Int, TextView>()
    private lateinit var selectionListener: DayPickerListener
    private var isSelectedByDefault: Boolean = true
    private var selectedColor: Int = ContextCompat.getColor(context, R.color.selected_color)
    private var unSelectedColor: Int = ContextCompat.getColor(context, R.color.unSelected_color)
    private var mTextColor: Int = ContextCompat.getColor(context, R.color.text_color)
    private var isFullText: Boolean = true
    private var mSpaces = 4
    private var mTextSize = resources.getDimension(R.dimen.text_size)
    private var mTextSelectedColor = 0
    private var mTextUnSelectedColor = 0
    private var mIsEnabled: Boolean = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setAttrs(attrs)
        post {
            initView()
        }
    }


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setAttrs(attrs)
        post {
            initView()
        }
    }

    /**
     * Set Attributes
     */

    fun setAttrs(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.dayPicker, 0, 0)
        try {
            mIsEnabled = typedArray.getBoolean(R.styleable.dayPicker_dp_enabled, true)
            isSelectedByDefault = typedArray.getBoolean(R.styleable.dayPicker_dp_default_selection, true)
            selectedColor = typedArray.getInt(
                R.styleable.dayPicker_dp_selected_color,
                ContextCompat.getColor(context, R.color.selected_color)
            )
            unSelectedColor = typedArray.getInt(
                R.styleable.dayPicker_dp_unSelected_color,
                ContextCompat.getColor(context, R.color.unSelected_color)
            )
            mTextColor = typedArray.getInt(
                R.styleable.dayPicker_dp_font_color,
                0
            )

            isFullText = typedArray.getBoolean(
                R.styleable.dayPicker_dp_day_text_full,
                true
            )
            mSpaces = typedArray.getDimension(
                R.styleable.dayPicker_dp_spaces_between_days,
                4f
            ).toInt()
            mTextSize = typedArray.getDimension(
                R.styleable.dayPicker_dp_text_size,
                resources.getDimension(R.dimen.text_size)
            )

            mTextSelectedColor = typedArray.getColor(
                R.styleable.dayPicker_dp_text_selected_color,
                ContextCompat.getColor(context, R.color.text_color)
            )

            mTextUnSelectedColor = typedArray.getColor(
                R.styleable.dayPicker_dp_text_unselected_color,
                ContextCompat.getColor(context, R.color.text_color)
            )


        } finally {
            typedArray.recycle()
        }
    }

    /**
     * init the view container and add dayItems to it
     */

    private fun initView() {
        val dayTextView = TextView(context)
        dayTextView.background = resources.getDrawable(R.drawable.day_bg)
        val currentLocale = getCurrentLocale(context)
        val containerWidth = measuredWidth
        val itemHeight = (containerWidth - (mSpaces * 6)) / daysList.size
        val params = layoutParams
        params.height = itemHeight
        layoutParams = params
        daysList.forEachIndexed { index, day ->
            day.isSelected = isSelectedByDefault
            val hasMargin = index != 0
            val dayItem = initText(day)
            dayItem.setDayText(currentLocale, day)
            val textParams = LinearLayout.LayoutParams(0, MATCH_PARENT, 1f)
            if (hasMargin)
                textParams.marginStart = mSpaces
            dayItem.layoutParams = textParams
            addView(dayItem)
            textItemList[index] = dayItem
        }
    }

    /**
     * Set every text view specifications
     * @param day is the day model that item hold
     */
    private fun initText(day: DaysModel.Day): TextView {
        return TextView(context).apply {
            this@apply.textSize = this@DayPicker.mTextSize
            gravity = Gravity.CENTER
            setPadding(1, 1, 1, 1)
            this.background = resources.getDrawable(R.drawable.day_bg)
            configSelection(day.isSelected)
            setOnClickListener {
                if(!mIsEnabled) return@setOnClickListener

                day.isSelected = !day.isSelected
                if (this@DayPicker::selectionListener.isInitialized) {
                    selectionListener.onSelectedDaysChange(daysList, day)
                }
                this.configSelection(day.isSelected)
            }

        }
    }

    /**
     * Handle text displays in the each day item
     * @param locale used by the user
     */
    private fun TextView.setDayText(locale: Locale, day: DaysModel.Day) {
        val targetLang = locale.language.toLowerCase(Locale.ROOT)

        text = when (targetLang) {
            "ar" -> if(isFullText) day.arDay else day.arDayAbbrev
            "en" -> if(isFullText) day.enDay else day.enDayAbbrev
            "de" -> if(isFullText) day.deDay else day.deDayAbbrev
            else -> "??"
        }

    }


    /**
     * Config selection behavior and colors textColor and background color
     */
    private fun TextView.configSelection(isSelected: Boolean) {
        if (isSelected) {
            background.setColorFilter(selectedColor, PorterDuff.Mode.SRC_ATOP)
            setTextColor(mTextSelectedColor)
        } else {
            background.setColorFilter(unSelectedColor, PorterDuff.Mode.SRC_ATOP)
            setTextColor(mTextUnSelectedColor)
        }
        if (mTextColor != 0) {
            setTextColor(mTextColor)
        }
    }

    /**
     * Selects or deselects all days.
     *
     * @param select or deselect all
     */
    fun selectAll(select : Boolean) {
        daysList.forEach {
            it.isSelected = select
        }

        textItemList.forEach {
           it.value.isSelected = select
           it.value.configSelection(select)
        }
    }

    /**
     * Method to get the selected List
     */
    fun getSelectedList(): ArrayList<DaysModel.Day> {
        return ArrayList(daysList.filter {
            it.isSelected
        })
    }

    /**
     * Selecting listener
     */
    interface DayPickerListener {
        fun onSelectedDaysChange(
            list: ArrayList<DaysModel.Day>,
            latestSelected: DaysModel.Day
        )
    }

    fun setOnSelectListener(dayPickerListener: DayPickerListener) {
        this.selectionListener = dayPickerListener
    }

    private fun getCurrentLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
    }
}
