package edu.dyds.movies.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.dyds.movies.domain.usecase.GetMoviesDetailsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MoviesDetailViewModel(
    private val getMoviesDetailsUseCase: GetMoviesDetailsUseCase
) : ViewModel() {

    private val movieDetailStateMutableStateFlow = MutableStateFlow(MovieDetailUiState())

    val movieDetailStateFlow: Flow<MovieDetailUiState> = movieDetailStateMutableStateFlow


    fun getMovieDetail(id: Int) {
        viewModelScope.launch {
            movieDetailStateMutableStateFlow.emit(
                MovieDetailUiState(isLoading = true)
            )

            val movies = getMoviesDetailsUseCase.getMovieDetails(id)

            movieDetailStateMutableStateFlow.emit(
                MovieDetailUiState(
                    isLoading = false,
                    movie = movies
                )
            )
        }
    }

}