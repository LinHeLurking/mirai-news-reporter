package online.ruin_of_future.reporter.crawler

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.awt.Color
import java.awt.Font
import java.awt.Image.SCALE_SMOOTH
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.min


object NewsCrawler {
    private val ioDispatcher = Dispatchers.IO
    private val httpGetter = HTTPGetter()
    private const val entryUrl: String = "https://www.zhihu.com/people/mt36501"

    private val byteArrayCache = Cached(byteArrayOf(), 1000 * 60 * 60 * 4L)

    private val font = Font
        .createFont(Font.TRUETYPE_FONT, this.javaClass.getResourceAsStream("/chinese_font.ttf"))
        .deriveFont(25f)

    fun isCacheValid(): Boolean {
        return byteArrayCache.isNotOutdated()
    }

    private suspend fun writeToImage(newsImg: BufferedImage, newsText: String): BufferedImage {
        // Set size
        val imgWidth = 860
        val scaledImgHeight = newsImg.height * imgWidth / newsImg.width
        var imgHeight = scaledImgHeight + font.size * 2
        for (line in newsText.lines()) {
            imgHeight += if (line.isEmpty()) {
                font.size / 2
            } else {
                (font.size * 1.5).toInt()
            }
        }

        // Create image
        val bufferedImage =
            BufferedImage(
                imgWidth,
                imgHeight,
                BufferedImage.TYPE_INT_RGB
            )

        // Draw banner & texts
        var g = bufferedImage.createGraphics()
        g.color = Color.WHITE
        g.fillRect(0, 0, bufferedImage.width, bufferedImage.height)
        g.dispose()

        g = bufferedImage.createGraphics()

        g.drawImage(
            newsImg.getScaledInstance(imgWidth, scaledImgHeight, SCALE_SMOOTH),
            0,
            0,
            null
        )
        g.dispose()

        g = bufferedImage.createGraphics()
        g.font = font
        g.color = Color.BLACK
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        var curHeight = scaledImgHeight + font.size * 2
        newsText.lines().forEach { s ->
            curHeight += if (s.trim().isNotEmpty()) {
                g.drawString(
                    s,
                    10,
                    curHeight
                )
                (font.size * 1.5).toInt()
            } else {
                font.size / 2
            }
        }
        g.dispose()

        return bufferedImage
    }


    private suspend fun parseNewsText(newsTextElement: Elements): String {
        val newsTextStringBuilder = StringBuilder()
        for (p in newsTextElement) {
            // TODO: better formatting
            val rawStr = StringBuilder()
            var lastNotChinese = false
            for (ch in p.text()) {
                val thisNotChinese = Character.UnicodeScript.of(ch.code) != Character.UnicodeScript.HAN &&
                        (ch.isLetter() || ch.isDigit()) &&
                        !(ch == '.' || ch == '。' || ch == ':' || ch == '：' || ch == ',' || ch == '，')

                lastNotChinese = if (thisNotChinese) {
                    if (!lastNotChinese) {
                        rawStr.append(' ')
                    }
                    rawStr.append("$ch")
                    true
                } else {
                    if (lastNotChinese) {
                        rawStr.append(' ')
                    }
                    rawStr.append("$ch")
                    false
                }
            }

            val pStr = rawStr.toString()
            if (pStr.isEmpty()) {
                continue
            }
            val lineLen = 40
            if (pStr.length <= lineLen) {
                newsTextStringBuilder.append(pStr)
                newsTextStringBuilder.append('\n')
            } else {
                var i = 0;
                while (i < pStr.length) {
                    newsTextStringBuilder.append(pStr.subSequence(i, min(i + lineLen, pStr.length)))
                    i += lineLen
                    while (i < pStr.length && Character.UnicodeScript.of(pStr[i].code) != Character.UnicodeScript.HAN) {
                        newsTextStringBuilder.append(pStr[i])
                        i += 1
                    }
                    newsTextStringBuilder.append('\n')
                }
            }
            newsTextStringBuilder.append("\n")
        }
        return newsTextStringBuilder.toString()
    }

    @Throws(IOException::class)
    suspend fun newsToday(): ByteArray {
        if (byteArrayCache.isNotOutdated()) {
            return byteArrayCache.value
        }

        val entryPageDoc = withContext(ioDispatcher) {
            Jsoup.parse(httpGetter.get(entryUrl))
        }
        var todayUrl: String = entryPageDoc.select("h2.ContentItem-title a[href]").first()?.attr("href")
            ?: throw IOException("Failed to get url!")
        if (todayUrl.startsWith("//")) {
            todayUrl = "https:$todayUrl"
        }
        val newsDoc = withContext(ioDispatcher) {
            Jsoup.parse(httpGetter.get(todayUrl))
        }
        val newsNode = newsDoc.select("#root > div > main > div > article > div.Post-RichTextContainer > div > div")
        val newsImgUrl = newsNode.select("figure > img").first()?.let {
            if (it.attr("src").startsWith("https")) {
                it.attr("src")
            } else if (it.attr("data-actualsrc").startsWith("https")) {
                it.attr("data-actualsrc")
            } else {
                // TODO: replace with an empty placeholder image
                ""
            }
        } ?: ""
        val newsTextElement = newsNode.select("p")
        val newsText = parseNewsText(newsTextElement)

        val newsImg = withContext(ioDispatcher) {
            ImageIO.read(URL(newsImgUrl))
        }
        val bufferedImage = writeToImage(newsImg, newsText)
        val os = ByteArrayOutputStream()
        withContext(ioDispatcher) {
            ImageIO.write(bufferedImage, "png", os)
        }
        byteArrayCache.value = os.toByteArray()
        return byteArrayCache.value
    }
}