package edu.dyds.movies.presentation.home

import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import kotlin.test.*


@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest {

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
            TestMovieFactory.createQualifiedMovie("title 1", true),
            TestMovieFactory.createQualifiedMovie("title 2", true),
            TestMovieFactory.createQualifiedMovie("title 3", true),
            TestMovieFactory.createQualifiedMovie("title 4", true),
            TestMovieFactory.createQualifiedMovie("title 5", true),
        )
        val viewModel = MoviesViewModel(FakeSuccessPopularUseCase(movies))

        viewModel.getAllMovies()

        advanceUntilIdle()

        val state = viewModel.moviesStateFlow.first { !it.isLoading }
        assertEquals(5, state.movies.size)
        state.movies.forEachIndexed { index, qualifiedMovie ->
            assertEquals("Test Movie title ${index + 1}", qualifiedMovie.movie.title)

        }
    }
}

object TestMovieFactory {
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

    fun createQualifiedMovie(title: String, isGood: Boolean): QualifiedMovie {
        return QualifiedMovie(
            movie = createMovie(title),
            isGoodMovie = isGood
        )
    }
}
