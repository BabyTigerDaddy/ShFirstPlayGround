# ShFirstPlayGround

5살 부모용 콘텐츠 추천 + 메타인지 질문 생성 Android 앱.

## 컨셉

아이와 함께 본 콘텐츠를 입력하면 시청 후 부모-아이 대화에 쓸 메타인지 질문을 자동 생성. 아이의 발달 단계·관심사·언어 노출 비중에 맞춘 콘텐츠 큐레이션도 함께.

임시 앱명: **큰형 콩순이** (출시 시 변경 가능)

## User Story

| | 사용자 | 행동 | 가치 |
|---|---|---|---|
| Story 1 (MVP) | 부모 | 아이와 본 콘텐츠 제목 입력 → 메타인지 질문 3개 받음 | 시청 후 아이와 대화 즉시 활용 |
| Story 2 | 부모 | 아침 푸시 알림으로 추천 콘텐츠 + 질문 받음 | 매일 콘텐츠 고르고 질문 준비 부담 줄임 |
| Story 3 | 부모 | 시청 기록 누적 → 발달·관심사 트래킹 리포트 | 콘텐츠 맞춤 큐레이션 + 발달 변화 인지 |

## 기술 스택

- Kotlin 2.0.21 / AGP 8.7.3 / Gradle 8.9 / JVM 17
- Android compileSdk 35, minSdk 28, targetSdk 35
- Jetpack Compose (BOM 2024.10.01) + Material 3
- Hilt 2.52 (DI), Room 2.6.1 (local DB), Navigation Compose 2.8.4
- 추후: LLM API 호출(질문 generator), Firebase Cloud Messaging(알림), AdMob + IAP(수익)

## 아키텍처

표준 layered (Clean Architecture lite):

```
app/src/main/java/com/babytigerdaddy/shfirstplayground/
├── ShFirstPlayGroundApp.kt   # Hilt Application
├── MainActivity.kt           # Compose entry point
├── data/                     # Room entities, DAOs, repositories (소보고 담당)
├── domain/                   # Use cases, 도메인 모델, 추천/질문 generator (소보고 담당)
├── ui/                       # Compose screens, theme (소문서 담당)
└── di/                       # Hilt modules
```

## 분담

- **소보고 (sh-briefing)** — 데이터·도메인·추천 알고리즘·메타인지 질문 generator. 콘텐츠 metadata 큐레이션(초기 100~200개 정적 JSON), 추천 로직, 질문 template 매칭.
- **소문서 (sh-documents)** — UI/UX·Compose 화면·Play Store 페이지·마케팅 이미지. 디자인 시스템, 화면 흐름, 출시 산출물.

## 수익화

- 무료: AdMob (Kids 광고 카테고리 + COPPA 가이드라인 통과)
- 프리미엄 IAP: 콘텐츠 pack 확장 + pro 질문 템플릿 + 광고 제거

## 빌드

```bash
./gradlew assembleDebug
```

(Gradle wrapper는 Android Studio에서 동기화 후 자동 생성. 첫 commit엔 wrapper 미포함 — 다음 commit에 추가 예정.)

## 현황 (2026-05-17)

- [x] Scaffold (Gradle Kotlin DSL + Compose + Hilt + Room 표준 결)
- [x] MainActivity placeholder Compose 화면
- [ ] data layer (소보고)
- [ ] domain layer + 질문 generator (소보고)
- [ ] ui flows (소문서)
- [ ] Play Store metadata (소문서)
