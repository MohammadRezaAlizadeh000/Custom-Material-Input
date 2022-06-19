package com.example.cif

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bigInput = findViewById<MainEditText>(R.id.bigInput)

        bigInput.conditionFunction = {
            if (it.isEmpty())
                MainEditTextConditionState.IsError("پر کردن فسلد اجباری است")
            else if (it.length != 11)
                MainEditTextConditionState.IsError("شماره تلفن صحیح نیست")
            else
                MainEditTextConditionState.IsPassed
        }

    }
}