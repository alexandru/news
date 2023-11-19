import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.cli.ArgType
import kotlinx.cli.ExperimentalCli
import kotlinx.cli.Subcommand
import kotlinx.cli.required
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup

@Serializable
data class GitHubRepoInfo(
    val name: String,
    val description: String?,
    val topics: List<String>
)

@OptIn(ExperimentalCli::class)
class CrawlPageCommand : Subcommand(
    "crawl-page",
    "Crawl webpage for GitHub projects"
) {
    val url by option(
        ArgType.String,
        fullName = "url",
        shortName = "u",
        description = "URL to crawl"
    ).required()

    val token by option(
        ArgType.String,
        fullName = "token",
        shortName = "t",
        description = "GitHub token"
    ).required()

    override fun execute() = runBlocking {
        val jsonConfig = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }
        val feeds = mutableListOf<Feed>()
        HttpClient {
            install(ContentNegotiation) {
                json(jsonConfig)
            }
        }.use { client ->
            val response = client.get(url)
            val body = response.bodyAsText()
            val html = Jsoup.parse(body)
            val regex = "https://github.com/([^/]+)/([^/]+)/?$".toRegex()
            for (element in html.select("a[href]")) {
                val href = element.attr("href")
                val match = regex.matchEntire(href) ?: continue
                val (user, repo) = match.destructured
                val repoId = "$user/$repo"
                System.err.println("Fetching: $repoId")
                try {
                    val info = client
                        .get("https://api.github.com/repos/$repoId") { bearerAuth(token) }
                        .body<GitHubRepoInfo>()
                    feeds.add(
                        Feed(
                            projectName = info.name,
                            projectUrl = href,
                            releasesUrl = "https://github.com/$repoId/releases.atom",
                            tags = info.topics
                        )
                    )
                } catch (e: Exception) {
                    System.err.println("Failed to fetch: $repoId")
                    e.printStackTrace()
                }
            }
            println(jsonConfig.encodeToString(feeds))
        }
    }
}
