import edu.dyds.movies.data.MovieRepositoryImpl
import edu.dyds.movies.data.local.MoviesLocalSourceFake
import edu.dyds.movies.data.external.MovieExternalBrokerFake
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        val external = MovieExternalBrokerFake(listOf(movie1, movie2))
        val repo = MovieRepositoryImpl(local, external)

        val result = repo.getPopularMovies()

        assertEquals(2, result.size)
        assertTrue(local.getMovies().containsAll(listOf(movie1, movie2)))
    }

    @Test
    fun `retorna peliculas del cache si no esta vacio`() = runTest {
        val local = MoviesLocalSourceFake()
        local.saveMovies(listOf(movie1)) // precargar cache
        val external = MovieExternalBrokerFake(tmdbMovies = listOf(movie2)) // no debe usarse
        val repo = MovieRepositoryImpl(local, external)

        val result = repo.getPopularMovies()

        assertEquals(listOf(movie1), result)
    }

    @Test
    fun `getPopularMovies retorna lista vacia si ocurre un error durante el fetch remoto`() = runTest {
        val fakeLocal = MoviesLocalSourceFake()
        val fakeExternal = MovieExternalBrokerFake(
            tmdbMovies = emptyList(),
            exceptionOnTMDBMovies = true
        )
        val repo = MovieRepositoryImpl(fakeLocal, fakeExternal)

        val result = repo.getPopularMovies()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getMovieDetails retorna pelicula si existe en remoto`() = runTest {
        val fakeLocal = MoviesLocalSourceFake()
        val external = MovieExternalBrokerFake(
            tmdbDetailsMap = mapOf(movie1.title to movie1)
        )

        val repo = MovieRepositoryImpl(fakeLocal, external)

        val result = repo.getMovieDetails(movie1.title)

        assertEquals(movie1, result)
    }

    @Test
    fun `getMovieDetails retorna null si ocurre error en remoto`() = runTest {
        val fakeExternal = MovieExternalBrokerFake(
            tmdbDetailsMap = emptyMap(),
            exceptionOnTMDBMovies = true
        )
        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), fakeExternal)
        val result = repo.getMovieDetails("ejemplo")

        assertEquals(null, result)
    }
}
