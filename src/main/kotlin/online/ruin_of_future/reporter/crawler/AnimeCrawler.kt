package online.ruin_of_future.reporter.crawler

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames
import java.awt.Color
import java.awt.Font
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.net.URL
import java.time.LocalDateTime
import javax.imageio.ImageIO

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class AnimeInfo(
    val badge: String = "?",
    @JsonNames("cover")
    val coverURL: String,
    val delay: Int, // Indicating whether this is delayed this week
    @JsonNames("delay_id")
    val delayId: Int = 0, // This is optional. Give it a default value.
    @JsonNames("delay_index")
    val delayIndex: String = "", // This is optional. Give it a default value.
    @JsonNames("delay_reason")
    val delayReason: String = "", // This is optional. Give it a default value.
    @JsonNames("ep_id")
    val epId: Int,
    val favorites: Int,
    val follow: Int,
    @JsonNames("is_published")
    val isPublished: Int,
    @JsonNames("pub_index")
    val pubIndex: String = "?", // This will be missing when delay is set. Give it a default value.
    @JsonNames("pub_time")
    val pubTime: String,
    @JsonNames("pub_ts")
    val pubTs: Long,
    @JsonNames("season_id")
    val seasonId: Int,
    @JsonNames("season_status")
    val seasonStatus: Int,
    @JsonNames("square_cover")
    val squareCoverURL: String,
    val title: String,
    val url: String
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ResultByDate(
    val date: String,
    @JsonNames("date_ts")
    val dateTs: Long,
    @JsonNames("day_of_week")
    val dayOfWeek: Int,
    @JsonNames("is_today")
    val isToday: Int,
    val seasons: List<AnimeInfo>
)

@Serializable
data class TimeLineInfo(
    val code: Int,
    val message: String,
    val result: List<ResultByDate>
)

class NoAnimeException(message: String) : Exception(message)

object AnimeCrawler {
    private val ioDispatcher = Dispatchers.IO
    private val httpGetter = HTTPGetter()
    private const val entryURL = "https://bangumi.bilibili.com/web_api/timeline_global"

    private val byteArrayCacheToday = Cached(byteArrayOf(), 1000 * 60 * 60 * 4L)

    private val font = Font
        .createFont(Font.TRUETYPE_FONT, this.javaClass.getResourceAsStream("/chinese_font.ttf"))


    private val json = Json { ignoreUnknownKeys = true }

    private suspend fun getData(): TimeLineInfo {
        val jsonStr = withContext(ioDispatcher) {
            httpGetter.get(entryURL)
        }
        return json.decodeFromString(jsonStr)
    }

    fun isCacheValid(): Boolean {
        return byteArrayCacheToday.isNotOutdated()
    }

    private suspend fun writeOneAnimeInfo(
        bufferedImage: BufferedImage,
        animeInfo: AnimeInfo,
        index: Int,
        oneAnimeHeight: Int,
        hBorderWidth: Int,
        previewHeight: Int,
        totalImageWidth: Int,
        titleFont: Font,
    ) {
        val curHeight = index * oneAnimeHeight

        val imgY = curHeight + hBorderWidth
        val oneAnimeImg = withContext(ioDispatcher) {
            ImageIO.read(URL(animeInfo.coverURL))
        }
        val previewWidth = oneAnimeImg.width * previewHeight / oneAnimeImg.height
        val oneAnimeImgScaled =
            oneAnimeImg.getScaledInstance(previewWidth, previewHeight, Image.SCALE_SMOOTH)
        val imgX = if (index % 2 == 0) {
            20
        } else {
            totalImageWidth - 20 - previewWidth
        }

        val vBorderWidth = if (index % 2 == 0) {
            imgX
        } else {
            totalImageWidth - imgX - previewWidth
        }

        // borders
        var g = bufferedImage.createGraphics()
        g.color = Color.decode("#BDD2B6")
        g.fillRect(0, curHeight, totalImageWidth, hBorderWidth)
        g.fillRect(0, curHeight, vBorderWidth, oneAnimeHeight)
        g.fillRect(0, imgY + previewHeight, totalImageWidth, hBorderWidth)
        g.fillRect(totalImageWidth - vBorderWidth, curHeight, vBorderWidth, oneAnimeHeight)
        g.dispose()


        // preview image
        g = bufferedImage.createGraphics()
        g.drawImage(oneAnimeImgScaled, imgX, imgY, null)
        g.dispose()

        var titleSize = 65f
        val titleTextWidth = 750f
        while (titleSize * animeInfo.title.length > titleTextWidth) {
            titleSize -= 1
        }

        // title text
        val titleY = imgY - 30 + (previewHeight / 2).toInt()
        val titleX = if (index % 2 == 0) {
            870 - (animeInfo.title.length / 2 * titleSize).toInt()
        } else {
            420 - (animeInfo.title.length / 2 * titleSize).toInt()
        }

        g = bufferedImage.createGraphics()
        g.font = titleFont.deriveFont(titleSize)
        g.color = Color.decode("#4e574c")
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.drawString(animeInfo.title.trim(), titleX, titleY)
        g.dispose()

        // detailed info
        val timeY = titleY + 60
        val timeX = titleX + 5 + (animeInfo.title.length / 2 * titleSize).toInt()
        g = bufferedImage.createGraphics()
        g.font = font.deriveFont(35f)
        g.color = Color.decode("#798777")
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.drawString(animeInfo.pubTime.trim(), timeX, timeY)
        g.dispose()
    }

    private suspend fun buildImageByteArray(animeInfos: List<AnimeInfo>): ByteArray {
        val hBorderWidth = 18
        val previewHeight = 600
        val oneAnimeHeight = previewHeight + 2 * hBorderWidth
        val totalImgHeight = animeInfos.size * oneAnimeHeight
        val totalImageWidth = 1200

        val bufferedImage = BufferedImage(totalImageWidth, totalImgHeight, BufferedImage.TYPE_INT_RGB)
        val g = bufferedImage.createGraphics()
        g.color = Color.decode("#F8EDE3")
        g.fillRect(0, 0, bufferedImage.width, bufferedImage.height)
        g.dispose()
        val titleFont = font.deriveFont(40f).deriveFont(Font.BOLD)

        animeInfos.forEachIndexed { index, animeInfo ->
            // Skip delayed episodes
            if (animeInfo.delay != 0) {
                return@forEachIndexed
            }
            writeOneAnimeInfo(
                bufferedImage,
                animeInfo,
                index,
                oneAnimeHeight,
                hBorderWidth,
                previewHeight,
                totalImageWidth,
                titleFont,
            )
            delay(100)
        }

        val os = ByteArrayOutputStream()
        withContext(ioDispatcher) {
            ImageIO.write(bufferedImage, "png", os)
        }
        return os.toByteArray()
    }

    @Throws(Exception::class)
    private suspend fun animeByDate(dateTime: LocalDateTime): ByteArray {
        val dateStr = "${dateTime.month.value}-${dateTime.dayOfMonth}"
        val rawData = getData()
        var dataOnDate: List<AnimeInfo> = listOf()
        for (result in rawData.result) {
            if (result.date == dateStr) {
                dataOnDate = result.seasons
            }
        }
        // Sometimes it's really empty
        if (dataOnDate.isEmpty()) {
            throw NoAnimeException("Not found info for date: $dateStr")
        }
        return buildImageByteArray(dataOnDate)
    }

    @Throws(Exception::class)
    suspend fun animeToday(): ByteArray {
        if (byteArrayCacheToday.isNotOutdated()) {
            return byteArrayCacheToday.value
        }
        byteArrayCacheToday.value = animeByDate(LocalDateTime.now())
        return byteArrayCacheToday.value
    }
}