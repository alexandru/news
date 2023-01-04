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
import java.util.*

data class Feed(
    val titleFormat: (String) -> String,
    val url: String,
    val tags: List<String>
)

val feeds = listOf(
    Feed(
        titleFormat = {
            "Scala v%s was released!".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/scala/scala/releases.atom",
        tags = listOf("Scala", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            "New `sbt` release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/sbt/sbt/releases.atom",
        tags = listOf("sbt", "Scala", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            val suffix = if (it.contains("\\b2\\.6\\.\\d+".toRegex())) " (open-source)" else " (proprietary)"
            "Akka$suffix release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/akka/akka/releases.atom",
        tags = listOf("Akka", "Scala", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            "Scala Native v%s released!".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/scala-native/scala-native/releases.atom",
        tags = listOf("Scala", "ScalaNative", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            "Scala.JS v%s released!".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/scala-js/scala-js/releases.atom",
        tags = listOf("Scala", "ScalaJS", "JavaScript", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            "Typelevel Cats release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/typelevel/cats-effect/releases.atom",
        tags = listOf("Scala", "FP", "Typelevel", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            "Typelevel Cats Effect release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/typelevel/cats/releases.atom",
        tags = listOf("Scala", "FP", "Typelevel", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            "Typelevel Fs2 release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/typelevel/fs2/releases.atom",
        tags = listOf("Scala", "FP", "Typelevel", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            "Typelevel Http4s release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/http4s/http4s/releases.atom",
        tags = listOf("Scala", "FP", "Typelevel", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            "FreshRSS release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/FreshRSS/FreshRSS/releases.atom",
        tags = listOf("SelfHosting", "Release"),
    ),
    Feed(
        titleFormat = {
            "Typelevel Doobie release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/tpolecat/doobie/releases.atom",
        tags = listOf("Scala", "FP", "Typelevel", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            "Typelevel Circe release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/circe/circe/releases.atom",
        tags = listOf("Scala", "FP", "Typelevel", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            "ScalaTest release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/scalatest/scalatest/releases.atom",
        tags = listOf("Scala", "UnitTests", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            "ScalaTest release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/scalatest/scalatest/releases.atom",
        tags = listOf("Scala", "UnitTests", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            "Scalafix release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/scalacenter/scalafix/releases.atom",
        tags = listOf("Scala", "UnitTests", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            "WartRemover release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/wartremover/wartremover/releases.atom",
        tags = listOf("Scala", "Build", "Programming", "Release"),
    ),
    Feed(
        titleFormat = {
            "Scalafmt release: v%s".format(it.replace("^\\D+".toRegex(), ""))
        },
        url = "https://github.com/scalameta/scalafmt/releases.atom",
        tags = listOf("Scala", "Build", "Programming", "Release"),
    ),
)

fun main() {
    val allEntries = mutableListOf<SyndEntry>()
    for (feedSpec in feeds) {
        val feed = SyndFeedInput().build(XmlReader(URL(feedSpec.url)))
        for (entry in feed.entries) {
            val title = feedSpec.titleFormat(entry.title)
            val newEntry = SyndEntryImpl().apply {
                this.title = title
                this.link = entry.link
                this.publishedDate = entry.publishedDate
                this.updatedDate = entry.updatedDate
                this.description = SyndContentImpl().apply {
                    value = feedSpec.tags.map { "#" + it }.joinToString(", ")
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
    output.output(allFeeds, PrintWriter(System.out));
}
