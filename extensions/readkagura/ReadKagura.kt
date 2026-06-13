package eu.kanade.tachiyomi.animeextension.en.readkagura

import eu.kanade.tachiyomi.animesource.model.*
import eu.kanade.tachiyomi.animesource.online.AnimeHttpSource
import okhttp3.Request
import org.jsoup.Jsoup

class ReadKagura : AnimeHttpSource() {

    override val name = "ReadKagura"
    override val baseUrl = "https://readkagura.com"
    override val lang = "en"
    override val supportsLatest = true

    // ======================
    // POPULAR / MANGA LIST
    // ======================
    override fun popularAnimeRequest(page: Int): Request {
        return GET("$baseUrl/manga/", headers)
    }

    override fun popularAnimeParse(response: String): AnimesPage {
        val doc = Jsoup.parse(response)

        val manga = doc.select("a[href*='/manga/']").map {
            SManga.create().apply {
                title = it.text()
                setUrlWithoutDomain(it.attr("href"))
            }
        }

        return AnimesPage(manga, false)
    }

    // ======================
    // MANGA DETAILS (CHAPTERS)
    // ======================
    override fun episodeListParse(response: String): List<SAnime> {
        val doc = Jsoup.parse(response)

        return doc.select("a[href*='chapter']").map {
            SAnime.create().apply {
                name = it.text()
                setUrlWithoutDomain(it.attr("href"))
            }
        }
    }

    // ======================
    // CHAPTER PAGES (IMAGES)
    // ======================
    override fun videoListParse(response: String): List<Video> {
        val doc = Jsoup.parse(response)

        return doc.select("img").mapNotNull {
            val src = it.attr("src")

            if (src.contains("blogger.googleusercontent.com")) {
                Video(src, "")
            } else null
        }
    }
}
