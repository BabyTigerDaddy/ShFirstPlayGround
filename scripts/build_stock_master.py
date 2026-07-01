#!/usr/bin/env python3
"""
KRX KIND 상장법인목록에서 전종목(코스피+코스닥) 코드·이름을 받아 stock_master.json 생성.

- 키·인증 없음(거래소 공식 다운로드). 매일 GitHub Actions가 실행.
- 목록이 실제로 바뀐 날만 파일 갱신(version=그날 날짜) → 앱이 괜히 매일 재다운 안 함.
"""
import json
import os
import re
import sys
import urllib.request
from datetime import datetime, timedelta, timezone

BASE = "https://kind.krx.co.kr/corpgeneral/corpList.do?method=download&marketType="
HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
    "Referer": "https://kind.krx.co.kr/corpgeneral/corpList.do",
}
MARKETS = [("stockMkt", "KOSPI"), ("kosdaqMkt", "KOSDAQ")]
OUT = "stock_master.json"


def fetch(market_type):
    req = urllib.request.Request(BASE + market_type, headers=HEADERS)
    with urllib.request.urlopen(req, timeout=30) as r:
        return r.read().decode("euc-kr", "ignore")


def parse(html, market):
    rows = []
    for tr in re.findall(r"<tr>(.*?)</tr>", html, re.S):
        tds = re.findall(r"<td[^>]*>(.*?)</td>", tr, re.S)
        if len(tds) >= 3:  # 회사명=td[0], 종목코드=td[2]
            name = re.sub(r"<[^>]+>", "", tds[0]).strip()
            code = re.sub(r"\D", "", tds[2]).strip()
            if len(code) == 6 and name and name != "회사명":
                rows.append({"code": code, "name": name, "market": market})
    return rows


def main():
    merged = {}
    for mtype, market in MARKETS:
        for s in parse(fetch(mtype), market):
            merged[s["code"]] = s
    stocks = sorted(merged.values(), key=lambda x: x["name"])

    if len(stocks) < 1000:  # 파싱 깨짐 방어 — 비정상이면 기존 유지
        print(f"ABORT: too few stocks ({len(stocks)}) - keep existing")
        sys.exit(1)

    old = {}
    if os.path.exists(OUT):
        with open(OUT, encoding="utf-8") as f:
            old = json.load(f)
    if old.get("stocks") == stocks:
        print("no change - skip")
        return

    kst = timezone(timedelta(hours=9))
    version = datetime.now(kst).strftime("%Y-%m-%d")
    out = {"version": version, "count": len(stocks), "stocks": stocks}
    with open(OUT, "w", encoding="utf-8") as f:
        json.dump(out, f, ensure_ascii=False, separators=(",", ":"))
    print(f"updated: {len(stocks)} stocks, version {version}")


if __name__ == "__main__":
    main()
