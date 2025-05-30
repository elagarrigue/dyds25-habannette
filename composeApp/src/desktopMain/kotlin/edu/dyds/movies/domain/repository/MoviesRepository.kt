package edu.dyds.movies.domain.repository

import edu.dyds.movies.data.RemoteMovie

interface MoviesRepository {
    suspend fun getPopularMovies(): List<RemoteMovie>
    suspend fun getMovieDetails(id: Int): RemoteMovie?
}