package edu.dyds.movies.data

import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.data.local.MoviesLocalSource
import edu.dyds.movies.domain.repository.MoviesRepository
import edu.dyds.movies.domain.entity.Movie


class MovieRepositoryImpl(
    private val moviesLocalSource: MoviesLocalSource,
    private val moviesExternalSource: MoviesExternalSource
) : MoviesRepository {


    override suspend fun getPopularMovies(): List<Movie> {
        try{
        if (moviesLocalSource.isEmpty()) {
            val remoteMovies = moviesExternalSource.getMovies()
            moviesLocalSource.saveMovies(remoteMovies)
        }
        return moviesLocalSource.getMovies().map {
            it.toDomainMovie()
        }
        } catch (e: Exception) {
            println("Error al hacer el fetch de las películas: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun getMovieDetails(id: Int): Movie? {
        return try {
            moviesExternalSource.getMovieDetails(id)?.toDomainMovie()
        } catch (e: Exception) {
            println("Error al obtener detalles de la película: ${e.message}")
            null
        }
    }


}