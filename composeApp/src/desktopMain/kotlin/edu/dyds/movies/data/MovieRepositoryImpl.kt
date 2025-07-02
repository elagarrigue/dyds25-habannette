package edu.dyds.movies.data

import edu.dyds.movies.data.external.MoviesExternalSource
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository


class MovieRepositoryImpl(
    private val moviesLocalSource: MoviesLocalSource,
    private val moviesExternalSource: MoviesExternalSource
) : MoviesRepository {


    override suspend fun getPopularMovies(): List<Movie> {
        try {
            if (moviesLocalSource.isEmpty()) {
                val remoteMovies = moviesExternalSource.getMovies()
                moviesLocalSource.saveMovies(remoteMovies)
            }
            return moviesLocalSource.getMovies()
        } catch (e: Exception) {
            println("Error al hacer el fetch de las peliculas: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun getMovieDetails(title: String): Movie? {
        return try {
            moviesExternalSource.getMovieDetails(title)
        } catch (e: Exception) {
            println("Error al obtener detalles de la pelicula: ${e.message}")
            null
        }
    }


}