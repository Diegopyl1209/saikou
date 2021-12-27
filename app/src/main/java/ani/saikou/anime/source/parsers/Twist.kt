package ani.saikou.anime.source.parsers


import ani.saikou.anime.Episode
import ani.saikou.anime.source.AnimeParser
import ani.saikou.media.Media
import ani.saikou.media.Source
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import org.jsoup.Jsoup
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList

class Twist:AnimeParser() {

    object DecodeTwistSources{
        private val secret = "267041df55ca2b36f2e322d05ee2c9cf".toByteArray()
        private fun base64decode(oriString:String): ByteArray {
            return android.util.Base64.decode(oriString, android.util.Base64.DEFAULT)
        }

        private fun md5(input:ByteArray): ByteArray {
            return MessageDigest.getInstance("MD5").digest(input)
        }

        private fun generateKey(salt:ByteArray): ByteArray {
            var key = md5(secret +salt)
            var currentKey = key
            while (currentKey.size < 48){
                key = md5(key + secret + salt)
                currentKey += key
            }
            return currentKey
        }

        private fun decryptSourceUrl(decryptionKey:ByteArray, sourceUrl: String): String {
            val cipherData = base64decode(sourceUrl)
            val encrypted = cipherData.copyOfRange(16, cipherData.size)
            val aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding")

            Objects.requireNonNull(aesCBC).init(Cipher.DECRYPT_MODE, SecretKeySpec(
                decryptionKey.copyOfRange(0,32),
                "AES"),
                IvParameterSpec(decryptionKey.copyOfRange(32,decryptionKey.size))
            )
            val decryptedData = aesCBC!!.doFinal(encrypted)
            return String(decryptedData, StandardCharsets.UTF_8)
        }

        fun decryptSource(input:String): String {
            return decryptSourceUrl(generateKey(base64decode(input).copyOfRange(8,16)),input)
        }
    }

    override fun getStream(episode: Episode): Episode {
        val url = Json.decodeFromString<JsonArray>(
            Jsoup.connect(episode.link!!).ignoreContentType(true).get().body().text()
        )[episode.number.toInt()-1].jsonObject["source"].toString().trim('"')
        1 ushr 2
        episode.streamLinks =  arrayListOf(
            Episode.StreamLinks(
                "Twist",
                listOf(
                    Episode.Quality(
                        url = "https://cdn.twist.moe${DecodeTwistSources.decryptSource(url)}",
                        quality = "unknown",
                        size = 0
                    )
                ),
                "https://twist.moe/"
            )
        )
        return episode
    }

    override fun getEpisodes(media: Media): MutableMap<String, Episode> {
        val responseList = mutableMapOf<String,Episode>()
        val animeJson = Jsoup.connect("https://api.twist.moe/api/anime").ignoreContentType(true).get().body().text()
        if (media.idMAL!=null) {
            val slug = Regex(""""mal_id": ${media.idMAL},(.|\n)+?"slug": "(.+?)"""").find(animeJson)?.destructured?.component2()
            if (slug!=null) {
                val slugURL = "https://api.twist.moe/api/anime/$slug/sources"
                (1..Json.decodeFromString<JsonArray>(
                    Jsoup.connect(slugURL).ignoreContentType(true).get().body().text()
                ).size).forEach {
                    responseList[it.toString()] = Episode(number = it.toString(), link = slugURL)
                }
                println("Response Episodes : $responseList")
                return responseList
            }
        }
        return mutableMapOf()
    }

    override fun search(string: String): ArrayList<Source> {
        TODO("Not yet implemented")
    }
}


