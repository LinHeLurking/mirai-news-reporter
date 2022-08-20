# Mirai News Reporter
一个简单的每日新闻速报/番剧速报 mirai-console 插件

# 项目地址:

https://github.com/LinHeLurking/mirai-news-reporter

# 功能

1. 爬取知乎的一个每日新闻页面, 以图片形式分享. 向机器人说 "今日新闻", "今日速报" 即可触发.
2. 爬取 B 站的今日番剧列表, 以图片形式分享. 用 "今日动画", "今日番剧" 触发.

**为了避免打扰网友, 群聊使用白名单管理. 只有通过命令指定的群聊, 才会在群聊中触发本机器人.** 

番剧群组白名单和新闻播报群组白名单是分开的两个名单，你可以使用 `/reporter_list` 命令来管理这两个名单。
该命令允许 `list`, `add`, `remove` 三种后缀。其中 `add`, `remove` 两个后缀需要跟一个群号。
在群号之后你可以用 `anime`, `news` 来指定操作哪一个白名单（留空表示二者都操作）。

举例如下：

将群号为 123456 的群加入番剧、新闻白名单：
`/reporter_list add 123456`

将群号为 123456 的群加入番剧白名单：
`/reporter_list add 123456 anime`

将群号为 123456 的群加入新闻白名单：
`/reporter_list add 123456 news`

> 更多命令细节可以通过 /help 获取.

目前排版还不是很好, 以后找机会继续优化.

效果展示：

今日番剧:

![000000000-000000000-00F92DB7B9564739595DD98104249079](https://user-images.githubusercontent.com/35602373/132117074-7659934d-d7d8-4d4c-86ee-ac3cd6aad849.png)

今日新闻:

![000000000-000000000-97C34B229D32E4E897AE6F268A950E3B](https://user-images.githubusercontent.com/35602373/132117096-cff83df8-0316-4283-b3ec-197f9b2cb444.png)


