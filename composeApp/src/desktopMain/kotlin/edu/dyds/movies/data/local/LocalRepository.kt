package edu.dyds.movies.data.local

import edu.dyds.movies.data.RemoteMovie
import kotlin.collections.mutableListOf

interface LocalRepository {
    suspend fun getMovies(cache :MutableList<RemoteMovie> ):  MutableList<RemoteMovie>
}