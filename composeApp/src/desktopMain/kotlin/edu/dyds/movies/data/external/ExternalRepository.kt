package edu.dyds.movies.data.external

import edu.dyds.movies.data.RemoteMovie

interface ExternalRepository {
        suspend fun getMovies(): List<RemoteMovie>
        suspend fun getMovieDetails(id: Int): RemoteMovie?
}
