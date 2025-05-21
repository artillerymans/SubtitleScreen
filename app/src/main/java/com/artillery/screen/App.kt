package com.artillery.screen

import android.app.Application
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.MavericksViewModelConfigFactory

/*
 * 日期: 2025年05月20日 17:35:30
 * 作者: zhiwei.zhu
 * 描述: 
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()

        Mavericks.initialize(
            context = this,
            viewModelConfigFactory = MavericksViewModelConfigFactory(debugMode = true)
        )
    }

}