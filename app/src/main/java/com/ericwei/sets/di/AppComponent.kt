package com.ericwei.sets.di

import android.content.Context
import com.ericwei.sets.game.GameFragment
import com.ericwei.sets.home.HomeFragment
import com.ericwei.sets.rules.RulesFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [PresenterModule::class, CoroutineScopeModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(fragment: HomeFragment)

    fun inject(fragment: GameFragment)

    fun inject(fragment: RulesFragment)

}