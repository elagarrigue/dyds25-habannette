package edu.dyds.movies.data.external.omdb

import edu.dyds.movies.data.external.MoviesExternalSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import edu.dyds.movies.domain.entity.Movie

class OMDBMoviesDetailSource(
    private val client: HttpClient
) : MoviesExternalSource {

    override suspend fun getMovieDetails(title: String): Movie? {
        val response = client.get {
            parameter("t", title)
        }
        return response.body<OMDBRemoteMovie>().toDomainMovie()
    }

    override suspend fun getMovies(): List<Movie> {
        throw NotImplementedError("getMovies no está implementado")
    }

}