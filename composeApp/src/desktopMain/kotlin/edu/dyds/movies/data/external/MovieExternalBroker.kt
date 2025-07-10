package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie


class MovieExternalBroker(
    private val tmdb: ExternalMoviesSourceGetDetail,
    private val omdb: ExternalMoviesSourceGetDetail
) : ExternalMoviesSourceGetDetail {

    private suspend fun getTMDBMovieDetails(title: String): Movie?{
        return try {
            tmdb.getMovieDetails(title)
        } catch (e: Exception) {
            println("Error al obtener detalles desde TMDB: ${e.message}")
            null
        }
    }

    private suspend fun getOMDBMovieDetails(title: String): Movie?{
        return try {
            omdb.getMovieDetails(title)
        } catch (e: Exception) {
            println("Error al obtener detalles desde OMDB: ${e.message}")
            null
        }
    }

    override suspend fun getMovieDetails(title: String): Movie? {
        val tmdbMovie = getTMDBMovieDetails(title)
        val omdbMovie = getOMDBMovieDetails(title)

        return when {
            tmdbMovie != null && omdbMovie != null -> buildMovie(tmdbMovie, omdbMovie)
            tmdbMovie != null -> tmdbMovie.copy(overview = "TMDB: ${tmdbMovie.overview}")
            omdbMovie != null -> omdbMovie.copy(overview = "OMDB: ${omdbMovie.overview}")
            else -> null
        }
    }

    private fun buildMovie(
        tmdbMovie: Movie,
        omdbMovie: Movie
    ) =
        Movie(
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

}
