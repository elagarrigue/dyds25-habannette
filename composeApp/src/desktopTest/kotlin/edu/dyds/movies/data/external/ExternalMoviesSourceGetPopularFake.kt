package edu.dyds.movies.data.external
import edu.dyds.movies.domain.entity.Movie
class ExternalMoviesSourceGetPopularFake(
    private val movies: List<Movie> = emptyList(),
    private val shouldThrow: Boolean = false
) : ExternalMoviesSourceGetPopular {
    override suspend fun getMovies(): List<Movie> {
        if (shouldThrow) throw RuntimeException("Simulated TMDB fetch error")
        return movies
    }
}
