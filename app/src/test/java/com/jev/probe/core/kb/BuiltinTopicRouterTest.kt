package com.jev.probe.core.kb

import com.jev.probe.core.ChatSnapshot
import com.jev.probe.core.Msg
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuiltinTopicRouterTest {
    private fun route(vararg texts: String) = BuiltinTopicRouter.route(
        ChatSnapshot("测试", texts.map { Msg("other", it) }), "朋友"
    )

    @Test fun ordinaryChatStillLoadsOnlyTheReplyCard() {
        assertEquals(listOf("即时回复编排"), route("今天吃什么").map { it.title })
    }

    @Test fun debtQuestionLoadsMoneyGuidanceAutomatically() {
        val titles = route("对了，上次你欠我的500块什么时候还？").map { it.title }
        assertTrue("金钱边界与诈骗风险" in titles)
    }

    @Test fun explicitThreatMakesSafetyTheFirstDomainCard() {
        val titles = route("你敢分手我就跟踪你，还要伤害自己").map { it.title }
        assertEquals("即时回复编排", titles[0])
        assertEquals("安全与危机", titles[1])
        assertTrue(titles.size <= 3)
    }

    @Test fun noAnalysisReceivesMoreThanThreeCards() {
        assertTrue(route("分手后他威胁我，还欠我钱并且一直跟踪我").size <= 3)
    }
}
