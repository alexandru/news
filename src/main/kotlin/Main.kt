import com.rometools.rome.feed.synd.SyndCategoryImpl
import com.rometools.rome.feed.synd.SyndContentImpl
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndEntryImpl
import com.rometools.rome.feed.synd.SyndFeedImpl
import com.rometools.rome.feed.synd.SyndLinkImpl
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.SyndFeedOutput
import com.rometools.rome.io.XmlReader
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.multiple
import kotlinx.cli.required
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File
import java.io.PrintWriter
import java.net.URL
import java.time.Instant
import java.util.*

data class FeedOutput(
    val full: SyndEntry,
    val summary: SyndEntry,
    val instant: Instant
)

suspend fun processFeed(
    feedSpec: Feed,
    limitPerFeed: Int
): List<FeedOutput> =
    withContext(Dispatchers.IO) {
        @Suppress("DEPRECATION")
        val feed = SyndFeedInput().build(XmlReader(URL(feedSpec.url)))
        val allEntries = mutableListOf<FeedOutput>()
        for (entry in feed.entries) {
            val instant = entry.publishedDate?.toInstant()
                ?: entry.updatedDate.toInstant()
            if (instant < feedSpec.startFrom) {
                continue
            }
            if (feedSpec.exclude(entry.title)) {
                continue
            }
            val title = feedSpec.titleFormat(entry.title)
            val tags = feedSpec.tags + listOf("Releases")
            val newFullEntry = SyndEntryImpl().apply {
                this.title = title
                this.link = entry.link
                this.uri = entry.link
                this.publishedDate = entry.publishedDate
                this.updatedDate = entry.updatedDate
                this.description = SyndContentImpl().apply {
                    contents = entry.contents
                }
                this.author = entry.author
                this.categories = tags.map { tag ->
                    SyndCategoryImpl().apply {
                        name = tag
                    }
                }
            }
            val newSummaryEntry =
                (newFullEntry.clone() as SyndEntryImpl).apply {
                    contents = listOf()
                    description = SyndContentImpl().apply {
                        value = tags.joinToString(", ") { "#$it" }
                        type = "text/plain"
                    }
                }
            allEntries.add(
                FeedOutput(
                    full = newFullEntry,
                    summary = newSummaryEntry,
                    instant = instant
                )
            )
        }
        val sorted = allEntries
            .sortedBy {
                -1 * it.instant.toEpochMilli()
            }
        if (limitPerFeed > 0) {
            sorted.take(limitPerFeed)
        } else {
            sorted
        }
    }

private fun buildFeed(
    feedTitle: String,
    feedHref: String,
    allEntries: List<SyndEntry>
) =
    SyndFeedImpl().apply {
        title = feedTitle
        uri = feedHref
        description = "Aggregated feed"
        feedType = "atom_1.0"
        author = "Alexandru Nedelcu"
        entries = allEntries
        publishedDate = Date()
        links.add(
            SyndLinkImpl().apply {
                href = feedHref
                rel = "self"
                type = "application/atom+xml"
            }
        )
    }

fun main(args: Array<String>) = runBlocking {
    val parser = ArgParser("news")
    val feedTitle by parser
        .option(
            ArgType.String,
            fullName = "title",
            shortName = null,
            "Title of the generated feed"
        ).required()
    val tags by parser
        .option(
            ArgType.String,
            fullName = "tag",
            shortName = null,
            "Filter list of RSS/Atom feeds by tags"
        ).multiple().required()
    val delayMins by parser
        .option(
            ArgType.Int,
            fullName = "delay-mins",
            shortName = null,
            "Publishing delay in minutes"
        )
    val limitPerFeed by parser
        .option(
            ArgType.Int,
            fullName = "limit-per-feed",
            shortName = null,
            "Limit number of entries per feed"
        ).default(10)
    val host by parser
        .option(
            ArgType.String,
            fullName = "host",
            shortName = null,
            "Hostname"
        ).default("news.alexn.org")
    val outputPath by parser
        .option(
            ArgType.String,
            fullName = "output",
            shortName = null,
            "Output path"
        ).required()
    parser.parse(args)

    val gate = Semaphore(30)
    val tasks = feeds
        .filter { feed ->
            feed.tags.map { it.lowercase().trim() }
                .containsAll(
                    tags.map { it.lowercase().trim() }
                )
        }
        .map { feed ->
            async {
                gate.withPermit {
                    processFeed(feed, limitPerFeed = limitPerFeed)
                }
            }
        }
    val before = delayMins?.let {
        Instant.now().minusSeconds(it.toLong() * 60)
    }
    val allEntries = tasks
        .flatMap { it.await() }
        .filter {
            before == null || it.instant.isBefore(before)
        }
        .sortedBy {
            -1 * it.instant.toEpochMilli()
        }

    val outputFileFull = File(outputPath)
    val outputFileSummary =
        File(
            outputPath.replace(
                "\\.([^.]+)$".toRegex(),
                "-summary.$1"
            )
        )

    val feedHref = "https://$host/${outputFileFull.toPath().fileName}"
    val newFeedFull =
        buildFeed(feedTitle, feedHref, allEntries.map { it.full })
    val newFeedSummary = buildFeed(
        "$feedTitle (Summary)",
        feedHref,
        allEntries.map { it.summary }
    )
    withContext(Dispatchers.IO) {
        SyndFeedOutput().output(
            newFeedFull,
            PrintWriter(outputFileFull)
        )
        SyndFeedOutput().output(
            newFeedSummary,
            PrintWriter(outputFileSummary)
        )
    }
}
