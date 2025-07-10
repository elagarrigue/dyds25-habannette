package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.data.external.ExternalMoviesSourceGetDetail
import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class OMDBMoviesExternalSource(
    private val omdbHttpClient: HttpClient
) : ExternalMoviesSourceGetDetail {

    override suspend fun getMovieDetails(title: String): Movie? =
        getOMDBMovieDetails(title).toDomainMovie()


    private suspend fun getOMDBMovieDetails(title: String): OMDBRemoteMovie =
        omdbHttpClient.get("/?t=$title").body()

}