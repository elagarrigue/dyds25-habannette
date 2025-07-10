import edu.dyds.movies.data.MovieRepositoryImpl
import edu.dyds.movies.data.external.ExternalMovieSourceGetDetailsFake
import edu.dyds.movies.data.external.ExternalMoviesSourceGetPopularFake
import edu.dyds.movies.data.local.MoviesLocalSourceFake
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class MoviesRepositoryTest {

    private lateinit var movie1: Movie
    private lateinit var movie2: Movie

    @BeforeTest
    fun setup() {
        movie1 = Movie(1, "Matrix", "Sci-fi", "1999", "poster", null, "The Matrix", "en", 9.0, 8.7)
        movie2 = Movie(2, "Interstellar", "Space", "2014", "poster", null, "Interstellar", "en", 8.9, 8.6)
    }

    @Test
    fun `retorna peliculas del remoto si cache esta vacio y guarda en local`() = runTest {
        val local = MoviesLocalSourceFake()
        val popularFake = ExternalMoviesSourceGetPopularFake(movies = listOf(movie1, movie2))
        val detailFake = ExternalMovieSourceGetDetailsFake(null)
        val repo = MovieRepositoryImpl(local, popularFake, detailFake)

        val result = repo.getPopularMovies()

        assertEquals(2, result.size)
        assertTrue(local.getMovies().containsAll(listOf(movie1, movie2)))
    }

    @Test
    fun `retorna peliculas del cache si no esta vacio`() = runTest {
        val local = MoviesLocalSourceFake().apply {
            saveMovies(listOf(movie1))
        }
        val popularFake = ExternalMoviesSourceGetPopularFake(movies = listOf(movie2))
        val detailFake = ExternalMovieSourceGetDetailsFake(null)
        val repo = MovieRepositoryImpl(local, popularFake, detailFake)

        val result = repo.getPopularMovies()

        assertEquals(listOf(movie1), result)
    }

    @Test
    fun `getPopularMovies retorna lista vacia si ocurre un error durante el fetch remoto`() = runTest {
        val local = MoviesLocalSourceFake()
        val popularFake = ExternalMoviesSourceGetPopularFake(exceptionGetMovies = true)
        val detailFake = ExternalMovieSourceGetDetailsFake(null)
        val repo = MovieRepositoryImpl(local, popularFake, detailFake)

        val result = repo.getPopularMovies()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getMovieDetails retorna pelicula si existe en remoto `() = runTest {
        val detailFake = ExternalMovieSourceGetDetailsFake(
            movie=movie1
        )
        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), ExternalMoviesSourceGetPopularFake(), detailFake)

        val result = repo.getMovieDetails(movie1.title)
        assertEquals(movie1, result)
    }


    @Test
    fun `getMovieDetails retorna null si remote lanza excepcion`() = runTest {
        val detailFake = ExternalMovieSourceGetDetailsFake(
            movie=null,
            exceptionDetails = true,
        )
        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), ExternalMoviesSourceGetPopularFake(), detailFake)

        val result = repo.getMovieDetails(movie1.title)

        assertNull(result)
    }
}
