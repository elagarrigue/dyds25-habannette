import data.local.MoviesExternalSourceFake
import edu.dyds.movies.data.MovieRepositoryImpl
import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.data.external.MoviesExternalSource
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class MovieRepositoryImplTest {

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
        val external = MoviesExternalSourceFake( listOf(movie1, movie2))
        val repo = MovieRepositoryImpl(local, external)

        val result = repo.getPopularMovies()

        assertEquals(2, result.size)
        assertTrue(local.getMovies().containsAll(listOf(movie1, movie2)))
    }

    @Test
    fun `retorna peliculas del cache si no esta vacio`() = runTest {
        val local = MoviesLocalSourceFake()
        local.saveMovies(listOf(movie1)) // precargar cache
        val external = MoviesExternalSourceFake(movies = listOf(movie2)) // no debe usarse
        val repo = MovieRepositoryImpl(local, external)

        val result = repo.getPopularMovies()

        assertEquals(listOf(movie1), result)
    }

    @Test
    fun `getPopularMovies retorna lista vacia si ocurre un error durante el fetch remoto`() = runTest {
        val externalConExcepcionSimulada = object : MoviesExternalSource {
            override suspend fun getMovies(): List<Movie> {
                throw RuntimeException("fallo de red")
            }

            override suspend fun getMovieDetails(id: Int): Movie? {
                throw RuntimeException("irrelevante")
            }
        }

        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), externalConExcepcionSimulada)

        val result = repo.getPopularMovies()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getMovieDetails retorna pelicula si existe en remoto`() = runTest {
        val external = MoviesExternalSourceFake(
            movieDetailsMap = mapOf(movie1.id to movie1)
        )
        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), external)

        val result = repo.getMovieDetails(movie1.id)

        assertEquals(movie1, result)
    }

    @Test
    fun `getMovieDetails retorna null si ocurre error en remoto`() = runTest {
        val externalConExcepcionSimulada = object : MoviesExternalSource {
            override suspend fun getMovies(): List<Movie> {
                throw RuntimeException("fallo de red")
            }

            override suspend fun getMovieDetails(id: Int): Movie? {
                throw RuntimeException("irrelevante")
            }
        }
        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), externalConExcepcionSimulada)
        val result = repo.getMovieDetails(0)

        assertEquals(null, result)
    }
}
