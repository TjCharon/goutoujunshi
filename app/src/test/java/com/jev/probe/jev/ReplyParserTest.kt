package com.jev.probe.jev

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ReplyParserTest {
    @Test fun parsesTheRequestedJsonArray() {
        assertEquals(listOf("先挂个号看看。", "你先休息，晚点聊。", "这事我们明天再说。"),
            ReplyParser.parseThree("[\"先挂个号看看。\",\"你先休息，晚点聊。\",\"这事我们明天再说。\"]"))
    }

    @Test fun removesReasoningTagsBeforeParsingFinalAnswer() {
        val raw = "<think>这里是很长的内部分析</think>\n[\"甲\",\"乙\",\"丙\"]"
        assertEquals(listOf("甲", "乙", "丙"), ReplyParser.parseThree(raw))
    }

    @Test fun markdownIntroductionAndHeadingsNeverBecomeReplies() {
        val raw = """
            以下是根据当前对话上下文给出的3条候选回复，风格分别偏向轻松化解、直接反问、冷处理：
            1. **轻松化解**：
            “你先别急，我听着呢。”
            2. **直接反问**：
            “你希望我现在具体做什么？”
            3. **冷处理**：
            “我先缓一会儿，晚点认真聊。”
        """.trimIndent()
        assertEquals(
            listOf("你先别急，我听着呢。", "你希望我现在具体做什么？", "我先缓一会儿，晚点认真聊。"),
            ReplyParser.parseThree(raw)
        )
    }

    @Test fun metadataOnlyOutputIsRejectedInsteadOfShownAsAReply() {
        assertThrows(IllegalArgumentException::class.java) {
            ReplyParser.parseThree("以下是三个候选回复：\n**稳健版**：\n**自然版**：")
        }
    }
}
