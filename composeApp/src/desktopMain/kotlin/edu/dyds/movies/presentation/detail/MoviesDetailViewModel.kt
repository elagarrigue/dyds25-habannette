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

    private val movieDetailStateMutableStateFlow = MutableStateFlow(MoviesDetailUiState())

    val movieDetailStateFlow: Flow<MoviesDetailUiState> = movieDetailStateMutableStateFlow


    fun getMovieDetail(id: Int) {
        viewModelScope.launch {
            movieDetailStateMutableStateFlow.emit(
                MoviesDetailUiState(isLoading = true)
            )

            val movies = getMoviesDetailsUseCase.getMovieDetails(id)

            movieDetailStateMutableStateFlow.emit(
                MoviesDetailUiState(
                    isLoading = false,
                    movie = movies
                )
            )
        }
    }

}