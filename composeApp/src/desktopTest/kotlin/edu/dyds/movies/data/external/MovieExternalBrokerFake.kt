package edu.dyds.movies.data.external
import edu.dyds.movies.domain.entity.Movie

class MovieExternalBrokerFake(
    private val tmdbMovies: List<Movie> = emptyList(),
    private val tmdbDetailsMap: Map<String, Movie> = emptyMap(),
    private val omdbDetailsMap: Map<String, Movie> = emptyMap(),
    private val exceptionOnTMDBMovies: Boolean = false,
    private val exceptionOnTMDBDetails: Boolean = false,
    private val exceptionOnOMDBDetails: Boolean = false
) : MoviesExternalSource {

    override suspend fun getMovies(): List<Movie> {
        if (exceptionOnTMDBMovies) throw RuntimeException("Simulated TMDB error")
        return tmdbMovies
    }

    override suspend fun getMovieDetails(title: String): Movie? {
        if (exceptionOnTMDBDetails) {
            if (exceptionOnOMDBDetails) throw RuntimeException("Both sources failed")
            return omdbDetailsMap[title]?.copy(overview = "OMDB: ${omdbDetailsMap[title]?.overview}")
        }

        val tmdbMovie = tmdbDetailsMap[title]
        val omdbMovie = if (!exceptionOnOMDBDetails) omdbDetailsMap[title] else null

        return when {
            tmdbMovie != null && omdbMovie != null -> Movie(
                id = tmdbMovie.id,
                title = tmdbMovie.title,
                overview = "TMDB: ${tmdbMovie.overview}\n\nOMDB: ${omdbMovie.overview}",
                releaseDate = tmdbMovie.releaseDate,
                poster = tmdbMovie.poster,
                backdrop = tmdbMovie.backdrop,
                originalTitle = tmdbMovie.originalTitle,
                originalLanguage = tmdbMovie.originalLanguage,
                popularity = (tmdbMovie.popularity + omdbMovie.popularity) / 2.0,
                voteAverage = (tmdbMovie.voteAverage + omdbMovie.voteAverage) / 2.0
            )
            tmdbMovie != null -> tmdbMovie.copy(overview = "TMDB: ${tmdbMovie.overview}")
            omdbMovie != null -> omdbMovie.copy(overview = "OMDB: ${omdbMovie.overview}")
            else -> null
        }
    }
}
