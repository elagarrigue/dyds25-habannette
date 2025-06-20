package presentation.detail

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.usecase.GetMoviesDetailsUseCase
import edu.dyds.movies.presentation.detail.MoviesDetailViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Fakes

    class FakeSuccessMoviesDetailUseCase(private val movie: Movie) : GetMoviesDetailsUseCase {
        override suspend fun getMovieDetails(id: Int): Movie = movie
    }

    // Tests

    @Test
    fun `el estado inicial debe tener pelicula nula y no estar cargando`() {
        val viewModel = MoviesDetailViewModel(
            FakeSuccessMoviesDetailUseCase(TestDetailMovieFactory.createMovie(1))
        )
        val state = viewModel.movieDetailStateFlow
        val initial = (state as MutableStateFlow).value

        assertFalse(initial.isLoading)
        assertNull(initial.movie)
    }

    @Test
    fun `al obtener getMovieDetail exitosamente, el estado contiene la pelicula y no esta cargando`() = runTest {
        val movie = TestDetailMovieFactory.createMovie(1)
        val viewModel = MoviesDetailViewModel(FakeSuccessMoviesDetailUseCase(movie))

        viewModel.getMovieDetail(1)

        advanceUntilIdle()

        val resultState = viewModel.movieDetailStateFlow.first { !it.isLoading }
        assertEquals(movie, resultState.movie)
    }
}

object TestDetailMovieFactory {
    fun createMovie(id: Int): Movie {
        return Movie(
            id = id,
            title = "Test Movie $id",
            overview = "Test Overview $id",
            releaseDate = "2024-03-10",
            poster = "test_poster_$id",
            backdrop = null,
            originalTitle = "Original Test Title $id",
            originalLanguage = "en",
            popularity = 5.0,
            voteAverage = 5.0
        )
    }
}
