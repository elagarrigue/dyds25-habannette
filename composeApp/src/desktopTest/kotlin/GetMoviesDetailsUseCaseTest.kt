import edu.dyds.movies.domain.entity.Movie
import edu.dyds.movies.domain.repository.MoviesRepository
import edu.dyds.movies.domain.usecase.GetMoviesDetailsUseCase
import edu.dyds.movies.domain.usecase.GetMoviesDetailsUseCaseImpl
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class GetMoviesDetailsUseCaseTest {
    private lateinit var repository: MoviesRepository
    private lateinit var useCase: GetMoviesDetailsUseCase

    @BeforeTest
    fun setup() {
        val m1 = Movie(id=1, title="A", "algo", "11/6/2025",
            "algo", "algo", "PeliculaA","Ingles", 2.5, 8.0)
        repository = object: MoviesRepository {
            override suspend fun getPopularMovies(): List<Movie> = emptyList()
            override suspend fun getMovieDetails(id:Int): Movie? {
                if(id==m1.id)
                    return m1
                else
                    return null
            }
        }
        useCase = GetMoviesDetailsUseCaseImpl(repository)
    }

    @Test
    fun `getMoviesDetails retorna Movie cuando existe`() = runBlocking {
        //Arrange se genera en setup
        //Act
        val resultado = useCase.getMovieDetails(1)

        //Assert
        assertEquals(1, resultado?.id)
    }

    @Test
    fun `getMoviesDetails retorna null cuando no existe`() = runBlocking {
        //Arrange se genera en setup
        //Act
        val resultado = useCase.getMovieDetails(2)

        //Assert
        assertEquals(null, resultado)
    }
}