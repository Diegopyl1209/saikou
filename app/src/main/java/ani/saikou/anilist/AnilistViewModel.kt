package ani.saikou.anilist

import ani.saikou.media.Media
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ani.saikou.anilist

class AnilistHomeViewModel : ViewModel() {
    private val listImages : MutableLiveData<ArrayList<String?>> = MutableLiveData<ArrayList<String?>>(arrayListOf())
    fun getListImages(): LiveData<ArrayList<String?>> = listImages
    fun setListImages() = listImages.postValue(anilist.query.getBannerImages())

    private val animeContinue: MutableLiveData<ArrayList<Media>> = MutableLiveData<ArrayList<Media>>(null)
    fun getAnimeContinue(): LiveData<ArrayList<Media>> = animeContinue
    fun setAnimeContinue() = animeContinue.postValue(anilist.query.continueMedia("ANIME"))

    private val mangaContinue: MutableLiveData<ArrayList<Media>> = MutableLiveData<ArrayList<Media>>(null)
    fun getMangaContinue(): LiveData<ArrayList<Media>> = mangaContinue
    fun setMangaContinue() = mangaContinue.postValue(anilist.query.continueMedia("MANGA"))

    private val recommendation: MutableLiveData<ArrayList<Media>> = MutableLiveData<ArrayList<Media>>(null)
    fun getRecommendation(): LiveData<ArrayList<Media>> = recommendation
    fun setRecommendation() = recommendation.postValue(anilist.query.recommendations())

    val load : MutableLiveData<Boolean> = MutableLiveData(false)
}

class AnilistAnimeViewModel : ViewModel() {
    private val type = "ANIME"
    private val trending: MutableLiveData<ArrayList<Media>> = MutableLiveData<ArrayList<Media>>(null)
    fun getTrending(): LiveData<ArrayList<Media>> = trending
    fun loadTrending() = trending.postValue(anilist.query.search(type, perPage = 10, sort="TRENDING_DESC").results)
}

class AnilistSearch : ViewModel(){
    private val search: MutableLiveData<SearchResults> = MutableLiveData<SearchResults>(null)
    fun getSearch(): LiveData<SearchResults> = search
    fun loadSearch(type:String,search_val:String?=null,genres:ArrayList<String>?=null,sort:String="SEARCH_MATCH") = search.postValue(anilist.query.search(type, search=search_val, sort=sort, genres = genres))

    fun loadNextPage(r:SearchResults) = anilist.query.search(r.type,r.page+1,r.perPage,r.search,r.sort,r.genres)
}