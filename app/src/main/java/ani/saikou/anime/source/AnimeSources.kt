package ani.saikou.anime.source

import ani.saikou.anime.source.parsers.*

object AnimeSources : Sources() {
    override val names = arrayListOf(
        "GOGO",
        "GOGO-DUB",
        "9ANIME",
        "9ANIME-DUB",
        "ZORO",
        "TWIST",
        "TENSHI",
            "MONOSCHINOS",
    )

    val animeParsers:MutableMap<Int,AnimeParser> = mutableMapOf()
    override operator fun get(i:Int) : AnimeParser?{
        val a = when (i) {
            0 -> animeParsers.getOrPut(i) { Gogo() }
            1 -> animeParsers.getOrPut(i) { Gogo(true) }
            2 -> animeParsers.getOrPut(i) { NineAnime() }
            3 -> animeParsers.getOrPut(i) { NineAnime(true) }
            4 -> animeParsers.getOrPut(i) { Zoro() }
            5 -> animeParsers.getOrPut(i) { Twist() }
            6 -> animeParsers.getOrPut(i) { Tenshi() }
            7 -> animeParsers.getOrPut(i) { Monoschinos() }
            else -> null
        }
        return a
    }
    override fun flushLive(){
        animeParsers.forEach{
            it.value.live.value=null
        }
    }
}