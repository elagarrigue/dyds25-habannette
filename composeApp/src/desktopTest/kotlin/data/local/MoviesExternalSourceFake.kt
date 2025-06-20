package data.local

import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.domain.entity.Movie

class MoviesExternalSourceFake(
    private val movies: List<Movie> = emptyList(),
    private val movieDetailsMap: Map<Int, Movie> = emptyMap(),
    private val exceptionGetMovies: Boolean = false,
    private val exceptionGetMovieDetails: Boolean = false
) : MoviesExternalSource {

    override suspend fun getMovies(): List<Movie> {
        if (exceptionGetMovies) throw RuntimeException("Simulated error")
        return movies
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        if (exceptionGetMovieDetails) throw RuntimeException("Simulated error")
        return movieDetailsMap[id]
    }
}


