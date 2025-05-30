package edu.dyds.movies.data.local

import edu.dyds.movies.data.RemoteMovie

class LocalRepositoryImpl : LocalRepository{
    override suspend fun getMovies(cache : MutableList<RemoteMovie>):  MutableList<RemoteMovie>{
            return cache
    }
}