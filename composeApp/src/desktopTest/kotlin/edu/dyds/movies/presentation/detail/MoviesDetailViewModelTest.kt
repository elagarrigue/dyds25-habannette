package edu.dyds.movies.presentation.detail

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMoviesDetailsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Fakes

    class FakeSuccessMoviesDetailUseCase(private val movie: Movie) : GetMoviesDetailsUseCase {
        override suspend fun getMovieDetails(title: String): Movie = movie
    }

    // Tests

    @Test
    fun `el estado inicial debe tener pelicula nula y no estar cargando`() {
        val viewModel = MoviesDetailViewModel(
            FakeSuccessMoviesDetailUseCase(TestDetailMovieFactory.createMovie("title 1"))
        )
        val state = viewModel.movieDetailStateFlow
        val initial = (state as MutableStateFlow).value

        assertFalse(initial.isLoading)
        assertNull(initial.movie)
    }

    @Test
    fun `al obtener getMovieDetail exitosamente, se emite loading y luego los datos`() = runTest {
        // Arrange
        val movie = TestDetailMovieFactory.createMovie("title 1")
        val viewModel = MoviesDetailViewModel(FakeSuccessMoviesDetailUseCase(movie))

        val events = mutableListOf<MoviesDetailUiState>()

        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.movieDetailStateFlow.drop(1).collect { state ->
                events.add(state)
            }
        }

        // Act
        viewModel.getMovieDetail("title 1")
        advanceUntilIdle()

        // Assert
        assertEquals(2, events.size)
        assertEquals(MoviesDetailUiState(isLoading = true, movie = null), events[0])
        assertEquals(MoviesDetailUiState(isLoading = false, movie = movie), events[1])
        collectJob.cancel()
    }
}

object TestDetailMovieFactory {
    fun createMovie(title: String): Movie {
        return Movie(
            id = title.hashCode(),
            title = "Test Movie $title",
            overview = "Test Overview $title",
            releaseDate = "2024-03-10",
            poster = "test_poster_$title",
            backdrop = null,
            originalTitle = "Original Test Title $title",
            originalLanguage = "en",
            popularity = 5.0,
            voteAverage = 5.0
        )
    }
}
