package com.ericwei.sets

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mSharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        mSharedPrefs = getSharedPreferences("sets_prefs", Context.MODE_PRIVATE)
        
        if (!mSharedPrefs.contains(getString(R.string.time_remain))) {
            with(mSharedPrefs.edit()) {
                putLong(getString(R.string.time_remain), FULL_TIME)
                apply()
            }
        }
    }

    companion object {
        const val FULL_TIME: Long = 60000 * 2
    }
}
