package edu.dyds.movies.data.external

import edu.dyds.movies.data.RemoteMovie

interface ExternalRepository {
        suspend fun getMovies(cache: MutableList<RemoteMovie>):  MutableList<RemoteMovie>
        suspend fun getMovieDetails(id: Int): RemoteMovie?
}
