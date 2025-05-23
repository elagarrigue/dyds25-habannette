package edu.dyds.movies.data.local


import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*


class MovieRepositoryImpl(
    private val httpClient: HttpClient
) : MoviesRepository {

    private val cacheMovies = mutableListOf<RemoteMovie>()

    override suspend fun getPopularMovies(): List<RemoteMovie> {
        return if (cacheMovies.isNotEmpty()) {
            cacheMovies
        } else {
            try {
                val result = getTMDBPopularMovies().results
                cacheMovies.clear()
                cacheMovies.addAll(result)
                result
            } catch (e: Exception) {
                emptyList()
            }
        }
    }



    override suspend fun getMovieDetails(id: Int): RemoteMovie? =
        try {
            getTMDBMovieDetails(id)
        } catch (e: Exception) {
            null
        }

    private suspend fun getTMDBMovieDetails(id: Int): RemoteMovie? =
        httpClient.get("/3/movie/$id").body()

    private suspend fun getTMDBPopularMovies(): RemoteResult =
        httpClient.get("/3/discover/movie?sort_by=popularity.desc").body()


}