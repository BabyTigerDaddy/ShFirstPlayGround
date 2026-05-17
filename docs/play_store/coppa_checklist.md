# COPPA + Play Store Kids 정책 체크리스트

이 앱은 5살 자녀를 둔 **부모를 위한 도구**라 아이가 직접 사용하는 앱이 아니다. 그러나 콘텐츠 컨텍스트가 미성년자 관련이라 광고·데이터 수집 측면에서 보수적 정책을 적용한다.

## 데이터 수집

- [x] **아동에게서 개인정보 수집 X** — 앱이 아이로부터 어떤 정보도 받지 않음
- [x] **부모 시청 기록은 device-local만** — Room DB로 기기 내부 저장, 서버 업로드 없음
- [x] **계정 시스템 없음 (MVP)** — 회원가입·로그인 X, 익명 사용
- [x] **분석 도구 비활성화** — Firebase Analytics·Crashlytics 등 미사용 또는 child-safe 설정
- [x] **위치·연락처·카메라 권한 X** — Android 권한 INTERNET만

## 광고 (AdMob 기준)

- [ ] **광고 콘텐츠 필터** — Kids 적합 광고 카테고리만 (AdMob 콘솔에서 child-directed 트래픽 플래그 ON)
- [ ] **Interest-based ads OFF** — 행동 기반 광고 비활성화
- [ ] **광고 클릭 외부 이동 시 부모 게이트** — confirmation modal "광고로 이동할까요?"
- [ ] **NPA(Non-Personalized Ads) 적용** — 광고 요청 시 `npa=1` 파라미터
- [ ] **광고 위치 — 부모 화면에만** — 아이 보기 가능한 질문 카드 화면엔 광고 X (오버레이도 X)

## Play Store 정책

- [x] **Designed for Families 미신청** — 부모 도구 카테고리라 부적합. 일반 Parenting 카테고리로 출시
- [x] **콘텐츠 등급 3+** — 폭력·약물·도박·성적 내용 없음
- [x] **앱 콘텐츠 등급 설문지 정직 답변** — Play Console 등급 신청 시
- [x] **개인정보 처리방침 URL 게시** — `privacy_policy_draft.md` 기반 정식 페이지 호스팅 후 등록

## 한국 정책 (KISA·방통위)

- [x] **개인정보 처리방침 한국어 제공** — `privacy_policy_draft.md` 한국어 섹션
- [x] **만 14세 미만 정보 수집 X** — 아이로부터 직접 수집 X (위 COPPA 체크와 동일)
- [x] **앱 내 결제 정책 명시** — IAP는 광고 제거·콘텐츠 pack (출시 직후엔 free only)

## 출시 직전 최종 확인

- [ ] AdMob 설정 ON child-directed flag + NPA
- [ ] 개인정보 처리방침 정식 URL 등록
- [ ] 권한 요청 화면에 한 줄 안내 ("이 앱은 광고 표시 외에는 인터넷을 사용하지 않습니다")
- [ ] 첫 실행 시 부모 인증 게이트 (간단한 곱셈 문제 등) — 광고 클릭 보호용
- [ ] 외부 링크 클릭 시 confirmation modal
