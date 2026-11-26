# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

"경조사 관리"(`com.devproject.eventmanager`, Play 스토어 이름은 "간편한 경조사 관리")는 경조사비로 **나간 돈**과 **받은 돈**을 기록하고 통계를 보여 주는 Android 앱으로, Play 스토어에 배포 중이다.

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
- 단위 테스트는 화면과 무관한 순수 로직(`EntryDateTest`, `MoneyFormatTest`, `EntryListTest`, `EntryStatsTest`, `DatabaseBackupTest`, `ExcelExporterTest`)만 있다. DB와 화면 동작은 기기나 에뮬레이터에서 직접 확인해야 한다.
- 릴리스할 때는 `app/build.gradle`의 `versionCode`/`versionName`을 올린다. 서명 설정은 build.gradle에 없고 `minifyEnabled false`다.
- 커밋 메시지는 `Release 2.4 - 레이아웃 수정`처럼 `Release <버전> - <한국어 변경 내용>` 형식이다. 작업은 `feature/...` 브랜치에서 하고 `master`로 병합한다.

## 아키텍처

### 화면 구성

`SplashScreen`(청록 바탕에 봉투 아이콘과 앱 이름, 약 2.5초. 화면을 벗어나면 넘어가지 않고 돌아오면 다시 기다린다) → `MainActivity`. `MainActivity`는 하단 메뉴(`BottomNavigationView`, `menu/bottom_nav.xml`)와 그 위의 AdMob 배너를 가지며, 세 Fragment를 한 번에 붙여 두고 숨김/보임으로 전환한다.

| 하단 메뉴 | Fragment | 내용 |
|---|---|---|
| 내역 | `HistoryFragment` | 합계 카드, 전체/받은 돈/나간 돈 필터, 이름 검색, 월별 목록 |
| 통계 | `StatisticsFragment` | 기간(전체·연도별)을 골라 나간 돈/받은 돈 합계와 경조사별·관계별·사람별 막대 |
| 설정 | `SettingsFragment` | 백업 파일 저장·보내기·불러오기·백업 방법 안내, 엑셀 파일로 저장, 앱 버전 |

내역의 + 버튼은 `EntryFormActivity`(추가, 보냄/받음 선택), 목록 항목을 누르면 같은 화면이 수정 모드로 열린다(오른쪽 위 휴지통으로 삭제). 엑셀 저장은 설정에만 있다(아래 "Excel 내보내기").

사람별 화면:
- 통계의 사람별 막대(상위 5명)의 "전체 보기"는 `PeopleActivity`(그 기간의 사람 목록, 주고받은 합계 큰 순, 이름 검색)를 연다. 사람을 누르면 `PersonActivity`(그 사람과 주고받은 합계 카드와 월별 내역, 기간 구분 없이 전부)로 가고, 추가 버튼은 이름을 채운 새 내역을 연다. `PersonActivity`는 `EntryAdapter`를 필터 칩 없이(`showFilters = false`) 쓴다.
- 이름은 앞뒤 공백을 빼고 똑같으면 같은 사람으로 본다(`EntryStats.withName`). 부분 일치나 동명이인은 구분하지 않는다.
- `EntryFormActivity`는 이름을 쓰면 잠시 뒤(250ms) 같은 이름의 다른 내역을 찾아 "이 분과 주고받은 내역 n건 · 받은 돈 · 나간 돈" 안내를 이름 칸 아래에 보여 준다(수정 중인 내역은 뺀다). 누르면 `PersonActivity`.

### 데이터 모델과 DB (`Entry`, `InOutDatabase`)

- 받은 돈·나간 돈은 하나의 모델 `Entry`(+ `Entry.Direction.RECEIVED`/`SENT`)로 다룬다. DB는 예전 그대로 두 테이블이다: `IN_INFO`(받은 돈), `OUT_INFO`(나간 돈). 방향과 테이블의 대응은 `InOutDatabase.table()` 한 곳에만 있다.
- `InOutDatabase`는 싱글톤에 `SQLiteOpenHelper`를 직접 쓴다(Room 없음). 화면마다 `getInstance(ctx)` 다음 `open()`을 부르는데, `open()`은 이미 열려 있으면 그대로 쓴다.
- 두 테이블의 스키마는 같다: `_id, NAME, DATE, CATEGORY, RELATION, MONEY, MEMO, CREATE_DATE`. `MONEY`도 TEXT다. `DATABASE_VERSION = 1`이고 `onUpgrade()`는 비어 있다. 스키마를 바꾸려면 버전을 올리고 마이그레이션을 직접 써야 한다(사용자 데이터는 기기에만 있다).
- 쓰기는 `insert`/`update`/`delete(direction, …)` 세 가지이고 값은 `ContentValues`로 넘긴다. DB에서 바로 세는 것은 `count`뿐이다. 합계, 경조사별·관계별·사람별 묶음, 연도로 거르기는 `selectAll()` 결과를 `EntryStats`(순수 로직, 단위 테스트)가 계산한다. 금액은 `MoneyFormat.parse()`로 읽어 `long`으로 더하고, 경조사·관계·이름은 `trim`해서 묶는다(빈 값은 "미지정"). 날짜 없는 내역은 "전체 기간"에만 들어간다.

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

- `HistoryFragment`·`StatisticsFragment`는 `onResume`과 `onHiddenChanged(false)`에서, `PeopleActivity`·`PersonActivity`·`EntryFormActivity`(이름 안내)는 `onResume`에서 DB를 다시 읽는다. `EntryFormActivity`는 저장·삭제 후 `finish()`만 하고, 돌아온 화면이 다시 읽는다.
- 뒤로가기는 `MainActivity`의 `OnBackPressedCallback`이 처리한다: 다른 탭이면 내역으로, 검색 중이면 검색 닫기, 그 밖에는 광고가 든 종료 확인 창(`ExitAdDialog`, 아래 "광고"). `onBackPressed()`를 오버라이드하지 않는다(targetSdk 36이라 Android 16 기기에서는 호출되지 않는다).

### 테마·스타일

- `AppTheme`은 `Theme.Material3.Light.NoActionBar`에 시안 B 팔레트(`colors.xml`: 먹색 `ink`, 청록 `accent`, 바탕 `background`)를 넣은 것이다. 스플래시만 `SplashTheme`(청록 바탕, 상태 표시줄 아이콘 밝게)이다. 이 바탕색이 Android 12+ 시스템 시작 화면의 배경이 되므로, `activity_splashscreen.xml`은 아이콘을 시스템 화면과 같은 크기로 화면 한가운데에 두어 두 화면이 이어져 보이게 한다. 아이콘 바탕색이 창 바탕색과 같으면 시스템이 바탕 없이 앞면만 1.2배로 그리므로 상자는 288dp다(바탕색을 바꾸면 240dp로 줄어든다). M3 대화상자(`MaterialAlertDialogBuilder`)는 기본 표면색이 보라빛이라 `colorSurfaceContainer*`를 모두 `surface`(흰색)로 덮고 `elevationOverlayEnabled`를 끈다.
- 글자는 `style="@style/Text.…"`(`Title`, `Section`, `Strong`, `Label`, `Caption`, `Amount`)로 맞춘다. **글꼴을 바꾸는 스타일·뷰에는 `android:fontFamily`와 `fontFamily`(app)를 함께 지정해야 한다.** 테마의 `fontFamily`(app)를 AppCompat이 나중에 덮어써서, `android:fontFamily`만 주면 굵은 글꼴이 적용되지 않는다.
- 금액은 `Text.Amount`(굵게, `tnum` 고정폭 숫자). 받은 돈은 `+`와 청록, 나간 돈은 `−`(U+2212)와 먹색이다.
- 같은 달 내역은 흰 카드 하나로 보이도록 `EntryAdapter`가 줄마다 `bg_card_top/middle/bottom/single`을 고른다.
- 하단 메뉴(`Widget.App.BottomNav`)는 높이 64dp(`android:minHeight`)이고 활성 표시(알약 배경)가 없다. 아이콘은 위에서 `itemPaddingTop`(11dp), 글자는 아래에서 `itemPaddingBottom`(15dp)만큼 떨어져 놓인다. Material3 기본값(위 12dp, 아래 16dp, 활성 표시 높이 32dp)은 높이 80dp 기준이라, 높이만 줄이면 글자가 아이콘에 붙는다. 높이를 바꾸면 두 여백도 함께 맞춘다. 탭 글자는 Material 1.11이 시스템 글자 크기와 상관없이 고정 크기로 그린다.

### 시스템 UI (targetSdk 36)

- 모든 Activity가 `WindowCompat.enableEdgeToEdge()`를 쓴다. `MainActivity`는 루트에 상태 표시줄만큼 위 여백을 주고, 아래 여백은 `BottomNavigationView`가 스스로 준다. `EntryFormActivity`는 아래를 내비게이션 바와 키보드 중 큰 쪽만큼 띄운다(manifest `adjustResize`).

### 앱 아이콘

- manifest는 `@mipmap/ic_launcher`(`roundIcon`은 `ic_launcher_round`)를 쓴다. 모양은 세로 경조사 봉투(앞면) 위로 오만 원권 두 장이 나와 있는 것을 살짝 기울인 것이다(덮개는 그리지 않는다).
- API 26 이상은 `mipmap-anydpi-v26`의 적응형 아이콘이다. 바탕은 `@color/ic_launcher_background`, 앞면은 `drawable/ic_launcher_foreground.xml`, Android 13 이상의 테마 아이콘은 `drawable/ic_launcher_monochrome.xml`을 쓴다. 테마 아이콘은 몸통과 앞 지폐만 그리고, 앞 지폐는 몸통 위로 나온 부분만 틈을 두고 잘라(`clip-path`) 그린다. 지폐의 테두리·동그라미 무늬 구멍은 `fillType="evenOdd"` 대신 반대 방향으로 그린 도형으로 낸다. `fillType` 같은 API 24 기능을 쓰면 minSdk 21이라 빌드가 쓰이지도 않는 옛 기기용 PNG를 따로 만들고 lint가 `VectorRaster` 경고를 낸다. 두 벡터는 512×512 스토어 아이콘과 같은 좌표로 그리고, 바깥 그룹(18 이동, 0.140625배)으로 가운데 72dp 안에 넣는다. 안쪽 그룹은 그림 전체를 1.06배 키우고 -8도 기울인 뒤, 지폐까지 포함한 그림이 가운데에 오도록 옮긴다.
- API 21~25는 `mipmap-*dpi`의 PNG(둥근 사각형 `ic_launcher`, 원형 `ic_launcher_round`)를 쓴다. 모양을 바꾸면 벡터 두 개, PNG 10장, Play 스토어 아이콘을 함께 바꾼다.

### 백업·불러오기 (`DatabaseBackup`)

- DB 파일(`InOut.db`)을 통째로 복사한다. 저장하거나 불러올 위치는 시스템 파일 창(SAF, `CreateDocument`/`OpenDocument`)에서 사용자가 고르므로 권한이나 로그인이 필요 없다. Google 드라이브는 드라이브 앱이 그 창에 나와서 쓸 수 있다. 기본 파일 이름은 `경조사 백업 yyyy-MM-dd.db`이고, 이름·확장자가 바뀐 파일도 고를 수 있게 열 때는 모든 파일(`*/*`)을 보여 준다.
- 불러올 때는 앱 캐시로 복사한 뒤 내용으로 검사한다: SQLite 머리글, `user_version`(1 이상 `DATABASE_VERSION` 이하. 더 높으면 새 버전 앱의 백업이라 거절), 두 테이블과 필수 컬럼, `PRAGMA quick_check`. 통과하면 백업과 지금 데이터의 건수, 백업의 마지막 날짜를 보여 주고 확인을 받아 바꾼다.
- 바꾸기 직전 DB는 `noBackupFilesDir/before-restore.db`에 보관하고 완료 창의 "되돌리기"로 되돌린다(다음 불러오기 때 덮어쓴다). 파일을 바꾸다 실패하면 원래 파일로 되돌린다.
- 파일 읽기·쓰기는 백그라운드 스레드 하나에서, DB 연결을 닫고 파일을 복사·교체하는 일은 메인 스레드에서 `InOutDatabase.withClosedConnection()`으로 한다. 싱글톤을 비우는 `close()`와 달리 객체를 그대로 두므로 화면들이 들고 있는 참조가 계속 산다. 내역·통계 탭은 다시 보일 때 DB를 새로 읽는다.
- DB 스키마를 바꿔 `DATABASE_VERSION`을 올리면 예전 백업은 불러온 뒤 `onUpgrade`를 거친다. 마이그레이션은 예전 백업 파일에도 맞아야 한다.
- 처음 "백업 파일 저장"을 누르면 안내 시트(`sheet_backup_guide.xml`, 단계 줄은 `item_guide_step.xml`)를 먼저 띄우고, 그 뒤로는 바로 저장 창을 연다. 설정의 "백업 방법 보기"로 언제든 다시 볼 수 있다. 안내를 봤는지와 마지막 백업 시각은 SharedPreferences `backup`(`guide_shown`, `last_backup`)에 두고, 마지막 백업 날짜를 백업 항목 설명에 보여 준다.
- Google 드라이브 앱이 없거나 꺼져 있으면(갤럭시에서 한 번도 열지 않은 기본 앱은 "사용할 때까지 꺼 둠") 저장 창에 드라이브가 나오지 않는다. 안내 시트는 `DatabaseBackup.isDriveAvailable()`(드라이브 문서 제공자 `com.google.android.apps.docs.storage` 조회, manifest `<queries>`에 선언)로 확인해 "Google 드라이브 켜기"(Play 스토어의 드라이브 페이지) 버튼을 보여 준다.
- "백업 파일 보내기"는 `cache/share`에 백업 파일을 만들어 FileProvider(`${applicationId}.backupfiles`, `res/xml/backup_file_paths.xml`)로 공유 창에 넘긴다(카카오톡 나에게 보내기, 메일 등). 받는 앱이 나중에 읽을 수 있어 이전 공유 파일은 다음에 만들 때 지운다. 보냈는지 알 수 없으므로 마지막 백업 날짜는 바꾸지 않는다.

### Excel 내보내기 (`ExcelExporter`)

Apache POI(HSSF)로 "나간 돈"/"받은 돈" 두 시트의 `.xls`를 만든다. 설정의 "엑셀 파일로 저장"이 시스템 저장 창(SAF `CreateDocument`, 기본 이름 `경조사 내역 yyyy-MM-dd.xls`)을 열고, 고른 곳에 쓴다. 저장소 권한이 필요 없어 모든 Android 버전에서 된다(2.5까지는 `java.io.File`로 Download 폴더에 써서 Android 10 이하에서 실패했다). DB는 메인 스레드에서 읽고, 엑셀 만들기와 쓰기는 백그라운드에서 한다. 경조사·관계는 trim, 금액은 숫자 셀로 쓴다.

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

- 현재 없음.
