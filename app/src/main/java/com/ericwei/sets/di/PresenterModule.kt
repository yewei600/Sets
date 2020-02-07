package com.ericwei.sets.di

import com.ericwei.sets.game.GameContract
import com.ericwei.sets.game.GamePresenter
import com.ericwei.sets.home.HomeContract
import com.ericwei.sets.home.HomePresenter
import com.ericwei.sets.rules.RulesContract
import com.ericwei.sets.rules.RulesPresenter
import dagger.Binds
import dagger.Module

@Module
abstract class PresenterModule {

    @Binds
    abstract fun provideHomePresenter(presenter: HomePresenter): HomeContract.Presenter

    @Binds
    abstract fun provideGamePresenter(presenter: GamePresenter): GameContract.Presenter

    @Binds
    abstract fun provideRulesPresenter(presenter: RulesPresenter): RulesContract.Presenter

}