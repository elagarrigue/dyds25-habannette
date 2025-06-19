import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.entity.QualifiedMovie
import edu.dyds.movies.domain.usecase.GetPopularMoviesUseCase
import edu.dyds.movies.presentation.home.MoviesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoviesViewModelTest {

    // Fakes

    class FakeSuccessPopularUseCase(
        private val movies: List<QualifiedMovie>
    ) : GetPopularMoviesUseCase {
        override suspend fun getPopularMovies(): List<QualifiedMovie> = movies
    }

    // Tests

    @Test
    fun `cuando se crea el ViewModel, el estado inicial no esta cargando y la lista de peliculas esta vacia`() {
        // Arrange
        val viewModel = MoviesViewModel(FakeSuccessPopularUseCase(emptyList()))

        // Act
        val initial = (viewModel.moviesStateFlow as kotlinx.coroutines.flow.MutableStateFlow).value

        // Assert
        assertFalse(initial.isLoading)
        assertTrue(initial.movies.isEmpty())
    }

    @Test
    fun `cuando getAllMovies es exitoso, el estado contiene la lista de peliculas y no esta cargando`() = runTest {
        // Arrange
        val movies = listOf(
            TestMovieFactory.createQualifiedMovie(1, true),
            TestMovieFactory.createQualifiedMovie(2, true)
        )
        val viewModel = MoviesViewModel(FakeSuccessPopularUseCase(movies))

        // Act
        viewModel.getAllMovies()

        // Assert
        val state = viewModel.moviesStateFlow.first { !it.isLoading }
        assertEquals(2, state.movies.size)
        assertEquals("Test Movie 1", state.movies[0].movie.title)
        assertEquals("Test Movie 2", state.movies[1].movie.title)
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