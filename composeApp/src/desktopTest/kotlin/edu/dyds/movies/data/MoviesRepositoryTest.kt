import edu.dyds.movies.data.MovieRepositoryImpl
import edu.dyds.movies.data.external.MovieExternalBrokerFake
import edu.dyds.movies.data.local.MoviesLocalSourceFake
import edu.dyds.movies.domain.entity.Movie
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class MoviesRepositoryTest {

    private lateinit var movie1: Movie
    private lateinit var movie2: Movie
    private lateinit var movieConFormato: Movie

    @BeforeTest
    fun setup() {
        movie1 = Movie(1, "Matrix", "Sci-fi", "1999", "poster", null, "The Matrix", "en", 9.0, 8.7)
        movie2 = Movie(2, "Interstellar", "Space", "2014", "poster", null, "Interstellar", "en", 8.9, 8.6)
    }

    @Test
    fun `retorna peliculas del remoto si cache esta vacio y guarda en local`() = runTest {
        val local = MoviesLocalSourceFake()
        val external = MovieExternalBrokerFake(tmdbMovies = listOf(movie1, movie2))
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
    fun `getMovieDetails retorna pelicula si existe en remoto (solo TMDB)`() = runTest {
        val fakeLocal = MoviesLocalSourceFake()
        val external = MovieExternalBrokerFake(
            tmdbDetailsMap = mapOf(movie1.title to movie1),
            omdbDetailsMap = emptyMap() // OMDB no tiene datos
        )

        val repo = MovieRepositoryImpl(fakeLocal, external)

        val result = repo.getMovieDetails(movie1.title)
        movieConFormato = Movie(1, "Matrix", "TMDB: Sci-fi", "1999", "poster", null, "The Matrix", "en", 9.0, 8.7)
        assertEquals(movieConFormato, result)
    }

    @Test
    fun `getMovieDetails retorna pelicula si existe en remoto (solo OMDB)`() = runTest {
        val fakeLocal = MoviesLocalSourceFake()
        val external = MovieExternalBrokerFake(
            tmdbDetailsMap = emptyMap(), // TMDB no tiene datos
            omdbDetailsMap = mapOf(movie1.title to movie1) // OMDB tiene datos
        )

        val repo = MovieRepositoryImpl(fakeLocal, external)

        val result = repo.getMovieDetails(movie1.title)
        movieConFormato = Movie(1, "Matrix", "OMDB: Sci-fi", "1999", "poster", null, "The Matrix", "en", 9.0, 8.7)
        assertEquals(movieConFormato, result)
    }

    @Test
    fun `getMovieDetails retorna pelicula fusionada si ambos servicios tienen datos`() = runTest {
        val fakeLocal = MoviesLocalSourceFake()
        val external = MovieExternalBrokerFake(
            tmdbDetailsMap = mapOf(movie1.title to movie1),
            omdbDetailsMap = mapOf(movie1.title to movie1.copy(overview = "OMDB: ${movie1.overview}"))
        )

        val repo = MovieRepositoryImpl(fakeLocal, external)

        val result = repo.getMovieDetails(movie1.title)

        assertEquals("TMDB: ${movie1.overview}\n\nOMDB: OMDB: ${movie1.overview}", result?.overview)
        assertEquals(movie1.title, result?.title)
    }

    @Test
    fun `getMovieDetails retorna null si ambos servicios no tienen datos`() = runTest {
        val fakeLocal = MoviesLocalSourceFake()
        val external = MovieExternalBrokerFake(
            tmdbDetailsMap = emptyMap(), // TMDB no tiene datos
            omdbDetailsMap = emptyMap()  // OMDB no tiene datos
        )

        val repo = MovieRepositoryImpl(fakeLocal, external)

        val result = repo.getMovieDetails(movie1.title)

        assertNull(result)
    }

    @Test
    fun `getMovieDetails retorna null si ocurre un error en el fetch remoto TMDB`() = runTest {
        val fakeExternal = MovieExternalBrokerFake(
            tmdbDetailsMap = emptyMap(), // TMDB no tiene datos
            exceptionOnTMDBMovies = true
        )
        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), fakeExternal)

        val result = repo.getMovieDetails("Inception")

        assertNull(result)
    }

    @Test
    fun `getMovieDetails retorna null si ocurre un error en el fetch remoto OMDB`() = runTest {
        val fakeExternal = MovieExternalBrokerFake(
            tmdbDetailsMap = emptyMap(), // TMDB no tiene datos
            omdbDetailsMap = emptyMap(), // OMDB no tiene datos
            exceptionOnOMDBDetails = true
        )
        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), fakeExternal)

        val result = repo.getMovieDetails("Inception")

        assertNull(result)
    }
}
