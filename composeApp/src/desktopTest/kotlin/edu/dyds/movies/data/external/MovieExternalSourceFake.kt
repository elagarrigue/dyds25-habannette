package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie

class MovieExternalSourceFake (
    private val movieDetailsMap: Map<String, Movie> = emptyMap(),
    private val exceptionGetMovieDetails: Boolean = false
    ) : MovieExternalSource {

    override suspend fun getMovieDetails(title: String): Movie? {
        if (exceptionGetMovieDetails) throw RuntimeException("Simulated error")
        return movieDetailsMap[title]
    }
}