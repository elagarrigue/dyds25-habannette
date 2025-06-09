package edu.dyds.movies.data.local

import edu.dyds.movies.data.RemoteMovie

interface MoviesLocalSource {
    suspend fun getMovies():  MutableList<RemoteMovie>
    suspend fun isEmpty():Boolean
    suspend fun saveMovies(movies: List<RemoteMovie>)
}