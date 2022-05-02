package com.youssef.weatherapp.model.repo

import android.content.Context
import com.youssef.weatherapp.model.datasources.localdatasource.LocalDataSourceInterface
import com.youssef.weatherapp.model.datasources.remotedatasource.RemoteDataSourceInterface

class Repository private constructor(
    var context: Context,
    var localSource: LocalDataSourceInterface,
    var remoteSource: RemoteDataSourceInterface
): RepositoryInterface {

    companion object {
        private var instance: RepositoryInterface? = null

        fun getInstance(context: Context, localSource: LocalDataSourceInterface, remoteSource: RemoteDataSourceInterface): RepositoryInterface {
            if(instance == null) {
                instance = Repository(context, localSource, remoteSource)
            }
            return instance as RepositoryInterface
        }
    }


}