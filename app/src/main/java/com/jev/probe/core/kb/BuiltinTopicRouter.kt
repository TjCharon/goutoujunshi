package com.jev.probe.core.kb

import com.jev.probe.core.ChatSnapshot

/**
 * Offline topic routing for the bundled relationship guidance cards.
 *
 * This deliberately uses transparent keyword evidence rather than another
 * network model: routing remains fast, private and testable. The reply card is
 * always present; at most two domain cards are added for the current scene.
 */
object BuiltinTopicRouter {
    const val ID_PREFIX = "builtin:"
    private const val MAX_DOMAIN_CARDS = 2

    private data class Card(
        val id: String,
        val title: String,
        val keywords: List<String>,
        val guidance: String,
        val priority: Int = 0
    ) {
        fun asNote() = Note(
            id = ID_PREFIX + id,
            title = title,
            content = "这是建议框架，不是关于对方的事实。$guidance",
            tags = emptyList(),
            enabled = true,
            updatedAt = 0L
        )
    }

    private val replyCard = Card(
        id = "reply_craft",
        title = "即时回复编排",
        keywords = emptyList(),
        guidance = "先识别本轮唯一主动作：承接、降压、调侃、轻推、约见、澄清或收线。" +
            "匹配双方原有口吻和长度；先给可直接发送的一句，再保留积极、含糊和拒绝三种反馈下的退出空间。"
    )

    private val domainCards = listOf(
        Card(
            "crisis_safety", "安全与危机",
            listOf("打我", "掐脖", "堵门", "跟踪", "威胁", "杀了", "自杀", "不想活", "伤害自己", "报警", "强迫", "下药", "偷拍", "勒索", "曝光隐私"),
            "暂停普通恋爱推进。区分即时危险、控制和普通冲突；有现实危险时优先离开并联系可信支持或当地紧急服务。" +
                "不以继续关系交换安全，不建议双方共同沟通来处理暴力或胁迫。",
            priority = 100
        ),
        Card(
            "money_risk", "金钱边界与诈骗风险",
            listOf("欠我", "欠你", "还钱", "借钱", "转账", "红包", "债务", "彩礼", "投资", "博彩", "虚拟币", "解冻费", "代收款", "500块", "钱什么时候"),
            "把金额、用途、借贷事实、偿还时间和证据说清，不用暧昧替代财务约定。网恋投资、陌生 App、解冻费或紧急转账按高风险处理；停止追加付款并核验。"
        ),
        Card(
            "conflict_repair", "冲突与修复",
            listOf("对不起", "道歉", "吵架", "生气", "冷战", "失约", "骗我", "不在乎", "你根本", "每次都", "从来不", "没听我说", "伤心"),
            "先区分误解、可解决问题、持续差异和安全问题。回复采用具体事实、影响、需要和一个请求；道歉要承认行为与影响并给可执行修复，不用长篇自辩稀释责任。"
        ),
        Card(
            "breakup_repair", "分手、背叛与复合",
            listOf("分手", "复合", "前任", "不想继续", "别联系", "不要联系", "删除你", "删了你", "拉黑", "出轨", "背叛", "原谅", "结束关系"),
            "明确结束或要求不联系时停止推进。复合要看分手原因是否被命名、改变是否已有行为证据、边界是否重新协商；想念不等于适合复合，宽恕也不等于必须继续。"
        ),
        Card(
            "reciprocity", "互惠、投入与退出",
            listOf("总是我主动", "一直是我主动", "敷衍", "已读不回", "不回消息", "忽冷忽热", "反复取消", "放鸽子", "只在需要", "备胎", "没时间", "下次吧"),
            "先看持续行为：谁发起、是否给替代安排、是否兑现、是否修复和尊重边界。证据不足时做一次能产生新信息的主动动作；持续单边时停止加码、设观察事件和退出条件，不用冷暴力测试对方。"
        ),
        Card(
            "consent_boundary", "同意与亲密边界",
            listOf("亲我", "接吻", "抱我", "肢体接触", "发生关系", "性生活", "不舒服", "不要碰", "停下来", "不愿意", "裸照", "私密照", "安全套", "避孕"),
            "同意必须清楚、自由、持续、有能力且可撤回；沉默、僵住、过去同意、恋爱关系或接受礼物都不代表同意。犹豫时先停，拒绝后不说服、不施压、不重复测试。"
        ),
        Card(
            "dating_progress", "主动表达与关系推进",
            listOf("约会", "见面", "表白", "喜欢你", "有好感", "暧昧", "在一起", "周末一起", "出来玩吗", "第一次见", "约她", "约他"),
            "没有明确拒绝或不适时，建议一次真实、具体、低压力且可拒绝的主动：表达欣赏、给时间地点或说明好感。根据对方是否回应、给替代安排和兑现来校准，身体接触不作为进度指标。"
        ),
        Card(
            "emotion_attachment", "依恋与情绪调节",
            listOf("回避型", "焦虑型", "安全型", "依恋", "没有安全感", "患得患失", "害怕失去", "怕被抛弃", "追着问", "冷处理", "需要空间"),
            "依恋是受关系与情境影响的焦虑、回避维度，不是人格诊断。把追问改为具体有限请求；需要空间时给返回时间。先区分触发、解释与事实，用长期可靠行为校正标签。"
        ),
        Card(
            "digital_relationship", "网络聊天与数字关系",
            listOf("已读", "不回", "朋友圈", "定位", "查手机", "手机密码", "网恋", "视频", "照片", "线上", "表情包", "回复慢", "几个小时没回"),
            "文字缺少语气，单次延迟、标点或表情不能证明爱与动机。只依据可见原文、顺序和持续行为；复杂冲突可约安全的语音或见面沟通。定位、密码和私密影像需明确自愿，拒绝共享不等于不忠。"
        ),
        Card(
            "emotional_support", "情绪支持",
            listOf("好累", "难受", "委屈", "压力大", "崩溃", "哭了", "焦虑", "失眠", "烦死了", "心情不好", "撑不住"),
            "先准确复述情绪和触发点，再问对方更需要倾听、陪伴还是建议。确认感受不等于同意全部解释；避免立刻讲道理、比较谁更惨或给未经请求的解决方案。"
        ),
        Card(
            "family_life", "婚姻、家庭与共同生活",
            listOf("结婚", "离婚", "婚房", "父母", "婆婆", "公婆", "岳父", "孩子", "生育", "家务", "共同账户", "工资上交", "买房", "育儿"),
            "同时看亲密、法律身份和生活合作。把金钱、债务、家务、照护、婚育、城市与双方家庭边界拆成可协商事项；公平不必逐项五五开，但信息、决策权、风险和休息要透明且双方认可。"
        ),
        Card(
            "manipulation", "操控识别与伦理替代",
            listOf("PUA", "贬低", "打压", "服从测试", "煤气灯", "故意冷淡", "制造嫉妒", "控制我", "不许我", "查岗", "孤立我", "情绪勒索"),
            "不提供贬低、推拉、嫉妒操控、服从测试、假未来或煤气灯脚本。把需求改写为真实表达、明确选择、可拒绝邀请和边界后果；短期顺从不等于自愿、吸引或长期信任。"
        )
    )

    fun route(snapshot: ChatSnapshot, relationship: String, maxCards: Int = 3): List<Note> {
        if (maxCards <= 0) return emptyList()
        val haystack = buildString {
            append(relationship.lowercase())
            snapshot.messages.takeLast(10).forEach { append('\n').append(it.text.lowercase()) }
        }
        val ranked = domainCards.mapNotNull { card ->
            val hitCount = card.keywords.count { haystack.contains(it.lowercase()) }
            if (hitCount == 0) null else card to (hitCount * 10 + card.priority)
        }.sortedWith(compareByDescending<Pair<Card, Int>> { it.second }.thenBy { it.first.title })

        val out = ArrayList<Note>()
        out.add(replyCard.asNote())
        ranked.take(minOf(MAX_DOMAIN_CARDS, maxCards - 1)).forEach { out.add(it.first.asNote()) }
        return out
    }

    fun isBuiltin(note: Note): Boolean = note.id.startsWith(ID_PREFIX)
}
