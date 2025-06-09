package edu.dyds.movies.data.local

import edu.dyds.movies.data.RemoteMovie

class MoviesLocalSourceImpl : MoviesLocalSource{
    private val cacheMovies = mutableListOf<RemoteMovie>()
    override suspend fun getMovies():  MutableList<RemoteMovie>{
            return cacheMovies
    }
    override suspend fun isEmpty(): Boolean {
        return cacheMovies.isEmpty()
    }
    override suspend fun saveMovies(movies: List<RemoteMovie>) {
        cacheMovies.clear()
        getMovies().addAll(movies)
    }
}