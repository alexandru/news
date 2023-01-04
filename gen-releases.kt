///usr/bin/env jbang --quiet "$0" "$@" ; exit $?

//JAVA 17+
//KOTLIN 1.8.0
//DEPS org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0
//DEPS com.rometools:rome:1.18.0
//DEPS org.slf4j:slf4j-nop:1.7.32

import com.rometools.rome.feed.synd.SyndCategoryImpl
import com.rometools.rome.feed.synd.SyndContentImpl
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndEntryImpl
import com.rometools.rome.feed.synd.SyndFeedImpl
import com.rometools.rome.io.SyndFeedInput
import com.rometools.rome.io.SyndFeedOutput
import com.rometools.rome.io.XmlReader
import java.io.PrintWriter
import java.net.URL
import java.time.Instant
import java.util.*

data class Feed(
    val titleFormat: (String) -> String,
    val description: String,
    val url: String,
    val tags: List<String>,
    val startFrom: Instant,
    val exclude: (String) -> Boolean = { false }
)

fun excludeMilestones(version: String): Boolean {
    return version.contains("\\d+\\.\\d+-M\\d+".toRegex())
}

fun excludeMilestonesOrReleaseCandidates(version: String): Boolean {
    return version.contains("\\d+\\.\\d+-(M|RC)\\d+".toRegex())
}

val feeds = listOf(
    Feed(
        titleFormat = {
            "Scala v%s was released!".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Statically typed OOP+FP programming language.",
        url = "https://github.com/scala/scala/releases.atom",
        tags = listOf("Scala", "Programming"),
        startFrom = Instant.parse("2022-10-13T13:23:10Z"),
    ),
    Feed(
        titleFormat = {
            "New `sbt` release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Build tool for Scala and Java projects.",
        url = "https://github.com/sbt/sbt/releases.atom",
        tags = listOf("sbt", "Scala", "Programming"),
        startFrom = Instant.parse("2023-01-03T22:22:59Z"),
    ),
    Feed(
        titleFormat = {
            val suffix = if (it.contains("\\b2\\.6\\.\\d+".toRegex())) " (open-source)" else " (proprietary)"
            "Akka$suffix release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Toolkit for concurrent, distributed, message-driven applications.",
        url = "https://github.com/akka/akka/releases.atom",
        tags = listOf("Akka", "Scala", "Programming"),
        startFrom = Instant.parse("2022-12-22T15:49:34Z"),
        exclude = ::excludeMilestonesOrReleaseCandidates
    ),
    Feed(
        titleFormat = {
            "Scala Native v%s released!".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Scala to native code compiler.",
        url = "https://github.com/scala-native/scala-native/releases.atom",
        tags = listOf("Scala", "ScalaNative", "Programming"),
        startFrom = Instant.parse("2022-11-24T10:59:28Z"),
        exclude = ::excludeMilestonesOrReleaseCandidates
    ),
    Feed(
        titleFormat = {
            "Scala.JS v%s released!".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Scala to JavaScript compiler.",
        url = "https://github.com/scala-js/scala-js/releases.atom",
        tags = listOf("Scala", "ScalaJS", "JavaScript", "Programming"),
        startFrom = Instant.parse("2022-11-23T17:17:24Z"),
        exclude = ::excludeMilestonesOrReleaseCandidates
    ),
    Feed(
        titleFormat = {
            "Typelevel Cats release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Functional programming library for Scala.",
        url = "https://github.com/typelevel/cats-effect/releases.atom",
        tags = listOf("Scala", "FP", "Typelevel", "Programming"),
        startFrom = Instant.parse("2022-12-30T16:44:50Z"),
        exclude = ::excludeMilestonesOrReleaseCandidates
    ),
    Feed(
        titleFormat = {
            "Typelevel Cats Effect release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Pure asynchronous runtime for Scala (e.g., the IO monad).",
        url = "https://github.com/typelevel/cats/releases.atom",
        tags = listOf("Scala", "FP", "Typelevel", "Programming"),
        startFrom = Instant.parse("2022-11-12T21:20:14Z"),
        exclude = ::excludeMilestonesOrReleaseCandidates
    ),
    Feed(
        titleFormat = {
            "Typelevel Fs2 release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Functional streams for Scala.",
        url = "https://github.com/typelevel/fs2/releases.atom",
        tags = listOf("Scala", "FP", "Typelevel", "Programming"),
        startFrom = Instant.parse("2022-11-20T14:12:32Z"),
        exclude = ::excludeMilestonesOrReleaseCandidates
    ),
    Feed(
        titleFormat = {
            "Http4s release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Typeful, functional, streaming HTTP for Scala.",
        url = "https://github.com/http4s/http4s/releases.atom",
        tags = listOf("Scala", "FP", "Typelevel", "Programming"),
        startFrom = Instant.parse("2023-01-04T00:00:00Z"),
        exclude = ::excludeMilestones
    ),
    Feed(
        titleFormat = {
            "FreshRSS release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Self-hosted RSS/Atom reader.",
        url = "https://github.com/FreshRSS/FreshRSS/releases.atom",
        tags = listOf("SelfHosting"),
        startFrom = Instant.parse("2022-12-09T22:05:59Z")
    ),
    Feed(
        titleFormat = {
            "Typelevel Doobie release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Functional JDBC layer for Scala.",
        url = "https://github.com/tpolecat/doobie/releases.atom",
        tags = listOf("Scala", "FP", "Typelevel", "Programming"),
        startFrom = Instant.parse("2022-08-08T22:02:53Z"),
        exclude = ::excludeMilestones
    ),
    Feed(
        titleFormat = {
            "Typelevel Circe release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Functional JSON library for Scala.",
        url = "https://github.com/circe/circe/releases.atom",
        tags = listOf("Scala", "FP", "Typelevel", "Programming"),
        startFrom = Instant.parse("2022-09-16T16:11:06Z"),
        exclude = ::excludeMilestonesOrReleaseCandidates
    ),
    Feed(
        titleFormat = {
            "ScalaTest release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Testing toolkit for Scala.",
        url = "https://github.com/scalatest/scalatest/releases.atom",
        tags = listOf("Scala", "Testing", "Programming"),
        startFrom = Instant.parse("2022-09-29T13:55:15Z"),
        exclude = ::excludeMilestonesOrReleaseCandidates
    ),
    Feed(
        titleFormat = {
            "ScalaCheck release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Property-based testing for Scala.",
        url = "https://github.com/typelevel/scalacheck/releases.atom",
        tags = listOf("Scala", "Testing", "Programming"),
        startFrom = Instant.parse("2022-09-16T02:35:51+03:00"),
        exclude = ::excludeMilestonesOrReleaseCandidates
    ),
    Feed(
        titleFormat = {
            "Scalafix release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Refactoring and linting tool for Scala.",
        url = "https://github.com/scalacenter/scalafix/releases.atom",
        tags = listOf("Scala", "Testing", "Programming"),
        startFrom = Instant.parse("2022-10-10T21:27:27Z"),
        exclude = ::excludeMilestonesOrReleaseCandidates
    ),
    Feed(
        titleFormat = {
            "WartRemover release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Linter for Scala.",
        url = "https://github.com/wartremover/wartremover/releases.atom",
        tags = listOf("Scala", "Build", "Programming"),
        startFrom = Instant.parse("2022-11-20T08:51:14Z"),
        exclude = ::excludeMilestonesOrReleaseCandidates
    ),
    Feed(
        titleFormat = {
            "Scalafmt release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        description = "Code formatter for Scala.",
        url = "https://github.com/scalameta/scalafmt/releases.atom",
        tags = listOf("Scala", "Build", "Programming"),
        startFrom = Instant.parse("2022-10-31T22:41:02Z"),
        exclude = ::excludeMilestonesOrReleaseCandidates
    ),
)

fun main() {
    val allEntries = mutableListOf<SyndEntry>()
    for (feedSpec in feeds) {
        val feed = SyndFeedInput().build(XmlReader(URL(feedSpec.url)))
        for (entry in feed.entries) {
            val instant = entry.publishedDate?.toInstant() ?: entry.updatedDate.toInstant()
            if (instant < feedSpec.startFrom) {
                continue
            }
            if (feedSpec.exclude(entry.title)) {
                continue
            }
            val title = feedSpec.titleFormat(entry.title)
            val newEntry = SyndEntryImpl().apply {
                this.title = title
                this.link = entry.link
                this.publishedDate = entry.publishedDate
                this.updatedDate = entry.updatedDate
                this.description = SyndContentImpl().apply {
                    value = "\"${feedSpec.description}\" (${feedSpec.tags.joinToString(", ") { "#$it" }})"
                }
                this.author = entry.author
                this.categories = feedSpec.tags.map { tag ->
                    SyndCategoryImpl().apply {
                        name = tag
                    }
                }
            }
            allEntries.add(newEntry)
        }
    }

    allEntries.sortBy { -1 * (it.publishedDate?.time ?: it.updatedDate.time) }
    val allFeeds = SyndFeedImpl().apply {
        title = "Software releases"
        link = "https://news.alexn.org/releases.xml"
        description = "Aggregated feed"
        feedType = "atom_1.0"
        author = "Alexandru Nedelcu"
        entries = allEntries
        publishedDate = Date()
    }
    val output = SyndFeedOutput()
    output.output(allFeeds, PrintWriter(System.out))
}
