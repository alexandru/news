import kotlinx.serialization.Serializable

@Serializable
data class Feed(
    val projectName: String,
    val projectUrl: String,
    val releasesUrl: String,
    val tags: List<String>
)

fun excludeMilestones(version: String): Boolean {
    return version.contains("(?i)\\d+\\.\\d+-(m|alpha|dev)[.-]?\\d+".toRegex())
}

fun excludeMilestonesOrReleaseCandidates(version: String): Boolean {
    return version.contains("(?i)\\d+\\.\\d+-(m|alpha|rc|dev)[.-]?\\d+".toRegex())
}
//
// val feeds = listOf(
//    Feed(
//        titleFormat = {
//            "Scala v%s was released!".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/scala/scala/releases.atom",
//        tags = listOf("Scala", "Programming", "Language"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            "New `sbt` release: v%s".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/sbt/sbt/releases.atom",
//        tags = listOf("sbt", "Scala", "Programming", "Tooling"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            val suffix = if (it.contains("\\b2\\.6\\.\\d+".toRegex())) " (open-source)" else " (proprietary)"
//            "Akka$suffix release: v%s".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/akka/akka/releases.atom",
//        tags = listOf("Akka", "Scala", "Programming", "Library"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            "Scala Native v%s released!".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/scala-native/scala-native/releases.atom",
//        tags = listOf("Scala", "ScalaNative", "Programming", "Language"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            "Scala.JS v%s released!".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/scala-js/scala-js/releases.atom",
//        tags = listOf("Scala", "ScalaJS", "JavaScript", "Programming", "Language"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            "Typelevel Cats release: v%s".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/typelevel/cats/releases.atom",
//        tags = listOf("Scala", "FP", "Typelevel", "Programming", "Library"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            "Typelevel Cats-Effect release: v%s".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/typelevel/cats-effect/releases.atom",
//        tags = listOf("Scala", "FP", "Typelevel", "Programming", "Library"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            "Typelevel Fs2 release: v%s".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/typelevel/fs2/releases.atom",
//        tags = listOf("Scala", "FP", "Typelevel", "Programming", "Library"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            "Http4s release: v%s".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/http4s/http4s/releases.atom",
//        tags = listOf("Scala", "FP", "Typelevel", "Programming", "Library"),
//        exclude = ::excludeMilestones
//    ),
//    Feed(
//        titleFormat = {
//            "Typelevel Doobie release: v%s".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/tpolecat/doobie/releases.atom",
//        tags = listOf("Scala", "FP", "Typelevel", "Programming", "Library"),
//        exclude = ::excludeMilestones
//    ),
//    Feed(
//        titleFormat = {
//            "Typelevel Circe release: v%s".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/circe/circe/releases.atom",
//        tags = listOf("Scala", "FP", "Typelevel", "Programming", "Library"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            "ScalaTest release: v%s".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/scalatest/scalatest/releases.atom",
//        tags = listOf("Scala", "Testing", "Programming", "Library"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            "ScalaCheck release: v%s".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/typelevel/scalacheck/releases.atom",
//        tags = listOf("Scala", "Testing", "Programming", "Library"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            "Arrow release: v%s".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/arrow-kt/arrow/releases.atom",
//        tags = listOf("Kotlin", "FP", "Programming", "Library"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            "Play Framework release: v%s".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/playframework/playframework/releases.atom",
//        tags = listOf("Scala", "Programming", "Library"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            "Finch release: v%s".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/finagle/finch/releases.atom",
//        tags = listOf("Typelevel", "FP", "Scala", "Programming", "Library"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            "sbt-typelevel release: v%s".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/typelevel/sbt-typelevel/releases.atom",
//        tags = listOf("Typelevel", "Scala", "Build", "Programming", "Tooling"),
//        startFrom = Instant.MIN,
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    ),
//    Feed(
//        titleFormat = {
//            "Kotlin v%s was released!".format(it.replace("^\\D+".toRegex(), ""))
//        },
//        url = "https://github.com/JetBrains/kotlin/releases.atom",
//        tags = listOf("Kotlin", "Programming", "Language"),
//        exclude = ::excludeMilestonesOrReleaseCandidates
//    )
// )
