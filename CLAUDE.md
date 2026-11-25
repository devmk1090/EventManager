# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

"간편한 경조사 관리"(`com.devproject.eventmanager`)는 경조사비로 **나간 돈**과 **받은 돈**을 기록하고 통계를 보여 주는 Android 앱으로, Play 스토어에 배포 중이다.

- 단일 `:app` 모듈, 순수 Java(소스 호환 1.8). Kotlin 플러그인은 루트 classpath에만 있고 적용되어 있지 않다.
- 데이터는 기기 로컬 SQLite에만 있다(서버·동기화 없음).
- 주요 라이브러리: Material Components 1.11(Material3 테마), AdMob(`play-services-ads`), Apache POI(`app/libs/poi-4.0.1.jar`, 로컬 jar).
- 화면 문구는 `res/values/strings.xml`에 있고, 말투는 "~해요"체다. 글꼴은 앱에 들어 있는 Pretendard(`res/font/`).

## 빌드와 테스트

`gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`는 저장소에 없고 `gradle/wrapper/gradle-wrapper.properties`(Gradle 8.11.1)만 있다. Android Studio는 이 파일만으로 빌드하지만 CLI에서는 wrapper를 먼저 만들어야 한다.

- **Gradle 8.x만 쓸 수 있다.** Gradle 9.x는 루트 `build.gradle`의 `jcenter()`가 없어진 버전이라 프로젝트 평가 단계에서 실패한다.
- JDK 17 이상과 Android SDK(platform 36, build-tools 36.0.0)가 필요하다. SDK 경로는 `ANDROID_HOME`이나 `local.properties`의 `sdk.dir`로 지정한다.

```bash
gradle wrapper --gradle-version 8.11.1   # 한 번만. Gradle 8.x로 프로젝트 루트에서 실행 (생성 파일은 현재 미커밋)
./gradlew assembleDebug
./gradlew installDebug                   # 연결된 기기/에뮬레이터에 설치
./gradlew testDebugUnitTest
./gradlew testDebugUnitTest --tests 'com.devproject.eventmanager.EntryListTest'
./gradlew connectedDebugAndroidTest      # 기기/에뮬레이터 필요
./gradlew lintDebug                      # 오류 0건 유지
```

- 로컬 Gradle이 9.x뿐이면 프로젝트 루트에서는 `gradle wrapper`도 실패한다. 빈 `settings.gradle`만 있는 임시 폴더에서 같은 명령을 실행한 뒤 `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`만 복사한다.
- 단위 테스트는 화면과 무관한 순수 로직(`EntryDateTest`, `MoneyFormatTest`, `EntryListTest`)만 있다. DB와 화면 동작은 기기나 에뮬레이터에서 직접 확인해야 한다.
- 릴리스할 때는 `app/build.gradle`의 `versionCode`/`versionName`을 올린다. 서명 설정은 build.gradle에 없고 `minifyEnabled false`다.
- 커밋 메시지는 `Release 2.4 - 레이아웃 수정`처럼 `Release <버전> - <한국어 변경 내용>` 형식이다. 작업은 `feature/...` 브랜치에서 하고 `master`로 병합한다.

## 아키텍처

### 화면 구성

`SplashScreen`(청록 바탕에 봉투 아이콘과 앱 이름, 약 2.5초. 화면을 벗어나면 넘어가지 않고 돌아오면 다시 기다린다) → `MainActivity`. `MainActivity`는 하단 메뉴(`BottomNavigationView`, `menu/bottom_nav.xml`)와 그 위의 AdMob 배너를 가지며, 세 Fragment를 한 번에 붙여 두고 숨김/보임으로 전환한다.

| 하단 메뉴 | Fragment | 내용 |
|---|---|---|
| 내역 | `HistoryFragment` | 합계 카드, 전체/받은 돈/나간 돈 필터, 이름 검색, 월별 목록 |
| 통계 | `StatisticsFragment` | 나간 돈/받은 돈 합계와 경조사별·관계별 막대 |
| 설정 | `SettingsFragment` | 엑셀 내보내기, 앱 버전 |

내역의 + 버튼은 `EntryFormActivity`(추가, 보냄/받음 선택), 목록 항목을 누르면 같은 화면이 수정 모드로 열린다(오른쪽 위 휴지통으로 삭제). 엑셀 내보내기는 내역 상단 아이콘과 설정에서 `ExcelExporter.confirmAndExport()`를 부른다.

### 데이터 모델과 DB (`Entry`, `InOutDatabase`)

- 받은 돈·나간 돈은 하나의 모델 `Entry`(+ `Entry.Direction.RECEIVED`/`SENT`)로 다룬다. DB는 예전 그대로 두 테이블이다: `IN_INFO`(받은 돈), `OUT_INFO`(나간 돈). 방향과 테이블의 대응은 `InOutDatabase.table()` 한 곳에만 있다.
- `InOutDatabase`는 싱글톤에 `SQLiteOpenHelper`를 직접 쓴다(Room 없음). 화면마다 `getInstance(ctx)` 다음 `open()`을 부르는데, `open()`은 이미 열려 있으면 그대로 쓴다.
- 두 테이블의 스키마는 같다: `_id, NAME, DATE, CATEGORY, RELATION, MONEY, MEMO, CREATE_DATE`. `MONEY`도 TEXT다. `DATABASE_VERSION = 1`이고 `onUpgrade()`는 비어 있다. 스키마를 바꾸려면 버전을 올리고 마이그레이션을 직접 써야 한다(사용자 데이터는 기기에만 있다).
- 쓰기는 `insert`/`update`/`delete(direction, …)` 세 가지이고 값은 `ContentValues`로 넘긴다. 합계는 `sum`/`count`, 통계는 `sumByCategory`/`sumByRelation`(`trim`으로 묶는 `GROUP BY`)이다. 금액 합계는 `long`이다.

### 행 식별과 중복

- `Entry`는 DB `_id`와 방향을 들고 있고, 수정·삭제는 그 둘로 한다. 수정 화면에서는 방향을 바꿀 수 없다(테이블이 다르므로).
- 같은 (이름, 날짜, 경조사, 관계) 조합은 추가할 때 `exists()`, 수정할 때 `hasOtherItem()`으로 막는다. 수정은 이 네 값을 바꾼 경우에만 검사하므로, 예전 버전에서 이미 생긴 중복 항목도 금액·메모는 고칠 수 있다.
- 2.4까지는 모델에 `_id`가 없어서 화면 텍스트로 행을 다시 찾았고, 같은 조합이 둘이면 먼저 찾은 행에 수정·삭제가 적용됐다. 예전 사용자의 DB에는 이런 중복이 남아 있을 수 있다.

### 저장 값 형식 (예전 버전과 호환 유지)

- **CATEGORY/RELATION**: 새로 저장할 때는 **앞뒤 공백을 붙인다**(`" 결혼식 "`). 예전 버전이 그렇게 저장했고, 예전 데이터와 중복 검사가 맞아야 하기 때문이다. 수정 화면은 바꾸지 않은 값이면 저장된 원문을 그대로 쓴다. 표시·통계는 `trim()`한 값을 쓴다.
- 선택지는 `EntryFormActivity`의 `CATEGORIES`/`RELATIONS` 배열에만 있다. 통계는 저장된 값으로 묶으므로 항목을 늘릴 때 다른 곳은 고치지 않아도 된다. 목록에 없는 예전 값(예: "선후배")은 수정 화면에 칩으로 하나 더 붙여 그대로 둘 수 있게 한다.
- **DATE**: 저장 형식은 `yyyy/M/d/요일`이다(0으로 채우지 않고, 요일은 기기 언어라 "Friday"일 수도 있다). 빈 문자열일 수 있다(예전에는 날짜가 선택 입력이었다). 읽기·표시는 `EntryDate`가 하고, 요일은 저장된 글자 대신 날짜로 다시 계산한다. 새 내역은 오늘 날짜로 시작한다.
- **정렬**: DATE가 문자열이라 DB에서 정렬하면 순서가 틀린다. `EntryList.sort()`가 날짜를 읽어 최신순으로 정렬하고, 날짜가 같으면 `_id`가 큰 것, 날짜 없는 내역은 맨 뒤에 둔다. 월별 묶음은 `EntryList.group()`이 만든다.
- **MONEY**: 숫자 문자열이다. `MoneyFormat.parse()`(long, 실패하면 0)로 읽고 `MoneyFormat`으로 표시한다. 입력 화면에는 한글 단위(`10만 원`)를 함께 보여 준다.

### 화면 갱신과 뒤로가기

- `HistoryFragment`·`StatisticsFragment`는 `onResume`과 `onHiddenChanged(false)`에서 DB를 다시 읽는다. `EntryFormActivity`는 저장·삭제 후 `finish()`만 하고, 돌아온 화면이 다시 읽는다.
- 뒤로가기는 `MainActivity`의 `OnBackPressedCallback`이 처리한다: 다른 탭이면 내역으로, 검색 중이면 검색 닫기, 그 밖에는 광고가 든 종료 확인 창(`ExitAdDialog`, 아래 "광고"). `onBackPressed()`를 오버라이드하지 않는다(targetSdk 36이라 Android 16 기기에서는 호출되지 않는다).

### 테마·스타일

- `AppTheme`은 `Theme.Material3.Light.NoActionBar`에 시안 B 팔레트(`colors.xml`: 먹색 `ink`, 청록 `accent`, 바탕 `background`)를 넣은 것이다. 스플래시만 `SplashTheme`(청록 바탕, 상태 표시줄 아이콘 밝게)이다. 이 바탕색이 Android 12+ 시스템 시작 화면의 배경이 되므로, `activity_splashscreen.xml`은 아이콘을 시스템 화면과 같은 크기로 화면 한가운데에 두어 두 화면이 이어져 보이게 한다. 아이콘 바탕색이 창 바탕색과 같으면 시스템이 바탕 없이 앞면만 1.2배로 그리므로 상자는 288dp다(바탕색을 바꾸면 240dp로 줄어든다). M3 대화상자(`MaterialAlertDialogBuilder`)는 기본 표면색이 보라빛이라 `colorSurfaceContainer*`를 모두 `surface`(흰색)로 덮고 `elevationOverlayEnabled`를 끈다.
- 글자는 `style="@style/Text.…"`(`Title`, `Section`, `Strong`, `Label`, `Caption`, `Amount`)로 맞춘다. **글꼴을 바꾸는 스타일·뷰에는 `android:fontFamily`와 `fontFamily`(app)를 함께 지정해야 한다.** 테마의 `fontFamily`(app)를 AppCompat이 나중에 덮어써서, `android:fontFamily`만 주면 굵은 글꼴이 적용되지 않는다.
- 금액은 `Text.Amount`(굵게, `tnum` 고정폭 숫자). 받은 돈은 `+`와 청록, 나간 돈은 `−`(U+2212)와 먹색이다.
- 같은 달 내역은 흰 카드 하나로 보이도록 `EntryAdapter`가 줄마다 `bg_card_top/middle/bottom/single`을 고른다.

### 시스템 UI (targetSdk 36)

- 모든 Activity가 `WindowCompat.enableEdgeToEdge()`를 쓴다. `MainActivity`는 루트에 상태 표시줄만큼 위 여백을 주고, 아래 여백은 `BottomNavigationView`가 스스로 준다. `EntryFormActivity`는 아래를 내비게이션 바와 키보드 중 큰 쪽만큼 띄운다(manifest `adjustResize`).

### 앱 아이콘

- manifest는 `@mipmap/ic_launcher`(`roundIcon`은 `ic_launcher_round`)를 쓴다. 모양은 세로로 긴 경조사 봉투의 뒷면(덮개)을 살짝 기울인 것이다.
- API 26 이상은 `mipmap-anydpi-v26`의 적응형 아이콘이다. 바탕은 `@color/ic_launcher_background`, 앞면은 `drawable/ic_launcher_foreground.xml`, Android 13 이상의 테마 아이콘은 `drawable/ic_launcher_monochrome.xml`(덮개만 반투명)을 쓴다. 두 벡터는 512×512 스토어 아이콘과 같은 좌표로 그리고, 바깥 그룹(18 이동, 0.140625배)으로 가운데 72dp 안에 넣는다.
- API 21~25는 `mipmap-*dpi`의 PNG(둥근 사각형 `ic_launcher`, 원형 `ic_launcher_round`)를 쓴다. 모양을 바꾸면 벡터 두 개, PNG 10장, Play 스토어 아이콘을 함께 바꾼다.

### Excel 내보내기 (`ExcelExporter`)

Apache POI(HSSF)로 "나간 돈"/"받은 돈" 두 시트의 `.xls`를 만들고, `java.io.File`로 공용 Download 폴더에 `경조사 엑셀 데이터.xls`로 덮어쓴다. 경조사·관계는 trim, 금액은 숫자 셀로 쓴다. 2.4에서 `WRITE_EXTERNAL_STORAGE` 권한을 뺐으므로 Android 11(API 30) 이상에서만 통한다(아래 "알려진 문제").

### 광고

AdMob App ID는 `AndroidManifest.xml`에 있고, 광고 단위 ID는 `@string/ad_unit_id_rev`(배너 운영 ID) 하나를 두 곳에서 쓴다.

- 하단 배너(320x50): `activity_main.xml`의 `AdView`, 하단 메뉴 바로 위.
- 종료 확인 창(300x250, `ExitAdDialog` + `dialog_exit.xml`): 창을 열자마자 보이도록 `MainActivity` 시작 3초 뒤에 미리 불러 두고, 창을 열 때마다 같은 `AdView`를 떼었다 붙여 다시 쓴다. 3초 안에 종료하면 그때 불러오고, 불러오지 못했으면 광고 자리를 접고 창만 띄운다. 창 밖을 눌러도 닫히지 않는다. M2 앱(`~/Documents/M2`)과 같은 방식이다.
- **디버그 빌드는 `app/src/debug/res/values/ad_ids.xml`이 `ad_unit_id_rev`를 Google 테스트 ID로 덮어쓴다.** 릴리스 빌드에만 운영 ID가 들어간다. 광고 단위를 새로 추가하면 이 파일에도 테스트 ID를 같이 넣는다.

`play-services-ads`는 23.6.0이다. 24.0.0부터 minSdk 23(25.x는 24)을 요구하므로, minSdk 21을 유지하는 동안은 23.x가 상한이다. 이전에 쓰던 21.3.0은 Play SDK Index에서 Play Console 게시 차단 대상으로 표시되어 있었다.

### 쓰이지 않는 리소스

`font/pretendard_family.xml`은 참조되지 않는다.

## 알려진 문제

코드를 읽고 확인한 내용이며, 고치면 이 목록에서 지운다.

- Excel 내보내기는 API 21~29 기기에서 저장 권한이 없어 실패한다(실패 토스트는 뜬다). 고치려면 MediaStore(API 29+)나 SAF로 저장해야 한다.
