package com.ericwei.sets

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mSharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        with(mSharedPrefs.edit()) {
            putLong(getString(R.string.time_remain), FULL_TIME)
            apply()
        }
    }

    companion object {
        val FULL_TIME: Long = 60000 * 2
    }
}
