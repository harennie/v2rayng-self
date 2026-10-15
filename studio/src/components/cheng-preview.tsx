"use client";

import { useMemo, useState, useSyncExternalStore } from "react";
import { parseShareLinks } from "@/lib/share-link";

type Server = {
  id: string;
  name: string;
  protocol: string;
  endpoint: string;
  group: string;
  delay: number | null;
};

type Sheet = "none" | "import" | "drawer" | "about";

const STORAGE_KEY = "cheng-preview-v1";

const samples: Server[] = [
  {
    id: "tokyo",
    name: "纸间 · 东京",
    protocol: "VLESS",
    endpoint: "tokyo.example:443",
    group: "常用",
    delay: 42,
  },
  {
    id: "singapore",
    name: "湖岸 · 新加坡",
    protocol: "Trojan",
    endpoint: "shore.example:443",
    group: "常用",
    delay: 68,
  },
  {
    id: "losangeles",
    name: "旧巷 · 洛杉矶",
    protocol: "VMess",
    endpoint: "alley.example:443",
    group: "备用",
    delay: 121,
  },
  {
    id: "home",
    name: "自建 · 家里",
    protocol: "WireGuard",
    endpoint: "home.example:51820",
    group: "备用",
    delay: null,
  },
];

function statusCopy(running: boolean, selected: Server | undefined, testing: boolean) {
  if (!selected) return "先选一条配置";
  if (testing) return "正在测试这条配置";
  if (!running) return "未连接";
  if (selected.delay === null) return "已连接 · 点这里测一下";
  if (selected.delay < 0) return "已连接 · 刚才没测通";
  return `已连接 · ${selected.delay} ms`;
}

function delayLabel(delay: number | null) {
  if (delay === null) return "";
  if (delay < 0) return "失败";
  return `${delay} ms`;
}

type Persisted = { servers: Server[]; selectedId: string; dark: boolean };

const fallbackPersisted: Persisted = {
  servers: samples,
  selectedId: "tokyo",
  dark: false,
};

let clientPersisted: Persisted | null = null;
const persistedListeners = new Set<() => void>();

function isServer(value: unknown): value is Server {
  if (!value || typeof value !== "object") return false;
  const item = value as Partial<Server>;
  return (
    typeof item.id === "string" &&
    typeof item.name === "string" &&
    typeof item.protocol === "string" &&
    typeof item.endpoint === "string" &&
    typeof item.group === "string" &&
    (item.delay === null || typeof item.delay === "number")
  );
}

function readStored(): Persisted | null {
  if (typeof window === "undefined") return null;
  try {
    const stored = window.localStorage.getItem(STORAGE_KEY);
    if (!stored) return null;
    const parsed = JSON.parse(stored) as {
      servers?: unknown;
      selectedId?: unknown;
      dark?: unknown;
    };
    if (!Array.isArray(parsed.servers)) return null;
    const servers = parsed.servers.filter(isServer);
    return {
      servers,
      selectedId: typeof parsed.selectedId === "string" ? parsed.selectedId : (servers[0]?.id ?? ""),
      dark: Boolean(parsed.dark),
    };
  } catch {
    return null;
  }
}

function getPersisted() {
  if (!clientPersisted) clientPersisted = readStored() ?? fallbackPersisted;
  return clientPersisted;
}

function writePersisted(next: Persisted) {
  clientPersisted = next;
  window.localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
  persistedListeners.forEach((listener) => listener());
}

function subscribePersisted(listener: () => void) {
  persistedListeners.add(listener);
  return () => persistedListeners.delete(listener);
}

export function ChengPreview() {
  const persisted = useSyncExternalStore(subscribePersisted, getPersisted, () => fallbackPersisted);
  const { servers, selectedId, dark } = persisted;
  const setServers = (action: Server[] | ((current: Server[]) => Server[])) => {
    const current = getPersisted();
    writePersisted({
      ...current,
      servers: typeof action === "function" ? action(current.servers) : action,
    });
  };
  const setSelectedId = (id: string) => writePersisted({ ...getPersisted(), selectedId: id });
  const setDark = (action: boolean | ((current: boolean) => boolean)) => {
    const current = getPersisted();
    writePersisted({
      ...current,
      dark: typeof action === "function" ? action(current.dark) : action,
    });
  };
  const [group, setGroup] = useState("全部");
  const [running, setRunning] = useState(false);
  const [testing, setTesting] = useState(false);
  const [query, setQuery] = useState("");
  const [searching, setSearching] = useState(false);
  const [sheet, setSheet] = useState<Sheet>("none");
  const [draft, setDraft] = useState("");
  const [importError, setImportError] = useState("");

  const groups = useMemo(() => {
    const names = Array.from(new Set(servers.map((server) => server.group)));
    return ["全部", ...names];
  }, [servers]);

  const visible = servers.filter((server) => {
    const inGroup = group === "全部" || server.group === group;
    const haystack = `${server.name} ${server.protocol} ${server.endpoint}`.toLowerCase();
    const matches = haystack.includes(query.trim().toLowerCase());
    return inGroup && matches;
  });

  const selected = servers.find((server) => server.id === selectedId);

  function selectServer(id: string) {
    setSelectedId(id);
  }

  function toggleConnection() {
    if (!selected) {
      setSheet("import");
      return;
    }
    setRunning((value) => !value);
  }

  function testCurrent() {
    if (!selected || testing) return;
    setTesting(true);
    const id = selected.id;
    window.setTimeout(() => {
      const delay = 28 + Math.floor(Math.random() * 140);
      setServers((current) =>
        current.map((server) => (server.id === id ? { ...server, delay } : server)),
      );
      setTesting(false);
    }, 700);
  }

  function testAll() {
    setTesting(true);
    window.setTimeout(() => {
      setServers((current) =>
        current.map((server) => ({
          ...server,
          delay: 24 + Math.floor(Math.random() * 180),
        })),
      );
      setTesting(false);
      setSheet("none");
    }, 800);
  }

  function removeServer(id: string) {
    setServers((current) => current.filter((server) => server.id !== id));
    if (selectedId === id) {
      setRunning(false);
      setSelectedId("");
    }
  }

  function importDraft() {
    const imported = parseShareLinks(draft);
    if (imported.length === 0) {
      setImportError("没有认出 VMess、VLESS、Trojan、Shadowsocks 或 Hysteria2 链接。");
      return;
    }
    const next = imported.map((item) => ({
      id: crypto.randomUUID(),
      name: item.name,
      protocol: item.protocol,
      endpoint: item.endpoint,
      group: "导入",
      delay: null,
    }));
    setServers((current) => [...next, ...current]);
    setSelectedId(next[0]?.id ?? selectedId);
    setGroup("导入");
    setDraft("");
    setImportError("");
    setSheet("none");
    setSearching(false);
    setQuery("");
  }

  return (
    <div className="mx-auto flex h-dvh w-full max-w-[420px] flex-col md:h-[min(844px,calc(100dvh-4rem))] md:overflow-hidden md:rounded-[36px] md:border-[10px] md:border-[#1c1915] md:shadow-[0_30px_80px_rgba(28,25,21,0.18)]">
      <div
        data-theme={dark ? "dark" : "light"}
        className="cheng-phone relative flex min-h-0 flex-1 flex-col"
      >
        <header className="flex items-center gap-1 px-2 pt-3">
          {searching ? (
            <label className="flex min-w-0 flex-1 items-center gap-2 px-2">
              <span className="sr-only">搜索配置</span>
              <input
                autoFocus
                value={query}
                onChange={(event) => setQuery(event.target.value)}
                placeholder="搜索名称或协议"
                className="h-10 min-w-0 flex-1 bg-transparent font-serif text-lg outline-none placeholder:text-[var(--muted)]"
              />
              <button
                type="button"
                className="px-2 text-sm"
                onClick={() => {
                  setSearching(false);
                  setQuery("");
                }}
              >
                取消
              </button>
            </label>
          ) : (
            <>
              <button
                type="button"
                aria-label="打开菜单"
                className="grid size-11 place-items-center"
                onClick={() => setSheet("drawer")}
              >
                <span className="flex w-4 flex-col gap-1.5">
                  <span className="h-px bg-current" />
                  <span className="h-px bg-current" />
                  <span className="h-px w-3 bg-current" />
                </span>
              </button>
              <h1 className="flex-1 font-serif text-[1.7rem] leading-none tracking-wide">澄</h1>
              <button
                type="button"
                aria-label="搜索"
                className="grid size-11 place-items-center text-sm"
                onClick={() => setSearching(true)}
              >
                搜
              </button>
              <button
                type="button"
                aria-label="导入链接"
                className="grid size-11 place-items-center text-lg"
                onClick={() => {
                  setImportError("");
                  setSheet("import");
                }}
              >
                +
              </button>
            </>
          )}
        </header>

        {groups.length > 1 && !searching ? (
          <div className="flex gap-2 overflow-x-auto px-4 pb-2">
            {groups.map((name) => (
              <button
                key={name}
                type="button"
                onClick={() => setGroup(name)}
                className={`shrink-0 border-b px-1 py-2 text-sm ${
                  group === name ? "border-current" : "border-transparent text-[var(--muted)]"
                }`}
              >
                {name}
              </button>
            ))}
          </div>
        ) : null}

        <div className="min-h-0 flex-1 overflow-y-auto px-3 pb-4">
          {visible.length === 0 ? (
            <div className="px-3 py-16 text-center">
              <p className="font-serif text-2xl">还没有配置</p>
              <p className="mt-2 text-sm text-[var(--muted)]">
                导入一条分享链接，它只会出现在这份预览里。
              </p>
              <button
                type="button"
                className="mt-6 rounded-full bg-[var(--pine)] px-5 py-2 text-sm text-[var(--on-pine)]"
                onClick={() => setSheet("import")}
              >
                导入
              </button>
            </div>
          ) : (
            <ul className="space-y-1">
              {visible.map((server) => {
                const active = server.id === selectedId;
                return (
                  <li key={server.id}>
                    <div
                      className={`rounded-[18px] px-3 py-2.5 ${active ? "bg-[var(--card)]" : ""}`}
                    >
                      <div className="flex items-start gap-2">
                        <button
                          type="button"
                          onClick={() => selectServer(server.id)}
                          className="min-w-0 flex-1 text-left"
                        >
                          <span className="block truncate text-base">{server.name}</span>
                        </button>
                        <button
                          type="button"
                          aria-label={`删除 ${server.name}`}
                          className="px-1 text-xs text-[var(--muted)]"
                          onClick={() => removeServer(server.id)}
                        >
                          删除
                        </button>
                      </div>
                      <button
                        type="button"
                        onClick={() => selectServer(server.id)}
                        className="mt-2 flex w-full items-center justify-between gap-3 text-left text-xs"
                      >
                        <span className="truncate text-[var(--persimmon)]">
                          {server.protocol} · {server.endpoint}
                        </span>
                        <span
                          className={
                            server.delay !== null && server.delay < 0
                              ? "text-[var(--fail)]"
                              : "text-[var(--celadon)]"
                          }
                        >
                          {delayLabel(server.delay)}
                        </span>
                      </button>
                    </div>
                  </li>
                );
              })}
            </ul>
          )}
        </div>

        <footer className="border-t border-[var(--line)] px-4 py-3">
          <div className="flex items-center gap-3">
            <button type="button" onClick={testCurrent} className="min-w-0 flex-1 text-left">
              <span className="flex items-center gap-2">
                <span
                  className={`size-2 rounded-full ${running ? "bg-[var(--celadon)]" : "bg-[var(--muted)]"}`}
                />
                <span className="truncate text-sm">{statusCopy(running, selected, testing)}</span>
              </span>
              <span className="mt-1 block pl-4 text-[11px] text-[var(--muted)]">
                预览不会转发流量
              </span>
            </button>
            <button
              type="button"
              onClick={toggleConnection}
              className={`rounded-full px-5 py-2.5 text-sm ${
                running
                  ? "bg-[var(--secondary)] text-[var(--on-secondary)]"
                  : "bg-[var(--pine)] text-[var(--on-pine)]"
              }`}
            >
              {running ? "断开" : "连接"}
            </button>
          </div>
        </footer>

        {sheet !== "none" ? (
          <button
            type="button"
            aria-label="关闭"
            className="absolute inset-0 bg-black/30"
            onClick={() => setSheet("none")}
          />
        ) : null}

        {sheet === "drawer" ? (
          <nav className="absolute inset-y-0 left-0 flex w-[78%] flex-col bg-[var(--paper)] px-5 py-8">
            <p className="font-serif text-4xl">澄</p>
            <p className="mt-2 text-sm text-[var(--muted)]">v2rayNG 的个人版本</p>
            <button
              type="button"
              className="mt-8 text-left text-base"
              onClick={() => setDark((value) => !value)}
            >
              {dark ? "改回纸色" : "改成墨色"}
            </button>
            <button type="button" className="mt-5 text-left text-base" onClick={testAll}>
              测全部延迟
            </button>
            <button
              type="button"
              className="mt-5 text-left text-base"
              onClick={() => {
                setServers(samples);
                setSelectedId("tokyo");
                setGroup("全部");
                setRunning(false);
              }}
            >
              恢复示例
            </button>
            <button
              type="button"
              className="mt-5 text-left text-base"
              onClick={() => setSheet("about")}
            >
              关于
            </button>
          </nav>
        ) : null}

        {sheet === "import" ? (
          <form
            className="absolute inset-x-0 bottom-0 rounded-t-3xl bg-[var(--paper)] px-5 pb-6 pt-5"
            onSubmit={(event) => {
              event.preventDefault();
              importDraft();
            }}
          >
            <p className="font-serif text-2xl">导入</p>
            <p className="mt-1 text-sm text-[var(--muted)]">
              链接只用来显示名称和协议，不会保存密钥，也不会连出去。
            </p>
            <textarea
              value={draft}
              onChange={(event) => setDraft(event.target.value)}
              placeholder="vless://… 或 vmess://…"
              className="mt-4 h-28 w-full resize-none rounded-2xl bg-[var(--card)] px-3 py-3 text-sm outline-none"
            />
            {importError ? <p className="mt-2 text-sm text-[var(--fail)]">{importError}</p> : null}
            <div className="mt-4 flex justify-end gap-3">
              <button type="button" className="px-3 py-2 text-sm" onClick={() => setSheet("none")}>
                取消
              </button>
              <button
                type="submit"
                className="rounded-full bg-[var(--pine)] px-4 py-2 text-sm text-[var(--on-pine)]"
              >
                加入列表
              </button>
            </div>
          </form>
        ) : null}

        {sheet === "about" ? (
          <div className="absolute inset-x-4 top-24 rounded-3xl bg-[var(--paper)] p-5 shadow-lg">
            <p className="font-serif text-2xl">关于澄</p>
            <p className="mt-3 text-sm leading-6 text-[var(--muted)]">
              这是界面预览。真正转发流量的是 Android 工程，包名 com.v2ray.ang.cheng，内核来自
              v2rayNG / Xray，许可证是 GPL-3.0。
            </p>
            <button
              type="button"
              className="mt-4 text-sm"
              onClick={() => setSheet("none")}
            >
              好
            </button>
          </div>
        ) : null}
      </div>
    </div>
  );
}
