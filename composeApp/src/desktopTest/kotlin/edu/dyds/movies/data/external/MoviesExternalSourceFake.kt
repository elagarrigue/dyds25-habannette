package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie

class MoviesExternalSourceFake (
    private val movieDetailsMap: Map<String, Movie> = emptyMap(),
    private val exceptionGetMovieDetails: Boolean = false
    ) : MoviesExternalSource {

    override suspend fun getMovieDetails(title: String): Movie? {
        if (exceptionGetMovieDetails) throw RuntimeException("Simulated error")
        return movieDetailsMap[title]
    }
    override suspend fun getMovies(): List<Movie> {
        if (exceptionGetMovieDetails) throw RuntimeException("Simulated error")
        return movieDetailsMap.values.toList()
    }
}