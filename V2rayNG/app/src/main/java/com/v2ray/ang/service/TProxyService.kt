package com.v2ray.ang.service

/**
 * JNI entry for the prebuilt libhev-socks5-tunnel.so.
 *
 * That library is built with PKGNAME=com/v2ray/ang/service and registers
 * TProxyStartService, TProxyStopService, TProxyIsRunning, and TProxyGetStats
 * on this exact class. The app namespace is com.miku.ray, so the service
 * implementation lives there and calls through here. JNI_OnLoad returns
 * JNI_ERR, and System.loadLibrary throws UnsatisfiedLinkError, when this
 * class is missing. Remove this class only after the .so is rebuilt with
 * PKGNAME=com/miku/ray/service.
 */
class TProxyService private constructor() {
    companion object {
        init {
            System.loadLibrary("hev-socks5-tunnel")
        }

        @JvmStatic
        external fun TProxyStartService(configPath: String, fd: Int): Boolean

        @JvmStatic
        external fun TProxyStopService(): Boolean

        @JvmStatic
        external fun TProxyIsRunning(): Boolean

        @JvmStatic
        external fun TProxyGetStats(): LongArray?
    }
}
