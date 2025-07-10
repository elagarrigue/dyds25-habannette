package edu.dyds.movies.data.external
import edu.dyds.movies.domain.entity.Movie

class ExternalMovieSourceGetDetailsFake(
    private val movie: Movie?,
    private val exceptionDetails: Boolean = false,
) : ExternalMoviesSourceGetDetail {

    override suspend fun getMovieDetails(title: String): Movie? {
        if (exceptionDetails) throw RuntimeException("Simulated fetch error")
        if (movie?.title == title) return movie
        return null
    }
}
