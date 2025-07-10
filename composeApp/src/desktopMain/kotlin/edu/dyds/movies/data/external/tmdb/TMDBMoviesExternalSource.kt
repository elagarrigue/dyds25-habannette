package edu.dyds.movies.data.external.tmdb

import edu.dyds.movies.data.external.ExternalMoviesSourceGetDetail
import edu.dyds.movies.data.external.ExternalMoviesSourceGetPopular
import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class TMDBMoviesExternalSource(
    private val httpClient: HttpClient
) : ExternalMoviesSourceGetPopular,ExternalMoviesSourceGetDetail {

    override suspend fun getMovies(): List<Movie> {
        val response: TMDBMovieListResponse = httpClient
            .get("/3/discover/movie?sort_by=popularity.desc")
            .body()
        return response.results.map { it.toDomainMovie() }
    }

    override suspend fun getMovieDetails(title: String): Movie? {
        val response: TMDBMovieListResponse = httpClient
            .get("/3/search/movie?query=$title")
            .body()
        return response.results.firstOrNull()?.toDomainMovie()
    }
}

@Serializable
private data class TMDBMovieListResponse(
    @SerialName("results")
    val results: List<TMDBRemoteMovie>
)