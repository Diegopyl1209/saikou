package ani.saikou.anime

import java.io.Serializable

data class Anime(
    var totalEpisodes: Int? = null,

    var episodeDuration: Int? = null,
    var season: String? = null,
    var seasonYear: Int? = null,

    var mainStudioID: Int? = null,
    var mainStudioName: String? =null,

    var youtube: String?=null,
    var nextAiringEpisode: Int? = null,
    var nextAiringEpisodeTime: Long? = null,

    var selectedEpisode: String?=null,
    var episodes: MutableMap<String,Episode>? = null,
    var slug:String?=null,
    var kitsuEpisodes: MutableMap<String,Episode>? = null
):Serializable