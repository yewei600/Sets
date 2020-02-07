package com.ericwei.sets

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.ericwei.sets.di.AppComponent
import com.ericwei.sets.di.DaggerAppComponent

class MainActivity : AppCompatActivity() {

    lateinit var mAppComponent: AppComponent
    lateinit var mSharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        mAppComponent = DaggerAppComponent.factory().create(this)

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
