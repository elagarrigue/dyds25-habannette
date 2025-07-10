package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie

interface ExternalMoviesSourceGetPopular {
    suspend fun getMovies(): List<Movie>
}

interface ExternalMoviesSourceGetDetail {
    suspend fun getMovieDetails(title: String): Movie?
}

