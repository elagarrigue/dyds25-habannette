
package edu.dyds.movies.data.external
import edu.dyds.movies.domain.entity.Movie


class MovieExternalBroker(
    private val tmdb: MoviesExternalSource,
    private val omdb: ExternalMoviesSourceGetDetail
) : MoviesExternalSource {

    override suspend fun getMovies(): List<Movie> {
        return tmdb.getMovies()
    }

    override suspend fun getMovieDetails(title: String): Movie? {
        return tmdb.getMovieDetails(title)
            ?: omdb.getMovieDetails(title)
    }
}
