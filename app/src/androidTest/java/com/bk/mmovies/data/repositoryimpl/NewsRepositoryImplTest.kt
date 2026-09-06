package com.bk.mmovies.data.repositoryimpl

import androidx.test.platform.app.InstrumentationRegistry
import com.bk.mmovies.data.mapper.NewsMapper
import com.bk.mmovies.data.source.remote.NetworkManager
import com.bk.mmovies.data.source.remote.api.NewsApi
import com.bk.mmovies.data.source.remote.dto.ArticleDto
import com.bk.mmovies.data.source.remote.dto.NewsResponseDto
import com.bk.mmovies.data.source.remote.dto.NewsSourceDto
import com.bk.mmovies.domain.model.result.NewsResult
import com.bk.mmovies.locale.LocaleMonitor
import com.google.gson.Gson
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

/**
 * Repository-level tests. There is no JVM unit test precedent for a
 * repository in this project (NewsRepositoryImpl calls context.getString and
 * NewsMapper depends on LocaleMonitor, both of which need a real Android
 * Context), so - like NetworkTest.kt for the API layer - this runs
 * instrumented, using a fake NewsApi instead of MockWebServer since the
 * request/response wiring itself is already covered by NewsApiTest.
 */
class NewsRepositoryImplTest {

    private lateinit var fakeApi: FakeNewsApi
    private lateinit var repository: NewsRepositoryImpl
    private lateinit var newsMapper: NewsMapper

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        fakeApi = FakeNewsApi()
        val localeMonitor = LocaleMonitor(context)
        newsMapper = NewsMapper(localeMonitor)
        val networkManager = NetworkManager(Gson())
        repository = NewsRepositoryImpl(fakeApi, networkManager, newsMapper, context)
    }

    @Test
    fun getNews_success_mapsArticlesAndPreservesUrl() = runBlocking {
        fakeApi.topHeadlinesResponse = Response.success(
                NewsResponseDto(
                        status = "ok",
                        totalResults = 1,
                        articles = listOf(
                                ArticleDto(
                                        source = NewsSourceDto("the-verge", "The Verge"),
                                        author = "Jane Doe",
                                        title = "Movie News",
                                        description = "A description",
                                        url = "https://example.com/article",
                                        imageUrl = "https://example.com/image.jpg",
                                        publishedAt = "2024-01-15T13:45:00Z",
                                        content = "content"
                                          )
                                          )
                               )
                                                        )

        val result = repository.getNews()

        assertTrue(result is NewsResult.Success)
        val newsItems = (result as NewsResult.Success).newsItems
        assertEquals(1, newsItems.size)
        assertEquals("https://example.com/article", newsItems.first().articleUrl)
        assertEquals("Movie News", newsItems.first().title)
        assertEquals("The Verge", newsItems.first().sourceName)
    }

    @Test
    fun getNews_forwardsRequestedPage() = runBlocking {
        fakeApi.topHeadlinesResponse = Response.success(NewsResponseDto("ok", 0, emptyList()))

        repository.getNews(page = 3)

        assertEquals(3, fakeApi.lastPage)
    }

    @Test
    fun getNews_computesTotalPagesFromTotalResults() = runBlocking {
        // NEWS_PAGE_SIZE is 20, so 45 results span 3 pages (20/20/5).
        fakeApi.topHeadlinesResponse = Response.success(NewsResponseDto("ok", 45, emptyList()))

        val result = repository.getNews(page = 1) as NewsResult.Success

        assertEquals(1, result.page)
        assertEquals(3, result.totalPages)
    }

    @Test
    fun getNews_totalPagesNeverExceedsFreePlanResultCap() = runBlocking {
        // NEWS_PAGE_SIZE is 20 and the free-plan cap (NEWS_MAX_RESULTS) is
        // 100, so even though totalResults claims 500 results (25 pages
        // worth), NewsAPI itself refuses anything past the 100th result -
        // totalPages must be capped at 5, not 25.
        fakeApi.topHeadlinesResponse = Response.success(NewsResponseDto("ok", 500, emptyList()))

        val result = repository.getNews(page = 1) as NewsResult.Success

        assertEquals(5, result.totalPages)
    }

    @Test
    fun getNews_emptyArticles_returnsSuccessWithEmptyList() = runBlocking {
        fakeApi.topHeadlinesResponse = Response.success(NewsResponseDto("ok", 0, emptyList()))

        val result = repository.getNews()

        assertTrue(result is NewsResult.Success)
        assertTrue((result as NewsResult.Success).newsItems.isEmpty())
    }

    @Test
    fun getNews_nullArticlesField_returnsSuccessWithEmptyList() = runBlocking {
        fakeApi.topHeadlinesResponse = Response.success(NewsResponseDto("ok", null, null))

        val result = repository.getNews()

        assertTrue(result is NewsResult.Success)
        assertTrue((result as NewsResult.Success).newsItems.isEmpty())
    }

    @Test
    fun getNews_articlesMissingUrl_areDropped() = runBlocking {
        fakeApi.topHeadlinesResponse = Response.success(
                NewsResponseDto(
                        status = "ok",
                        totalResults = 2,
                        articles = listOf(
                                ArticleDto(null, null, "No url movie article", null, null, null, null, null),
                                ArticleDto(null, null, "Has url movie article", null, "https://example.com/x", null, null, null)
                                          )
                               )
                                                        )

        val result = repository.getNews() as NewsResult.Success

        assertEquals(1, result.newsItems.size)
        assertEquals("Has url movie article", result.newsItems.first().title)
    }

    // Goes through NewsMapper directly rather than the full repository: a
    // null title maps to "", and "" can never contain a required keyword, so
    // this article would be dropped by the repository's title-keyword filter
    // before these mapping defaults could be observed at all.
    @Test
    fun newsMapper_allNullableFieldsMissing_mapsToSafeDefaults() {
        val item = newsMapper.toModel(
                ArticleDto(
                        source = null,
                        author = null,
                        title = null,
                        description = null,
                        url = "https://example.com/only-url",
                        imageUrl = null,
                        publishedAt = null,
                        content = null
                          )
                                     )!!

        assertEquals("", item.title)
        assertEquals("", item.description)
        assertEquals("", item.imageUrl)
        assertEquals("", item.sourceName)
        assertEquals("", item.author)
        assertEquals("https://example.com/only-url", item.articleUrl)
    }

    @Test
    fun getNews_titleMissingRequiredKeyword_isFilteredOut() = runBlocking {
        fakeApi.topHeadlinesResponse = Response.success(
                NewsResponseDto(
                        status = "ok",
                        totalResults = 2,
                        articles = listOf(
                                ArticleDto(null, null, "Celebrity buys a new house", null, "https://example.com/off-topic", null, null, null),
                                ArticleDto(null, null, "New movie announced", null, "https://example.com/on-topic", null, null, null)
                                          )
                               )
                                                        )

        val result = repository.getNews() as NewsResult.Success

        assertEquals(1, result.newsItems.size)
        assertEquals("New movie announced", result.newsItems.first().title)
    }

    @Test
    fun getNews_httpFailure_returnsFailureResult() = runBlocking {
        fakeApi.topHeadlinesResponse = Response.error(
                401,
                "{\"status\":\"error\"}".toResponseBody(null)
                                                      )

        val result = repository.getNews()

        assertTrue(result is NewsResult.Failure)
    }

    @Test
    fun getNews_exception_returnsFailureResult() = runBlocking {
        fakeApi.throwOnTopHeadlines = true

        val result = repository.getNews()

        assertTrue(result is NewsResult.Failure)
    }

    private class FakeNewsApi : NewsApi {
        var topHeadlinesResponse: Response<NewsResponseDto> = Response.success(NewsResponseDto("ok", 0, emptyList()))
        var throwOnTopHeadlines = false
        var lastPage: Int? = null

        override suspend fun getTopHeadlines(
                country: String,
                category: String,
                page: Int,
                pageSize: Int,
                apiKey: String
                                             ): Response<NewsResponseDto> {
            lastPage = page
            if (throwOnTopHeadlines) throw java.io.IOException("network down")
            return topHeadlinesResponse
        }
    }
}
