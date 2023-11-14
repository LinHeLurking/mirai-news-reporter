# Mirai News Reporter

一个简单的每日新闻速报/番剧速报 mirai-console 插件

> ⚠ 每日新闻的知乎账号已经停更，因此目前每日新闻无法正确获取。

# 项目地址:

https://github.com/LinHeLurking/mirai-news-reporter

# 功能

1. 爬取知乎的一个每日新闻页面, 以图片形式分享. 向机器人说 "今日新闻", "今日速报" 即可触发.
2. 爬取 B 站的今日番剧列表, 以图片形式分享. 用 "今日动画", "今日番剧" 触发.
3. 从微信公众号摸鱼人日历的 API 获取日历图片并发送。用“摸鱼日历”，”摸鱼人日历“触发。

### 白名单

**为了避免打扰网友, 群聊使用白名单管理. 只有通过命令指定的群聊, 才会在群聊中触发本机器人.**

番剧群组白名单，新闻播报群组白名单和摸鱼人日历白名单是分开的三个名单，你可以使用 `/reporter_list` 命令来管理这三个名单。
该命令允许 `show`, `add`, `remove` 三种后缀。其中 `add`, `remove` 两个后缀需要跟一个群号。
在群号之后你可以用 `anime`, `news`，`moyu` 来指定操作哪一个白名单（留空表示二者都操作）。

举例如下：

将群号为 123456 的群加入番剧、新闻白名单：
`/reporter_list add 123456`

将群号为 123456 的群加入番剧白名单：
`/reporter_list add 123456 anime`

将群号为 123456 的群加入新闻白名单：
`/reporter_list add 123456 news`

> 更多命令细节可以通过 /help 获取.

### 自定义语句

Bot 在回复命令时的很多语句，都可以通过 `/reporter_msg` 命令来自定义。

命令格式： `/reporter_msg <key> <list>`。

其中，`<key>` 的可能取值及其含义见下表。
`<list>` 是一个用逗号或分号分割的列表（也可以只是一个词），中文标点和英文标点都可以，但是不能有空格。
`<list>` 表示相应的**用户发出触发语句**可以使用 `<list>` 中的**任何一个**，
而**机器人回复的语句**会在列表中**随机选取**。

|key| 默认值                      | 含义                       |
|:---|:-------------------------|:-------------------------|
|dailyTriggers|今日,每日,日常,daily,Daily| "今日番剧"/“每日新闻” 中的 “今日/每日” |
|animeTriggers|番剧,动画,B站番剧,B站番剧| "今日番剧"/“每日动画” 中的 “番剧/动画” |
|newsTriggers|新闻,速报,新闻速报| "今日新闻"/“每日速报” 中的 “新闻/速报” |
|waitMessages|稍等哦QwQ| 需要进行长加载时的提示语             |
|animeDailyMessages|早上好呀,这是今天的B站番剧\n(•̀ω•́)✧| 早上的自动动画播报提示              |
|animeReplyMessages|这是今天的B站番剧\n(•̀ω•́)✧| 用关键词触发播报后的回复语            |
|noAnimeMessages|好像今天没有放送呢>_<| 今天没有番剧时的提示语              |
|newsDailyMessages|这是今天的新闻速报\nq(≧▽≦q)| 早上的自动新闻播报提示              |
|newsReplyMessages|"这是今天的新闻速报 \nq(≧▽≦q)"| 用关键词触发播报后的回复语            |
|noDisturbingGroupMessages|为了防止打扰到网友，这个群不在日报白名单呢QwQ| 白名单提示                    |
|errorMessages|出错啦,等会再试试吧￣へ￣| 错误提示                     |
|touchfishDailyMessages|"摸鱼时间到～～这是今天的摸鱼日历 \n(≖‿≖)✧"))| 早上的自动摸鱼提示 |
|touchfishReplyMessages|"这是今天的摸鱼日历 \n(≖‿≖)✧"))|  用关键词触发摸鱼后的回复语   |

> 上述的 key，写成单数形式也 OK

## 效果展示

目前排版还不是很好, 以后找机会继续优化.

效果展示：

今日番剧:

![000000000-000000000-00F92DB7B9564739595DD98104249079](https://user-images.githubusercontent.com/35602373/132117074-7659934d-d7d8-4d4c-86ee-ac3cd6aad849.png)

今日新闻:

![000000000-000000000-97C34B229D32E4E897AE6F268A950E3B](https://user-images.githubusercontent.com/35602373/132117096-cff83df8-0316-4283-b3ec-197f9b2cb444.png)

摸鱼日历:

![image](https://github.com/fsry/mirai-news-reporter/assets/53202887/df67a2e6-db45-4e5a-9b46-dc2ed8cf6536)


