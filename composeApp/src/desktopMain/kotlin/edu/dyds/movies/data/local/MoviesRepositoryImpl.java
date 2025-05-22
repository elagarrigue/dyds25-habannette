package edu.dyds.movies.data.repository

import edu.dyds.movies.data.DataEntities.RemoteMovie
import edu.dyds.movies.data.DataEntities.RemoteResult
import edu.dyds.movies.data.mappers.toDomain
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.local.MoviesRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class MovieRepositoryImpl(
        private val httpClient: HttpClient
) : MoviesRepository {

    private val cacheMovies = mutableListOf<RemoteMovie>()

    override suspend fun getPopularMovies(): List<QualifiedMovie> =
    if (cacheMovies.isNotEmpty()) {
        cacheMovies.sortAndMap()
    } else {
        try {
            getTMDBPopularMovies().results.apply {
                cacheMovies.clear()
                cacheMovies.addAll(this)
            }.sortAndMap()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovieDetails(id: Int): Movie? =
    try {
        getTMDBMovieDetails(id).toDomain()
    } catch (e: Exception) {
        null
    }

    private suspend fun getTMDBMovieDetails(id: Int): RemoteMovie =
            httpClient.get("/3/movie/$id").body()

    private suspend fun getTMDBPopularMovies(): RemoteResult =
            httpClient.get("/3/discover/movie?sort_by=popularity.desc").body()

    
}
