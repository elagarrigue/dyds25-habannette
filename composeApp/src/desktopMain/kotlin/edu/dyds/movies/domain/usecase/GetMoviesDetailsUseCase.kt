package edu.dyds.movies.domain.usecase

import edu.dyds.movies.domain.repository.MoviesRepository
import edu.dyds.movies.domain.entity.Movie


interface GetMoviesDetailsUseCase {
    suspend fun getMovieDetails(id: Int): Movie?
}

class GetMoviesDetailsUseCaseImpl (
    private val repository: MoviesRepository
) : GetMoviesDetailsUseCase {

    override suspend fun getMovieDetails(id: Int) =
        repository.getMovieDetails(id)

}