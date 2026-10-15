import dynamic from "next/dynamic";

const ChengPreview = dynamic(
  () => import("@/components/cheng-preview").then((mod) => mod.ChengPreview),
  {
    ssr: false,
    loading: () => <div className="h-dvh w-full bg-[#f7f4ee] md:h-[844px] md:rounded-[36px]" />,
  },
);

export default function Home() {
  return (
    <main className="min-h-dvh bg-[#e4ddd2] text-[#1c1915] md:grid md:grid-cols-[minmax(0,1fr)_440px] md:items-center">
      <section className="hidden px-10 py-16 md:block lg:px-16">
        <p className="text-xs tracking-[0.22em] text-[#6f675d]">ANDROID · 个人版本</p>
        <h1 className="mt-4 font-[family-name:var(--font-instrument-serif)] text-7xl leading-none">
          澄
        </h1>
        <p className="mt-6 max-w-md text-lg leading-8">
          从 v2rayNG 分出来的 Android 客户端。纸色、松绿，底部直接写着连接和断开。协议和内核仍是上游的 Xray。
        </p>
        <dl className="mt-10 max-w-md space-y-4 text-sm leading-6">
          <div>
            <dt className="text-[#6f675d]">和官方版并存</dt>
            <dd>包名 com.v2ray.ang.cheng，不会覆盖已安装的 v2rayNG。</dd>
          </div>
          <div>
            <dt className="text-[#6f675d]">右边这部手机</dt>
            <dd>可以选配置、切换纸色和墨色、导入分享链接。它不转发流量。</dd>
          </div>
          <div>
            <dt className="text-[#6f675d]">装到自己的手机</dt>
            <dd>用 Android Studio 打开 V2rayNG 目录，或按 README 打出调试包。</dd>
          </div>
        </dl>
      </section>
      <ChengPreview />
    </main>
  );
}
