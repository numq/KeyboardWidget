package com.numq.osk

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView

class KeyboardView(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs) {

    private var onClick: ((MaterialTextView) -> Unit)? = null
    private val keySets: MutableMap<String, Array<String>> =
        mutableMapOf("en" to arrayOf(Keys.top, Keys.mid, Keys.bot))
    private var currentLang = "en"

    private var cardView: ((View) -> Unit)? = null
    private var keyView: ((View) -> Unit)? = null

    private val keyboard = inflate(context, R.layout.layout_keyboard, this)
    private val top = keyboard.findViewById<LinearLayout>(R.id.keyboardTop)
    private val mid = keyboard.findViewById<LinearLayout>(R.id.keyboardMid)
    private val bot = keyboard.findViewById<LinearLayout>(R.id.keyboardBot)

    init {
        build()
    }

    private fun buildKeys(
        keys: Array<String> = keySets["en"]!!,
        vararg colors: () -> Unit = arrayOf({})
    ) {
        val rows = arrayOf(top, mid, bot)
        rows.map { it.removeAllViews() }
        for ((pos, row) in rows.withIndex()) {
            row.weightSum = keys.maxOf { it.length } * 1f
            for (k in keys[pos]) {
                val itemView = inflate(context, R.layout.item_key, null)
                row.addView(itemView)

                colors.map { it.invoke() }

                val anim = AnimatorSet()
                val card = itemView.findViewById<MaterialCardView>(R.id.keyCard)
                cardView?.invoke(card)
                val key = itemView.findViewById<MaterialTextView>(R.id.key)
                keyView?.invoke(key)
                with(key) {
                    text = k.toString()
                    gravity = Gravity.CENTER
                }
                with(card) {
                    val params = layoutParams as LinearLayout.LayoutParams
                    params.weight = 1f
                    layoutParams = params
                    val set = arrayOf(
                        ObjectAnimator.ofFloat(card, "scaleX", 1f, 1.2f),
                        ObjectAnimator.ofFloat(card, "scaleY", 1f, 1.2f),
                        ObjectAnimator.ofFloat(card, "scaleX", 1.2f, 1f),
                        ObjectAnimator.ofFloat(card, "scaleY", 1.2f, 1f)
                    )
                    anim.interpolator = LinearInterpolator()
                    anim.duration = 500
                    anim.playTogether(*set)
                    setOnClickListener {
                        onClick?.invoke(key as MaterialTextView)
                        anim.start()
                    }
                }
            }
        }
    }


    fun addKeySet(lang: String, rows: Array<String>) {
        keySets[lang] = rows
    }

    fun addKeySet(lang: String, topRow: String, midRow: String, botRow: String) {
        keySets[lang] = arrayOf(topRow, midRow, botRow)
    }

    private fun build() = buildKeys()

    private fun build(lang: String, vararg colors: () -> Unit) {
        try {
            buildKeys(keySets[lang]!!, colors = colors)
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
        }
    }

    fun build(top: String, mid: String, bot: String) = buildKeys(arrayOf(top, mid, bot))

    fun build(lang: String) {
        try {
            buildKeys(keySets[lang]!!)
            currentLang = lang
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
        }
    }

    fun build(rows: Array<String>) {
        try {
            buildKeys(rows)
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
        }
    }

    fun changeCardColor(colorCode: String) =
        build(currentLang, { cardView = { it.setBackgroundColor(Color.parseColor(colorCode)) } })

    fun changeKeyColor(colorCode: String) =
        build(currentLang, { keyView = { it.setBackgroundColor(Color.parseColor(colorCode)) } })

    fun changeBackgroundColor(colorCode: String) =
        build(currentLang, { setBackgroundColor(Color.parseColor(colorCode)) })

    fun changeCardColor(color: Int) =
        build(currentLang, { cardView = { it.setBackgroundColor(color) } })

    fun changeKeyColor(color: Int) =
        build(currentLang, { keyView = { it.setBackgroundColor(color) } })

    fun changeBackgroundColor(color: Int) = build(currentLang, { this.setBackgroundColor(color) })

    fun show() {
        visibility = VISIBLE
    }

    fun hide() {
        visibility = GONE
    }

    fun setOnKeyClickListener(action: (MaterialTextView) -> Unit) {
        onClick = {
            action.invoke(it)
        }
    }

    private object Keys {
        var top: String = "qwertyuiop"
        var mid: String = "asdfghjkl"
        var bot: String = "zxcvbnm"
    }
}