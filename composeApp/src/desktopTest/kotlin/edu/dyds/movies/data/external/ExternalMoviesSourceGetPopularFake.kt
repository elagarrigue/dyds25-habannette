package edu.dyds.movies.data.external
import edu.dyds.movies.domain.entity.Movie
class ExternalMoviesSourceGetPopularFake(
    private val movies: List<Movie> = emptyList(),
    private val exceptionGetMovies: Boolean = false
) : ExternalMoviesSourceGetPopular {
    override suspend fun getMovies(): List<Movie> {
        if (exceptionGetMovies) throw RuntimeException("Simulated fetch error")
        return movies
    }
}
