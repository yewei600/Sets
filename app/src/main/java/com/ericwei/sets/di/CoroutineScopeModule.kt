package com.ericwei.sets.di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

@Module
class CoroutineScopeModule {

    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(Dispatchers.Main + Job())
    }
}