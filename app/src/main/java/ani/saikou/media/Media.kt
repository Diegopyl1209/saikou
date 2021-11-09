package ani.saikou.media

import ani.saikou.FuzzyDate
import ani.saikou.anime.Anime
import ani.saikou.manga.Manga
import java.io.Serializable
import java.util.*

data class Media(
    val anime: Anime? = null,
    val manga: Manga? = null,
    val id: Int,

    val name: String,
    val nameRomaji: String,
    val cover: String?=null,
    val banner: String?=null,
    var relation: String? =null,

    var isFav: Boolean = false,
    var notify: Boolean = false,
    val userPreferredName: String,
    var userProgress: Int? = null,
    var userStatus: String? = null,
    var userScore: Int = 0,
    var userRepeat:Int = 0,
    var userUpdatedAt: Date?=null,
    var userStartedAt : FuzzyDate = FuzzyDate(),
    var userCompletedAt : FuzzyDate=FuzzyDate(),

    val status : String? = null,
    var format:String?=null,
    var source:String? = null,
    val meanScore: Int? = null,
    var genres:ArrayList<String>?=null,
    var description: String? = null,
    var startDate: FuzzyDate?=null,
    var endDate: FuzzyDate?=null,

    var characters:ArrayList<Character>?=null,
    var relations: ArrayList<Media>?=null,
    var recommendations: ArrayList<Media>?=null,
) : Serializable