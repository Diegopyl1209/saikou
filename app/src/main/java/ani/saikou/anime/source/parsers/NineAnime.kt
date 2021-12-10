package ani.saikou.anime.source.parsers

import ani.saikou.anime.Episode
import ani.saikou.anime.source.AnimeParser
import ani.saikou.loadData
import ani.saikou.logger
import ani.saikou.media.Media
import ani.saikou.media.MediaDetailsViewModel
import ani.saikou.media.Source
import ani.saikou.saveData
import ani.saikou.sortByTitle
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.jsoup.Jsoup
import java.net.URLEncoder

class NineAnime(private val model: MediaDetailsViewModel, private val dub:Boolean=false): AnimeParser() {

    //WE DO A LIL TROLLIN
    private val host = listOf(
        "https://animekisa.in/"
        //where 9anime.to
    )

    override fun getStream(episode: Episode): Episode {
        val streams = arrayListOf<Episode.StreamLinks?>()
        Jsoup.connect(episode.link!!).get().select("#servers-list ul.nav li a").forEach { servers ->
            val embedLink = servers.attr("data-embed") // embed link of servers
            val name = servers.select("span").text()
            val token = Regex("(?<=window.skey = )'.*?'").find(
                Jsoup.connect(embedLink).header("referer", host[0]).get().html()
            )?.value?.trim('\'') //token to get the m3u8

            val m3u8Link = Json.decodeFromString<JsonObject>(Jsoup.connect("${embedLink.replace("/e/", "/info/")}&skey=$token")
                .header("referer", host[0])
                .ignoreContentType(true).get().body().text())["media"]!!.jsonObject["sources"]!!.jsonArray[0].jsonObject["file"].toString().trim('"')
            streams.add(Episode.StreamLinks(name,listOf(Episode.Quality(m3u8Link,"Multi",0)),"https://vidstream.pro/"))
        }
        episode.streamLinks = streams
        return episode
    }

    override fun getEpisodes(media: Media): MutableMap<String, Episode> {
        var slug:Source? = loadData("animekisa_in${if(dub) "dub" else ""}_${media.id}")
        if (slug==null) {
            val it = media.nameMAL
            model.parserText.postValue("Searching for ${media.nameMAL} ${media.name}")
            logger("9anime : Searching for ${media.nameMAL} ${media.name}")
            val search = search("$! | &language%5B%5D=${if(dub) "d" else "s"}ubbed&year%5B%5D=${media.anime?.seasonYear}&sort=default&season%5B%5D=${media.anime?.season?.lowercase()}&type%5B%5D=${media.typeMAL?.lowercase()}")
            if (search.isNotEmpty()) {
                search.sortByTitle(it!!)
                slug = search[0]
                model.parserText.postValue("Found : ${slug.name}")
                saveData("animekisa_in${if(dub) "dub" else ""}_${media.id}", slug)
            }
        }
        else{
            model.parserText.postValue("Selected : ${slug.name}")
        }
        if (slug!=null) return getSlugEpisodes(slug.link)
        return mutableMapOf()
    }

    override fun search(string: String): ArrayList<Source> {
        //THIS IS LIKE THE WORST SEARCH ENGINE OF AN WEBSITE
        var url = URLEncoder.encode(string, "utf-8")
        if(string.startsWith("$!")){
            val a = string.replace("$!","").split(" | ")
            url = URLEncoder.encode(a[0], "utf-8")+a[1]
        }
        println("${host[0]}filter?keyword=$url")
        val responseArray = arrayListOf<Source>()
        Jsoup.connect("${host[0]}filter?keyword=$url").get()
            .select("#main-wrapper .film_list-wrap > .flw-item .film-poster").forEach{
                val link = it.select("a").attr("href")
                val title = it.select("img").attr("title")
                val cover = it.select("img").attr("data-src")
                responseArray.add(Source(link,title,cover))
            }
        return responseArray
    }

    private fun getSlugEpisodes(slug:String): MutableMap<String, Episode>{
        val responseArray = mutableMapOf<String,Episode>()
        val pageBody = Jsoup.connect(slug).get().body()
        pageBody.select(".tab-pane > ul.nav").forEach{
            it.select("li>a").forEach { i ->
                val num = i.text().trim()
                responseArray[num] = Episode(number = num,link = i.attr("href").trim())
            }
        }
        println("Response Episodes : $responseArray")
        return responseArray
    }
}