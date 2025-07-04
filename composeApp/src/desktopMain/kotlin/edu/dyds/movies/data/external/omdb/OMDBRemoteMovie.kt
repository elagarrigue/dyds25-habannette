package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.domain.entity.Movie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OMDBRemoteMovie(
    @SerialName("Response") val response: String? = null,
    @SerialName("Error") val error: String? = null,
    @SerialName("Title") val title: String? = null,
    @SerialName("Plot") val plot: String? = null,
    @SerialName("Released") val released: String? = null,
    @SerialName("Year") val year: String? = null,
    @SerialName("Poster") val poster: String? = null,
    @SerialName("Language") val language: String? = null,
    @SerialName("Metascore") val metaScore: String? = null,
    @SerialName("imdbRating") val imdbRating: String? = null
) {
    private fun isSuccess(): Boolean = response.equals("True", ignoreCase = true)

    fun toDomainMovie(): Movie? {
        if (!isSuccess()) return null

        return Movie(
            id = (title ?: "").hashCode(),
            title = title ?: "",
            overview = plot ?: "",
            releaseDate = if (!released.isNullOrEmpty() && released != "N/A") released else (year ?: ""),
            poster = poster ?: "",
            backdrop = poster ?: "",
            originalTitle = title ?: "",
            originalLanguage = language ?: "",
            popularity = imdbRating?.toDoubleOrNull() ?: 0.0,
            voteAverage = if (!metaScore.isNullOrEmpty() && metaScore != "N/A") {
                metaScore.toDoubleOrNull() ?: 0.0
            } else 0.0
        )
    }
}
