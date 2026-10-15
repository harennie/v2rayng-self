"use client";

import dynamic from "next/dynamic";

const ChengPreview = dynamic(
  () => import("@/components/cheng-preview").then((mod) => mod.ChengPreview),
  {
    ssr: false,
    loading: () => <div className="h-dvh w-full bg-[#f7f4ee] md:h-[844px] md:rounded-[36px]" />,
  },
);

export function PreviewFrame() {
  return <ChengPreview />;
}
