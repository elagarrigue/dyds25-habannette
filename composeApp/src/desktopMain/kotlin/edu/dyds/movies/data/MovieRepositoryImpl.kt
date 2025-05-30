package edu.dyds.movies.data

import edu.dyds.movies.data.external.ExternalRepository
import edu.dyds.movies.data.local.LocalRepository
import edu.dyds.movies.domain.repository.MoviesRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class MovieRepositoryImpl(
 //   private val httpClient: HttpClient,
    private val localRepository: LocalRepository,
    private val externalRepository: ExternalRepository
) : MoviesRepository {

    private val cacheMovies = mutableListOf<RemoteMovie>()

    override suspend fun getPopularMovies(): List<RemoteMovie> {
        return if (cacheMovies.isNotEmpty()) {
                localRepository.getMovies(cacheMovies)
        } else {
            try {
                externalRepository.getMovies(cacheMovies)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }


    override suspend fun getMovieDetails(id: Int): RemoteMovie? =
        externalRepository.getMovieDetails(id)


}