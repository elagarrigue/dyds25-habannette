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
        val detailFake = ExternalMovieSourceGetDetailsFake()
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
        val popularFake = ExternalMoviesSourceGetPopularFake(movies = listOf(movie2)) // no debe usarse
        val detailFake = ExternalMovieSourceGetDetailsFake()
        val repo = MovieRepositoryImpl(local, popularFake, detailFake)

        val result = repo.getPopularMovies()

        assertEquals(listOf(movie1), result)
    }

    @Test
    fun `getPopularMovies retorna lista vacia si ocurre un error durante el fetch remoto`() = runTest {
        val local = MoviesLocalSourceFake()
        val popularFake = ExternalMoviesSourceGetPopularFake(shouldThrow = true)
        val detailFake = ExternalMovieSourceGetDetailsFake()
        val repo = MovieRepositoryImpl(local, popularFake, detailFake)

        val result = repo.getPopularMovies()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `getMovieDetails retorna pelicula si existe en remoto `() = runTest {
        val detailFake = ExternalMovieSourceGetDetailsFake(
            tmdbDetailsMap = mapOf(movie1.title to movie1)
        )
        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), ExternalMoviesSourceGetPopularFake(), detailFake)

        val result = repo.getMovieDetails(movie1.title)
        assertEquals(movie1, result)
    }



    @Test
    fun `getMovieDetails retorna pelicula fusionada si ambos servicios tienen datos`() = runTest {
        val detailFake = ExternalMovieSourceGetDetailsFake(
            tmdbDetailsMap = mapOf(movie1.title to movie1),
            omdbDetailsMap = mapOf(movie1.title to movie1)
        )
        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), ExternalMoviesSourceGetPopularFake(), detailFake)

        val result = repo.getMovieDetails(movie1.title)
        val expected = movie1.copy(
            overview = "TMDB: ${movie1.overview}\n\nOMDB: ${movie1.overview}"
        )
        assertEquals(expected,result)
    }

    @Test
    fun `getMovieDetails retorna null si ambos servicios no tienen datos`() = runTest {
        val detailFake = ExternalMovieSourceGetDetailsFake()
        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), ExternalMoviesSourceGetPopularFake(), detailFake)

        val result = repo.getMovieDetails(movie1.title)

        assertNull(result)
    }

    @Test
    fun `getMovieDetails retorna null si ocurre un error en el fetch remoto TMDB`() = runTest {
        val detailFake = ExternalMovieSourceGetDetailsFake(
            exceptionOnTMDBDetails = true
        )
        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), ExternalMoviesSourceGetPopularFake(), detailFake)

        val result = repo.getMovieDetails(movie1.title)

        assertNull(result)
    }

    @Test
    fun `getMovieDetails retorna null si ocurre un error en el fetch remoto OMDB`() = runTest {
        val detailFake = ExternalMovieSourceGetDetailsFake(
            exceptionOnOMDBDetails = true
        )
        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), ExternalMoviesSourceGetPopularFake(), detailFake)

        val result = repo.getMovieDetails(movie1.title)

        assertNull(result)
    }

    @Test
    fun `getMovieDetails retorna detalle de OMDB si TMDB lanza excepcion`() = runTest {
        val detailFake = ExternalMovieSourceGetDetailsFake(
            exceptionOnTMDBDetails = true,
            omdbDetailsMap = mapOf(movie1.title to movie1)
        )
        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), ExternalMoviesSourceGetPopularFake(), detailFake)

        val result = repo.getMovieDetails(movie1.title)

        assertEquals(movie1, result)
    }

    @Test
    fun `getMovieDetails retorna null si ambos servicios lanzan excepcion`() = runTest {
        val detailFake = ExternalMovieSourceGetDetailsFake(
            exceptionOnTMDBDetails = true,
            exceptionOnOMDBDetails = true
        )
        val repo = MovieRepositoryImpl(MoviesLocalSourceFake(), ExternalMoviesSourceGetPopularFake(), detailFake)

        val result = repo.getMovieDetails(movie1.title)

        assertNull(result)
    }
}
