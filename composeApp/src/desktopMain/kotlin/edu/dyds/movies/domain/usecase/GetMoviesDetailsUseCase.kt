package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.repository.MoviesRepository
import edu.dyds.movies.data.local.toDomainMovie

class GetMoviesDetailsUseCase (
    private val repository: MoviesRepository
) {

    suspend fun getMovieDetails(id: Int) =
        try {
            repository.getMovieDetails(id)?.toDomainMovie()
        } catch (e: Exception) {
            null
        }
}