package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie

class MoviesExternalSourceFake(
    private val movies: List<Movie> = emptyList(),
    private val exceptionGetMovies: Boolean = false
) : MoviesExternalSource {

    override suspend fun getMovies(): List<Movie> {
        if (exceptionGetMovies) throw RuntimeException("Simulated error")
        return movies
    }

}