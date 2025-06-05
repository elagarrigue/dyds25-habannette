package edu.dyds.movies.data

import edu.dyds.movies.data.external.ExternalRepository
import edu.dyds.movies.data.local.LocalRepository
import edu.dyds.movies.domain.repository.MoviesRepository
import kotlin.collections.mutableListOf

class MovieRepositoryImpl(
    private val localRepository: LocalRepository,
    private val externalRepository: ExternalRepository
) : MoviesRepository {

    private val cacheMovies = mutableListOf<RemoteMovie>()

    override suspend fun getPopularMovies(): MutableList<RemoteMovie> {
        return try {
            if (cacheMovies.isNotEmpty()) {
                localRepository.getMovies(cacheMovies)
            } else {
                cacheMovies.clear()
                cacheMovies.addAll(externalRepository.getMovies())
            }
            cacheMovies
        } catch (e: Exception) {
            println("Error al hacer el fetch de las películas: ${e.message}")
            mutableListOf()
        }
    }



    override suspend fun getMovieDetails(id: Int): RemoteMovie? =
        externalRepository.getMovieDetails(id)


}