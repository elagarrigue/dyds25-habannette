package presentation.home

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import edu.dyds.movies.presentation.home.MoviesViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest {

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

    class FakeSuccessPopularUseCase(
        private val movies: List<QualifiedMovie>
    ) : GetPopularMoviesUseCase {
        override suspend fun getPopularMovies(): List<QualifiedMovie> = movies
    }

    // Tests

    @Test
    fun `cuando se crea el ViewModel, el estado inicial no esta cargando y la lista de peliculas esta vacia`() {
        val viewModel = MoviesViewModel(FakeSuccessPopularUseCase(emptyList()))
        val initial = (viewModel.moviesStateFlow as MutableStateFlow).value

        assertFalse(initial.isLoading)
        assertTrue(initial.movies.isEmpty())
    }

    @Test
    fun `cuando getAllMovies es exitoso, el estado contiene 5 peliculas y no esta cargando`() = runTest {
        val movies = listOf(
            TestMovieFactory.createQualifiedMovie(1, true),
            TestMovieFactory.createQualifiedMovie(2, true),
            TestMovieFactory.createQualifiedMovie(3, true),
            TestMovieFactory.createQualifiedMovie(4, true),
            TestMovieFactory.createQualifiedMovie(5, true),
        )
        val viewModel = MoviesViewModel(FakeSuccessPopularUseCase(movies))

        viewModel.getAllMovies()

        advanceUntilIdle()

        val state = viewModel.moviesStateFlow.first { !it.isLoading }
        assertEquals(5, state.movies.size)
        state.movies.forEachIndexed { index, qualifiedMovie ->
            assertEquals("Test Movie ${index + 1}", qualifiedMovie.movie.title)
        }
    }
}

object TestMovieFactory {
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

    fun createQualifiedMovie(id: Int, isGood: Boolean): QualifiedMovie {
        return QualifiedMovie(
            movie = createMovie(id),
            isGoodMovie = isGood
        )
    }
}
