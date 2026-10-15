export type ImportedServer = {
  name: string;
  protocol: string;
  endpoint: string;
};

function decodeBase64(input: string): string {
  const normalized = input.replace(/-/g, "+").replace(/_/g, "/");
  const padded = normalized + "=".repeat((4 - (normalized.length % 4)) % 4);
  const binary = atob(padded);
  const bytes = Uint8Array.from(binary, (char) => char.charCodeAt(0));
  return new TextDecoder().decode(bytes);
}

function remarkFromHash(value: string): string {
  const index = value.lastIndexOf("#");
  if (index < 0) return "";
  const raw = value.slice(index + 1);
  try {
    return decodeURIComponent(raw);
  } catch {
    return raw;
  }
}

function hostFromAuthority(value: string): string {
  const withoutScheme = value.replace(/^[a-z0-9+.-]+:\/\//i, "");
  const hashless = withoutScheme.split("#")[0] ?? "";
  const queryless = hashless.split("?")[0] ?? "";
  const at = queryless.lastIndexOf("@");
  return at >= 0 ? queryless.slice(at + 1) : "";
}

export function parseShareLink(raw: string): ImportedServer | null {
  const text = raw.trim();
  if (!text) return null;
  const lower = text.toLowerCase();
  const remark = remarkFromHash(text);

  if (lower.startsWith("vmess://")) {
    try {
      const payload = JSON.parse(decodeBase64(text.slice("vmess://".length).split("#")[0] ?? "")) as {
        ps?: string;
        add?: string;
        port?: string | number;
      };
      const host = payload.add ? `${payload.add}${payload.port ? `:${payload.port}` : ""}` : "VMess";
      return {
        name: payload.ps?.trim() || remark || "VMess",
        protocol: "VMess",
        endpoint: host,
      };
    } catch {
      return null;
    }
  }

  const schemes: Array<[string, string]> = [
    ["vless://", "VLESS"],
    ["trojan://", "Trojan"],
    ["ss://", "Shadowsocks"],
    ["socks://", "SOCKS"],
    ["hysteria2://", "Hysteria2"],
    ["hy2://", "Hysteria2"],
  ];

  const match = schemes.find(([scheme]) => lower.startsWith(scheme));
  if (!match) return null;
  const [scheme, protocol] = match;
  const endpoint = hostFromAuthority(text.slice(scheme.length)) || protocol;
  return {
    name: remark || protocol,
    protocol,
    endpoint,
  };
}

export function parseShareLinks(raw: string): ImportedServer[] {
  return raw
    .split(/\s+/)
    .map((part) => parseShareLink(part))
    .filter((item): item is ImportedServer => item !== null);
}
