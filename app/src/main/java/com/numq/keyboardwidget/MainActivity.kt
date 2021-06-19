package com.numq.keyboardwidget

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.numq.osk.KeyboardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val display = findViewById<TextView>(R.id.display)

        val keyboard = findViewById<KeyboardView>(R.id.keyboard)
        val ruKeys = arrayOf("йцукенгшщзхъ", "фывапролджэ", "ячсмитьбю")
        keyboard.addKeySet("ru", ruKeys)

        var langChanged = false
        display.setOnClickListener {
            if (keyboard.isVisible) {
                if (langChanged) {
                    keyboard.build(ruKeys)
                } else {
                    keyboard.build("en")
                }
                langChanged = !langChanged
            }
        }

        display.setOnLongClickListener {
            if (keyboard.isVisible) {
                keyboard.hide()
            } else {
                keyboard.show()
            }
            return@setOnLongClickListener true
        }

        keyboard.setOnKeyClickListener {
            display.text = it.text.toString().uppercase()
        }
    }
}