package com

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.cookie
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File
import java.text.SimpleDateFormat
import java.util.Properties

const val FILE_NAME = "script.sh"
const val CHUNK_SIZE = 10
const val START_DATE = "1970-01-01"
val config = loadConfig()
val DOMAIN = config.getProperty("domain") ?: throw IllegalArgumentException("domain을 입력해주세요.")
val SESSION = config.getProperty("session") ?: throw IllegalArgumentException("session을 입력해주세요.")

suspend fun main() {
    val userName = print("userName: ").let {
        readlnOrNull() ?: throw IllegalArgumentException("사용자명을 입력해주세요.")
    }
    val year = print("year: ").let {
        readlnOrNull()?.toIntOrNull() ?: throw IllegalArgumentException("년도를 입력해주세요.")
    }

    val contributions = getContributions(userName, year).also {
        println("총 ${it.size}개의 기여도를 찾았습니다.")
    }
    contributions.chunked(CHUNK_SIZE).forEachIndexed { index, chunked ->
        val command = "git pull origin main\n" + chunked.joinToString("") { it.toCommitCommand() } + "git push -f origin main"
        File(FILE_NAME).writeText(command).run {
            withContext(Dispatchers.IO) {
                Runtime.getRuntime().exec("sh $FILE_NAME").waitFor()
            }
        }

        println("$index / ${contributions.size / CHUNK_SIZE} 완료")
    }
}

suspend fun getContributions(userName: String, year: Int): List<Contribution> {
    val url = "$DOMAIN/${userName}?tab=overview&from=${year}-12-01&to=${year}-12-31"
    return HttpClient(CIO).get(url) {
        cookie("user_session", SESSION)
    }.let {
        parseContributions(it.bodyAsText())
    }
}

suspend fun getLastCommitDateOrNull(): String? {
    return withContext(Dispatchers.IO) {
        Runtime.getRuntime().exec("git log -1 --pretty=%B").inputStream.bufferedReader().readText().let {
            if (isDateFormat(it)) {
                it
            } else {
                null
            }
        }
    }
}

suspend fun parseContributions(html: String): List<Contribution> {
    val contributionCalendarDays = Jsoup.parse(html).select("td.ContributionCalendar-day")
    return contributionCalendarDays.map { day ->
        Contribution(
            date = day.attr("data-date"),
            count = day.select("span.sr-only").text().split(" contribution")[0].toIntOrNull() ?: 0,
        )
    }.sortedBy { it.date }
        .filter { it.count > 0 }
        .filter { it.date > (getLastCommitDateOrNull() ?: START_DATE) }
}

fun isDateFormat(date: String, format: String = "yyyy-MM-dd"): Boolean {
    return try {
        SimpleDateFormat(format).parse(date)
        true
    } catch (e: Exception) {
        false
    }
}

fun loadConfig(): Properties {
    val properties = Properties()
    properties.load(File("config.properties").inputStream())
    return properties
}

data class Contribution(
    val date: String,
    val count: Int,
) {
    fun toCommitCommand(): String {
        return "GIT_AUTHOR_DATE=\"${date}T12:00:00\" GIT_COMMITTER_DATE=\"${date}T12:00:00\" git commit --allow-empty -m \"${date}\" > /dev/null\n".repeat(
            count
        )
    }
}
