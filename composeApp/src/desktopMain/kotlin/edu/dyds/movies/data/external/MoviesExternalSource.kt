package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie

interface MoviesExternalSource {
    suspend fun getMovies(): List<Movie>
    suspend fun getMovieDetails(id: Int): Movie?
}
