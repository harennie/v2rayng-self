package com.v2ray.ang.ui.main

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HomeGreetingTest {

    @Test
    fun greetingFollowsTheHour() {
        assertEquals(HomeDayPart.Night, homeDayPart(0))
        assertEquals(HomeDayPart.Night, homeDayPart(4))
        assertEquals(HomeDayPart.Morning, homeDayPart(5))
        assertEquals(HomeDayPart.Morning, homeDayPart(11))
        assertEquals(HomeDayPart.Afternoon, homeDayPart(12))
        assertEquals(HomeDayPart.Afternoon, homeDayPart(17))
        assertEquals(HomeDayPart.Evening, homeDayPart(18))
        assertEquals(HomeDayPart.Evening, homeDayPart(22))
        assertEquals(HomeDayPart.Night, homeDayPart(23))
    }
}
