package com.anisoft.emarket

import android.app.Application
import com.anisoft.emarket.data.local.AppDatabase
import com.anisoft.emarket.data.repository.AppRepository

class EMarketApplication : Application() {

    private var database: AppDatabase? = null
    private var repository: AppRepository? = null

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        repository = AppRepository(database!!)
    }

    fun getRepository(): AppRepository = repository!!
}