package com.miku.ray.core

import com.miku.ray.AppConfig
import com.miku.ray.dto.CoreConfigContext.RoutingDomainRule
import com.miku.ray.dto.V2rayConfig.DnsBean.ServersBean
import org.junit.Assert.assertEquals
import org.junit.Test

class CoreConfigDnsTest {
    @Test
    fun routedDomainsUseEveryResolverAndEndEachListAtItsLastServer() {
        val remoteDomains = listOf("domain:remote.example")
        val localDomains = listOf("domain:local.example")
        val rules = listOf(
            RoutingDomainRule(remoteDomains, AppConfig.TAG_PROXY),
            RoutingDomainRule(localDomains, AppConfig.TAG_DIRECT),
            RoutingDomainRule(listOf("domain:blocked.example"), AppConfig.TAG_BLOCKED),
        )
        val servers = arrayListOf<Any>()
        val localTag = "${AppConfig.TAG_DOMESTIC_DNS}_1"

        val localTags = CoreConfigManager.buildDnsFromRoutingRules(
            routingDomainRules = rules,
            servers = servers,
            remoteDns = listOf("192.0.2.1", "192.0.2.2"),
            domesticDns = listOf("192.0.2.3", "192.0.2.4"),
        )

        assertEquals(listOf("${localTag}_0", "${localTag}_1"), localTags)
        assertEquals(
            listOf(
                ServersBean(address = "192.0.2.1", domains = remoteDomains),
                ServersBean(address = "192.0.2.2", domains = remoteDomains, finalQuery = true),
                ServersBean(address = "192.0.2.3", domains = localDomains, skipFallback = true, tag = localTags[0]),
                ServersBean(address = "192.0.2.4", domains = localDomains, skipFallback = true, finalQuery = true, tag = localTags[1]),
            ),
            servers,
        )
    }
}
