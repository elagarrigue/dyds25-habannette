package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class MoviesExternalSourceImpl(private val httpClient: HttpClient): MoviesExternalSource {

    override suspend fun getMovies(): List<Movie> {
        val result = getTMDBPopularMovies().results.map {
            it.toDomainMovie()
        }
        return result
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        return try {
            getTMDBMovieDetails(id)?.toDomainMovie()
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