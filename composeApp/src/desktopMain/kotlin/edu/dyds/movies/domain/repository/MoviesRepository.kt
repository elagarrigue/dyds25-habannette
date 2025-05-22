package edu.dyds.movies.domain.repository

import edu.dyds.movies.domain.entity.RemoteMovie

interface MoviesRepository {
    //quedarian mejor los nombres sin TMDB
    suspend fun getPopularMovies(): List<RemoteMovie>
    suspend fun getMovieDetails(id: Int): RemoteMovie
}