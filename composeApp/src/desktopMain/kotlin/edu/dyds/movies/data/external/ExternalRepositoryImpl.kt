package edu.dyds.movies.data.external

import edu.dyds.movies.data.RemoteMovie
import edu.dyds.movies.data.RemoteResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ExternalRepositoryImpl(private val httpClient: HttpClient): ExternalRepository {

    override suspend fun getMovies(): List<RemoteMovie> {
        val result = getTMDBPopularMovies().results
        return result
    }

    override suspend fun getMovieDetails(id: Int): RemoteMovie? {
        return try {
            getTMDBMovieDetails(id)
        } catch (e: Exception) {
            println("Error al traer las peliculas del repositorio externo : ${e.message}")
            null
        }
    }

    private suspend fun getTMDBMovieDetails(id: Int): RemoteMovie? =
        httpClient.get("/3/movie/$id").body()

    private suspend fun getTMDBPopularMovies(): RemoteResult =
        httpClient.get("/3/discover/movie?sort_by=popularity.desc").body()

}