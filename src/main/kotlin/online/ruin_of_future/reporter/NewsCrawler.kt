package online.ruin_of_future.reporter

import org.jsoup.Jsoup
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


class NewsCrawler {
    private val httpGetter = HTTPGetter()

    private val entryUrl: String = "https://www.zhihu.com/people/mt36501"


    private val byteArrayCache = Cached(byteArrayOf(), 1000 * 60 * 60 * 4L)

    private val font = Font
        .createFont(Font.TRUETYPE_FONT, this.javaClass.getResourceAsStream("/chinese_font.ttf"))
        .deriveFont(25f)

    @Throws(IOException::class)
    suspend fun newsToday(): ByteArray {
        if (byteArrayCache.isNotOutdated()) {
            return byteArrayCache.value
        }

        val entryPageDoc = Jsoup.parse(httpGetter.get(entryUrl))
        var todayUrl: String = entryPageDoc.select("h2.ContentItem-title a[href]").first()?.attr("href")
            ?: throw IOException("Failed to get url!")
        if (todayUrl.startsWith("//")) {
            todayUrl = "https:$todayUrl"
        }
        println(todayUrl)
        val newsDoc = Jsoup.parse(httpGetter.get(todayUrl))
        val newsNode = newsDoc.select("#root > div > main > div > article > div.Post-RichTextContainer > div > div")
        val newsImgUrl = newsNode.select("figure > img").first()?.let {
//            println(it.toString())
            if (it.attr("src").startsWith("https")) {
                it.attr("src")
            } else if (it.attr("data-actualsrc").startsWith("https")) {
                it.attr("data-actualsrc")
            } else {
                // TODO: replace with an empty place holder image
                ""
            }
        } ?: ""
//        println(newsImgUrl)
        val newsTextElement = newsNode.select("p")
        val newsTextStringBuilder = StringBuilder()
        for (p in newsTextElement) {
            // TODO: better formatting
            val rawStr = StringBuilder()
            var lastNotChinise = false
            for (ch in p.text()) {
                val thisNotChinese = Character.UnicodeScript.of(ch.code) != Character.UnicodeScript.HAN &&
                        (ch.isLetter() || ch.isDigit()) &&
                        !(ch == '.' || ch == '。' || ch == ':' || ch == '：' || ch == ',' || ch == '，')

                if (thisNotChinese) {
                    if (!lastNotChinise) {
                        rawStr.append(' ')
                    }
                    rawStr.append("$ch")
                    lastNotChinise = true
                } else {
                    if (lastNotChinise) {
                        rawStr.append(' ')
                    }
                    rawStr.append("$ch")
                    lastNotChinise = false
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
        val newsText = newsTextStringBuilder.toString()

        val imgWidth = 860
        val newsImg = ImageIO.read(URL(newsImgUrl))
        val scaledImgHeight = newsImg.height * imgWidth / newsImg.width
        var imgHeight = scaledImgHeight + font.size * 2
        for (line in newsText.lines()) {
            if (line.isEmpty()) {
                imgHeight += font.size / 2
            } else {
                imgHeight += (font.size * 1.5).toInt()
            }
        }
        val bufferedImage =
            BufferedImage(
                imgWidth,
                imgHeight,
                BufferedImage.TYPE_INT_RGB
            )


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
            if (s.trim().isNotEmpty()) {
                g.drawString(
                    s,
                    10,
                    curHeight
                )
                curHeight += (font.size * 1.5).toInt()
            } else {
                curHeight += font.size / 2
            }
        }
        g.dispose()
        val os = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "png", os)
        byteArrayCache.value = os.toByteArray()
        return byteArrayCache.value
    }
}