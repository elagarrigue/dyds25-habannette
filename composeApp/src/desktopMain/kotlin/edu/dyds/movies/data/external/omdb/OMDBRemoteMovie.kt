package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.domain.entity.Movie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OMDBRemoteMovie(
    @SerialName("Title") val title: String,
    @SerialName("Plot") val overview: String?,
    @SerialName("Released") val releaseDate: String?,
    @SerialName("Poster") val poster: String?,
    @SerialName("imdbRating") val voteAverage: String?,
    @SerialName("imdbID") val imdbId: String,
    @SerialName("Language") val originalLanguage: String?,
    @SerialName("imdbVotes") val popularity: String?
) {
    fun toDomainMovie(): Movie {
        return Movie(
            id = 0,
            title = title,
            overview = overview ?: "",
            releaseDate = releaseDate ?: "",
            poster = poster ?: "",
            backdrop = null,
            originalTitle = title,
            originalLanguage = originalLanguage ?: "",
            popularity = 0.0,
            voteAverage = voteAverage?.toDoubleOrNull() ?: 0.0
        )
    }
}