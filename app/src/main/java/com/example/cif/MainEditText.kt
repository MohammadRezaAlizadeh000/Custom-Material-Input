package com.example.cif

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.cif.R.*


class MainEditText : FrameLayout {

    constructor(context: Context) : super(context) {
        initConfig(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initConfig(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initConfig(context, attrs)
    }

    var conditionFunction: (text: String) -> MainEditTextConditionState =
        { MainEditTextConditionState.Nothing }

    private lateinit var typedArray: TypedArray
    private var size = EditTextSize.SMALL
    private var style = EditTextStyle.SIMPLE
    private var inputHeight = 0
    private var borderColor = ContextCompat.getColor(context, color.black)
    private var inputCorner = dimen.big_input_corner
    private var inputLayout = LinearLayout(context)
    private var inputHintTextView = TextView(context)
    private var inputEditText = EditText(context)
    private var isMandatory = false
    private var hintHorizontalMargin = 0
    private var isPassHide = true
    private var leftImageView1 = ImageView(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 0, context.resources.getDimensionPixelSize(dimen.padding8), 0)
        }
        visibility = GONE
    }
    private var rightImageView = ImageView(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            setMargins(context.resources.getDimensionPixelSize(dimen.padding8), 0, 0, 0)
        }
    }
    private var hidePassImageView = ImageView(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 0, context.resources.getDimensionPixelSize(dimen.padding8), 0)
            setImageResource(R.drawable.ic_show_outline)
            visibility = GONE
        }
    }
    private var rightImageSrc = 0
    private var horizontalPadding = dimen.padding6
    private var inputHintText = ""

    //    private var hintBottomMargin = dimen.padding8
    private var hintBottomMargin = 0

    private fun initConfig(context: Context, attrs: AttributeSet?) {

        typedArray = context.obtainStyledAttributes(attrs, styleable.MainEditText)

        size = typedArray.getEnum(styleable.MainEditText_size, EditTextSize.SMALL)
        style = typedArray.getEnum(styleable.MainEditText_style, EditTextStyle.SIMPLE)
        inputHintText = typedArray.getString(styleable.MainEditText_hint) ?: ""
        isMandatory = typedArray.getBoolean(styleable.MainEditText_isMandatory, false)
        rightImageSrc = typedArray.getResourceId(styleable.MainEditText_rightIcon, 0)


        createView()
        typedArray.recycle()
    }

    private fun createView() {
        handleSize()
        handleStyle()

        createMainLayout()
        createInputLayout()

        handleHint()
        hintClickAction()
    }

    private fun handleSize() {
        when (size) {
            EditTextSize.SMALL -> {
                setLayoutHeight(dimen.small_input_fringe_height)
                setInputHeight(dimen.small_input_height)
                setInputCorners(dimen.small_input_corner)
                setInputHorizontalPadding(dimen.padding6)
                setHintMargin(dimen.padding6)
            }
            EditTextSize.NORMAL -> {
                setLayoutHeight(dimen.normal_input_fringe_height)
                setInputHeight(dimen.normal_input_height)
                setInputCorners(dimen.normal_input_corner)
                setInputHorizontalPadding(dimen.padding12)
                setHintMargin(dimen.padding12)
            }
            EditTextSize.BIG -> {
                setLayoutHeight(dimen.big_input_fringe_height)
                setInputHeight(dimen.big_input_height)
                setInputCorners(dimen.big_input_corner)
                setInputHorizontalPadding(dimen.padding10)
                setHintMargin(dimen.padding10)
            }
        }
    }

    private fun setLayoutHeight(@DimenRes value: Int) {
        minimumHeight = context.resources.getDimensionPixelSize(value)
    }

    private fun setInputHeight(@DimenRes value: Int) {
        inputHeight = context.resources.getDimensionPixelSize(value)
    }

    private fun setInputCorners(@DimenRes value: Int) {
        inputCorner = context.resources.getDimensionPixelSize(value)
    }

    private fun setInputHorizontalPadding(@DimenRes value: Int) {
        horizontalPadding = context.resources.getDimensionPixelSize(value)
    }

    private fun createMainLayout() {
        layoutParams = LayoutParams(width, minimumHeight)
    }

    private fun handleStyle() {
        when (style) {
            EditTextStyle.SIMPLE -> {
                handleSimpleStyle()
            }
            EditTextStyle.ICON -> {
                handleIconStyle()
                setHintMargin(dimen.padding24)
                setHintMargin(dimen.padding8)
            }
            EditTextStyle.PHONE -> {

            }
            EditTextStyle.PASS -> {
                setHintMargin(dimen.padding24)
                setHintMargin(dimen.padding8)
                handlePasswordStyle()
            }
            EditTextStyle.DISCOUNT -> {

            }
        }
    }

    private fun setHintMargin(@DimenRes value: Int) {
        hintHorizontalMargin += context.resources.getDimensionPixelSize(value)
    }

    private fun handleHint() {
        addView(
            inputHintTextView.apply {
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                    Gravity.END or Gravity.TOP
                ).apply {
                    setMargins(
                        hintHorizontalMargin,
                        ((this@MainEditText.minimumHeight / 2) - (inputLayout.minimumHeight / 2)),
                        hintHorizontalMargin,
                        0
                    )
                    setPadding(
                        context.resources.getDimensionPixelSize(dimen.padding4),
                        0,
                        context.resources.getDimensionPixelSize(dimen.padding4),
                        0
                    )
                }
                setBackgroundColor(ContextCompat.getColor(context, color.white))
                setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(dimen.input_text_size)
                )
                text = inputHintText
            }
        )
    }

    private fun hintClickAction() {
        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                hintToLabel()
                borderColor = ContextCompat.getColor(context, color.primary_light)
                rightImageView.setColorFilter(ContextCompat.getColor(context, color.primary_light))
                inputHintTextView.setTextColor(ContextCompat.getColor(context, color.primary_light))
            } else {
                if (inputEditText.text.isNullOrEmpty()) labelToHint()
                handleCondition()
            }
            inputLayout.background = setInputBackground()
        }
    }

    private fun handleCondition() {
        when (val result = conditionFunction.invoke(inputEditText.text.toString())) {
            is MainEditTextConditionState.IsError -> errorStyle(result.message)
            is MainEditTextConditionState.IsPassed -> defaultStyle()
            is MainEditTextConditionState.Nothing ->
                if (isMandatory) isMandatoryCheck()
                else defaultStyle()
        }
    }

    private fun errorStyle(errorMessage: String) {
        borderColor = ContextCompat.getColor(context, color.error_light)
        inputLayout.background = setInputBackground()
        inputHintTextView.setTextColor(ContextCompat.getColor(context, color.error_light))
        rightImageView.setColorFilter(ContextCompat.getColor(context, color.error_light))
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun defaultStyle() {
        borderColor = ContextCompat.getColor(context, color.black)
        inputLayout.background = setInputBackground()
        rightImageView.setColorFilter(ContextCompat.getColor(context, color.black))
        inputHintTextView.setTextColor(ContextCompat.getColor(context, color.black))
    }

    private fun isMandatoryCheck() {
        if (inputEditText.text.toString().isEmpty())
            errorStyle("پر کردن این فیلد اجباری است :)")
        else defaultStyle()
    }

    private fun hintToLabel() {
        inputHintTextView.apply {
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(dimen.input_label_size)
            )
            animate().translationY(
                -((inputLayout.height / 2)).toFloat()
            )
            if (rightImageView.width != 0)
                animate().translationX(rightImageView.width.toFloat() + (context.resources.getDimensionPixelSize(dimen.padding8)))

            if (isMandatory && !text.contains("*"))
                text = " * $text"
        }
    }

    private fun labelToHint() {
        inputHintTextView.apply {
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(dimen.input_text_size)
            )
            animate().translationY(0F)
            animate().translationX(0F)
            if (isMandatory)
                text = inputHintText
        }
    }

    private fun createInputLayout() {
        addView(inputLayout.apply {
            layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, inputHeight, Gravity.BOTTOM).apply {
//                gravity = Gravity.END
                }
            orientation = LinearLayout.HORIZONTAL
            background = setInputBackground()
            gravity = Gravity.CENTER_VERTICAL or Gravity.END
            setPadding(horizontalPadding, 0, horizontalPadding, 0)
        })
    }

    private fun setInputBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setStroke(1, borderColor)
            cornerRadius = inputCorner.toFloat()
            setColor(ContextCompat.getColor(context, R.color.white))
        }
    }


    private fun handleSimpleStyle() {

        addCircleCloseBtn()
        onCircleCloseBtnClick()

        addEditText()
        onEditTextWatcher()

    }

    private fun handleIconStyle() {
        addCircleCloseBtn()
        onCircleCloseBtnClick()

        addEditText()
        onEditTextWatcher()

        addRightIcon()
    }

    private fun handlePasswordStyle() {
        inputEditText.transformationMethod = PasswordTransformationMethod.getInstance()

        addHidePassBtn()
        handlePassBtnClick()

        addCircleCloseBtn()
        onCircleCloseBtnClick()

        addEditText()
        onEditTextWatcher()

        rightImageSrc = drawable.ic_lock_close_outline
        addRightIcon()
    }

    private fun addCircleCloseBtn() {
        inputLayout.addView(leftImageView1.apply { setImageResource(R.drawable.ic_circle_close) })

    }

    private fun addEditText() {
        inputLayout.addView(inputEditText.apply {
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                this.weight = 1F
//                TODO("layout direction and text direction")
            }
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(dimen.input_text_size)
            )
        })
    }

    private fun onEditTextWatcher() {
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                s?.let {
                    if (s.isEmpty()) {
                        leftImageView1.visibility = GONE
                        hidePassImageView.visibility = GONE
                    } else {
                        leftImageView1.visibility = View.VISIBLE
                        hidePassImageView.visibility = VISIBLE
                    }
                }

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun onCircleCloseBtnClick() {
        leftImageView1.setOnClickListener {
            inputEditText.setText("")
            inputEditText.requestFocus()
            (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
                showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    private fun addRightIcon() {
        if (rightImageSrc != 0)
            inputLayout.addView(rightImageView.apply { setImageResource(rightImageSrc) })
    }

    private fun addHidePassBtn() {
        inputLayout.addView(hidePassImageView)
    }

    private fun handlePassBtnClick() {
        hidePassImageView.setOnClickListener {
            isPassHide = if (isPassHide) {
                hidePassImageView.setImageResource(drawable.ic_hide_outline)
                inputEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                false
            } else {
                hidePassImageView.setImageResource(drawable.ic_show_outline)
                inputEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                true
            }
        }
    }
}


enum class EditTextStyle { SIMPLE, ICON, PHONE, PASS, DISCOUNT }
enum class EditTextSize { SMALL, NORMAL, BIG }

internal inline fun <reified T : Enum<T>> TypedArray.getEnum(index: Int, default: T) =
    getInt(index, -1).let { if (it >= 0) enumValues<T>()[it] else default }