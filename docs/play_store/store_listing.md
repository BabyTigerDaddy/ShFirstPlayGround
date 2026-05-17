# Play Store Listing 메타데이터

## 앱 정보

| 항목 | 값 | 비고 |
|---|---|---|
| 앱명 (한국) | 아이와 대화 | 출시 직전 brand 다듬기 예정 |
| 앱명 (영어) | Talk With Kid | tentative |
| 번들 ID | `com.babytigerdaddy.shfirstplayground` | release 직전 product-named로 교체 (예: `com.babytigerdaddy.kidstalk`) |
| 버전명 | 0.0.1 | semver |
| 버전 코드 | 1 | int |
| 최소 SDK | API 28 (Android 9) | |
| 타겟 SDK | API 35 (Android 15) | |

## 카테고리

| 항목 | 값 |
|---|---|
| 메인 카테고리 | Parenting (육아) |
| 보조 분류 | Education (교육) |
| 타겟 연령 | 부모 (성인) — 아이 직접 사용 아님 |
| 콘텐츠 등급 | 3+ (모든 연령) |
| Designed for Families 프로그램 | 신청 X (이 앱은 부모 도구라 키즈 카테고리 아님) |

**Designed for Families를 신청 안 하는 이유** — 아이가 직접 쓰는 앱이 아니라 부모 도구라, "Designed for Families" 프로그램은 부적합. 다만 Kids 광고 가이드라인은 적용 (광고가 children-facing일 수 있다는 가능성 대비).

## 키워드 (SEO)

### 한국어
- 아이와 대화
- 메타인지 질문
- 5살 부모
- 영상 시청 후 대화
- 부모 도움
- 아이 영상 추천
- 부모 자녀 대화

### 영어
- conversation starters for kids
- after-show questions
- metacognitive questions
- parenting tool
- 5 year old
- joint media engagement
- preschool questions
- parent-child talk

## 스크린샷 가이드

총 5장 권장 (Play Store 최대 8장). 모두 Phone 형식 1080×1920 (또는 9:16 비율).

1. **Hero 화면** — 검색바 + "아이와 본 콘텐츠를 찾아보세요" + 추천 콘텐츠 4~5개
2. **검색 결과** — "콩순이" 검색 → 해당 카드 + 메타 정보
3. **질문 카드 3개** — 카테고리 색깔 5종 중 3개 (정서·인지·회상 또는 정서·적용·비교 조합)
4. **카테고리 안내** — 5 카테고리 색깔 매핑 설명 한 장
5. **개인정보 안내** — "시청 기록은 기기 안에만" 한 줄 + 가족 그림자 silhouette

## 앱 아이콘 spec

- 사이즈: 512×512 PNG (Play Store) + 192×192 (앱 launcher)
- 스타일: Pretendard 한글 + 부드러운 키즈톤 일러스트
- 컬러: Primary navy(#4A6FA5) + 카테고리 색깔 액센트(amber Secondary)
- 모티프: 부모-아이 대화 아이콘 (말풍선 2개 겹침) 또는 책 들고 있는 곰
- 디자인 시안 초안: 출시 직전 디자이너 협업 또는 AI 생성 + 손 다듬기

## 출시 단계 체크

- [x] Scaffold + UI/data layer 1차
- [ ] 콘텐츠 시드 30 확장 (sh-briefing 작업)
- [ ] 빌드 안정성 검증 (Android Studio)
- [ ] 앱 아이콘 + 스크린샷 5장
- [ ] Description 다듬기 + 키워드 SEO 최적화
- [ ] COPPA 체크리스트 통과 (`coppa_checklist.md`)
- [ ] 개인정보 처리방침 등록 (`privacy_policy_draft.md`)
- [ ] 내부 테스트 트랙 첫 빌드
- [ ] Closed alpha (가족·지인 10명)
- [ ] Open alpha → Production
