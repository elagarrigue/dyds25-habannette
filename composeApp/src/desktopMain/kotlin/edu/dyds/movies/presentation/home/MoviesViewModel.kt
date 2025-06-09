package edu.dyds.movies.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.dyds.movies.data.RemoteMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class MoviesViewModel(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase
) : ViewModel() {

    private val moviesStateMutableStateFlow = MutableStateFlow(MoviesUiState())


    val moviesStateFlow: Flow<MoviesUiState> = moviesStateMutableStateFlow


    fun getAllMovies() {
        viewModelScope.launch {
            moviesStateMutableStateFlow.emit(
                MoviesUiState(isLoading = true)
            )

            val movies = getPopularMoviesUseCase.getPopularMovies()

            moviesStateMutableStateFlow.emit(
                MoviesUiState(
                    isLoading = false,
                    movies = movies
                )
            )
        }
    }

}