package edu.dyds.movies.data.external

import edu.dyds.movies.domain.entity.Movie
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class MoviesExternalSourceImpl(private val httpClient: HttpClient) : MoviesExternalSource {

    override suspend fun getMovies(): List<Movie> {
        val result = getTMDBPopularMovies().results.map {
            it.toDomainMovie()
        }
        return result
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        return getTMDBMovieDetails(id)?.toDomainMovie()
    }

    private suspend fun getTMDBMovieDetails(id: Int): RemoteMovie? =
        httpClient.get("/3/movie/$id").body()

    private suspend fun getTMDBPopularMovies(): RemoteResult =
        httpClient.get("/3/discover/movie?sort_by=popularity.desc").body()

}