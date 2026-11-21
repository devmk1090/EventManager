# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

"간편한 경조사 관리"(`com.devproject.eventmanager`)는 경조사비로 **나간 돈**과 **받은 돈**을 기록하고 통계를 보여 주는 Android 앱으로, Play 스토어에 배포 중이다.

- 단일 `:app` 모듈, 순수 Java(소스 호환 1.8). Kotlin 플러그인은 루트 classpath에만 있고 적용되어 있지 않다.
- 데이터는 기기 로컬 SQLite에만 있다(서버·동기화 없음).
- 주요 라이브러리: MPAndroidChart(JitPack), AdMob(`play-services-ads`), Apache POI(`app/libs/poi-4.0.1.jar`, 로컬 jar).
- UI 문자열은 대부분 Java 코드와 레이아웃 XML에 한국어로 직접 들어 있다(`strings.xml`에는 광고 ID 등 일부만 있음).

## 빌드와 테스트

`gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`는 저장소에 없고 `gradle/wrapper/gradle-wrapper.properties`(Gradle 8.11.1)만 있다. Android Studio는 이 파일만으로 빌드하지만 CLI에서는 wrapper를 먼저 만들어야 한다.

- **Gradle 8.x만 쓸 수 있다.** Gradle 9.x는 루트 `build.gradle`의 `jcenter()`가 없어진 버전이라 프로젝트 평가 단계에서 실패한다.
- JDK 17 이상과 Android SDK(platform 36, build-tools 36.0.0)가 필요하다. SDK 경로는 `ANDROID_HOME`이나 `local.properties`의 `sdk.dir`로 지정한다.

```bash
gradle wrapper --gradle-version 8.11.1   # 한 번만. Gradle 8.x로 프로젝트 루트에서 실행 (생성 파일은 현재 미커밋)
./gradlew assembleDebug
./gradlew installDebug                   # 연결된 기기/에뮬레이터에 설치
./gradlew testDebugUnitTest
./gradlew testDebugUnitTest --tests 'com.devproject.eventmanager.ExampleUnitTest'
./gradlew connectedDebugAndroidTest      # 기기/에뮬레이터 필요
./gradlew lintDebug                      # 현재 오류 2건으로 실패 (아래 "알려진 문제")
```

- 로컬 Gradle이 9.x뿐이면 프로젝트 루트에서는 `gradle wrapper`도 실패한다. 빈 `settings.gradle`만 있는 임시 폴더에서 같은 명령을 실행한 뒤 `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`만 복사한다.
- 테스트는 Android Studio 템플릿 예제(`ExampleUnitTest`, `ExampleInstrumentedTest`)뿐이다. 동작 확인은 기기나 에뮬레이터에서 직접 해야 한다.
- 릴리스할 때는 `app/build.gradle`의 `versionCode`/`versionName`을 올린다. 서명 설정은 build.gradle에 없고 `minifyEnabled false`다.
- 커밋 메시지는 `Release 2.4 - 레이아웃 수정`처럼 `Release <버전> - <한국어 변경 내용>` 형식이다. 2.4는 `feature/version-2.4` 브랜치에서 작업해 `master`로 PR 병합했다.

## 아키텍처

### 화면 구성

`SplashScreen`(약 2.5초) → `MainActivity`(Toolbar + TabLayout + `ViewPager` + 하단 AdMob 배너). `ViewPagerAdapter`의 탭 순서는 다음과 같다.

| 탭 | Fragment | 내용 |
|---|---|---|
| 0 | `OutFragment` | 나간 돈 목록, 이름 검색 |
| 1 | `InFragment` | 받은 돈 목록, 이름 검색 |
| 2 | `StatisticsFragment` | 합계, 경조사별/관계별 파이 차트 |

**`In` = 받은 돈, `Out` = 나간 돈이다.** 목록의 FAB를 누르면 `InActivity`/`OutActivity`(추가), 항목을 누르면 다이얼로그에서 `InDetailActivity`/`OutDetailActivity`(수정)나 삭제를 고른다. 툴바 메뉴는 Excel 내보내기(`MainActivity.saveExcel()`) 하나뿐이다.

### In/Out 복제 구조

받은 돈과 나간 돈은 같은 기능을 `In*`/`Out*` 클래스 쌍(Activity, DetailActivity, Adapter, Fragment, `InList`/`OutList` 모델)과 `InOutDatabase`의 `...In()`/`...Out()` 메서드 쌍으로 따로 구현했다. 차이는 호출하는 DB 메서드, 토스트 문구, 저장 후 돌아갈 탭 정도다. **한쪽을 고치면 반대쪽도 같이 고쳐야 한다.** 실제로 2023년에 목록 텍스트에 붙인 `" · "`를 `OutAdapter`에서만 걷어 내는 바람에, 받은 돈의 수정·삭제는 2.4에서 `InAdapter`를 고칠 때까지 깨져 있었다(이유는 아래 "행 식별").

두 쪽 모두 같은 레이아웃을 쓴다. 입력/수정 화면은 `activity_inout_rev.xml`, 목록 항목은 `asform_rev.xml`이다. 이름이 비슷한 `activity_inout.xml`, `asform.xml`은 쓰이지 않는다.

### 데이터 계층 (`InOutDatabase`)

- 싱글톤에 `SQLiteOpenHelper`를 직접 쓴다(Room 없음). 화면마다 `InOutDatabase.getInstance(ctx)` 다음 `open()`을 호출하는 패턴이다.
- DB 파일은 `InOut.db`, 테이블은 `IN_INFO`(받은 돈)와 `OUT_INFO`(나간 돈)이고 스키마가 같다: `_id, NAME, DATE, CATEGORY, RELATION, MONEY, MEMO, CREATE_DATE`. `MONEY`도 TEXT다.
- `DATABASE_VERSION = 1`이고 `onUpgrade()`는 비어 있다. 스키마를 바꾸려면 버전을 올리고 마이그레이션을 직접 써야 한다. `saveExcel()`은 `SELECT *` 결과를 컬럼 인덱스(1~6)로 읽으므로 컬럼 순서에도 의존한다.
- 통계용 합계는 경조사·관계 값마다 하드코딩된 메서드(`getCategoryMarryOutMoney()` 등, In/Out 각각)로 구한다.

### 행 식별: 화면에 표시된 텍스트가 키다

목록 모델(`InList`/`OutList`)에는 `_id`가 없다. 수정·삭제할 때 Adapter는 ViewHolder의 **TextView에 표시된 이름·날짜·경조사·관계 텍스트를 다시 읽어** `getSearchItemIdIn/Out()`으로 `_id`를 찾는다.

- 이 네 TextView에는 DB 값을 가공하지 말고 그대로 넣어야 한다. 구분자를 덧붙이면 수정·삭제가 깨진다. 구분자(`" · "`)는 `asform_rev.xml`의 별도 TextView로 둔다.
- 금액은 `###,###원`으로 표시해도 된다. 수정 화면으로 넘길 때 `getMoneyIn/Out(id)`로 DB 값을 다시 읽기 때문이다.
- 저장할 때 같은 (이름, 날짜, 경조사, 관계) 조합은 `checkItemIn/Out()`으로 막는다. 이 네 값의 조합이 사실상 기본 키다.

### 저장 값 형식

- **CATEGORY/RELATION**: 4개 Activity(`In/Out` × 추가/수정)에 각각 하드코딩된 배열(`categoryItems`, `relationItems`)에서 고르고, **앞뒤 공백이 붙은 채로 저장된다**(`" 결혼식 "`, `" 친구 "`). 통계 쿼리도 `WHERE CATEGORY=' 결혼식 '`처럼 공백까지 비교하므로 trim하면 기존 사용자 데이터의 통계가 깨진다. `res/values/array.xml`의 배열은 쓰이지 않는다.
- 경조사나 관계 항목을 추가하려면 4개 Activity의 배열, `InOutDatabase`의 항목별 합계 메서드, `StatisticsFragment`(항목별 TextView, `PieEntry`, 7색 배열)를 모두 고쳐야 한다.
- **DATE**: `yyyy/M/d/요일` 형식의 표시용 문자열이다(0으로 채우지 않고, 요일은 기기 로케일). 날짜는 필수 입력이 아니어서 빈 문자열일 수 있다. 요일 계산의 `new Date(year, month, dayOfMonth-1)`에서 `-1`은 deprecated 생성자의 1900년 오프셋으로 생기는 요일 차이를 메우는 꼼수다(2001~2099년 범위에서만 보장됨). `-1`만 지우면 요일이 틀어진다.
- **MONEY**: 숫자 문자열이다. 통계는 SQLite `SUM()` 결과를 `getInt()`로, 목록은 `Integer.parseInt()`로 읽는다.

### 화면 갱신 방식

목록과 통계는 Fragment의 `onCreateView`에서 한 번만 읽고, `onResume`에서 다시 읽지 않는다. 추가·수정 후에는 `MainActivity`를 새로 `startActivity`해서(플래그나 `finish()` 없음) 다시 그리고, 받은 돈 쪽은 `"In"` extra로 1번 탭을 연다. 그래서 백스택에 `MainActivity`가 쌓이고, `MainActivity`의 뒤로가기는 종료 확인 다이얼로그 후 `finishAffinity()`로 전부 닫는다.

### Excel 내보내기

Apache POI(HSSF)로 "나간 돈"/"받은 돈" 두 시트의 `.xls`를 만들고, `java.io.File`로 공용 Download 폴더에 `경조사 엑셀 데이터.xls`로 덮어쓴다. 2.4에서 `WRITE_EXTERNAL_STORAGE` 권한을 뺐으므로 이 방식은 scoped storage가 Download 폴더 쓰기를 허용하는 Android 11(API 30) 이상에서만 통한다. `checkPermission()`은 이름과 달리 `saveExcel()`만 호출한다.

### 시스템 UI (targetSdk 36)

- Android 15 이상에서는 edge-to-edge가 강제된다. 시스템 바 인셋은 각 화면 루트 레이아웃의 `android:fitsSystemWindows="true"`로 처리하고, WindowInsets 리스너는 없다.
- `WindowCompat.enableEdgeToEdge()`는 `MainActivity`, `Out*`, `SplashScreen`에서만 호출하고 `In*`에는 없다. 상태 바 아이콘 색은 `SplashScreen`을 뺀 Activity들에서 `setAppearanceLightStatusBars(true)`로 맞춘다.

### 광고

AdMob App ID는 `AndroidManifest.xml`에 있고, 배너 단위 ID는 `content_main.xml`이 참조하는 `@string/ad_unit_id_rev`(운영 ID)다. Google 테스트 배너 ID는 `strings.xml`에 주석으로 있다.

### 쓰이지 않는 레거시

아래 파일은 고쳐도 화면에 반영되지 않는다: `layout/activity_inout.xml`, `layout/asform.xml`, `values/array.xml`, `InItemClickListener.java`, `@string/ad_unit_id`. `layout/nav_header.xml`과 `menu/activity_main_drawer.xml`은 주석 처리된 NavigationView에서만 참조한다(2.4에서 내비게이션 드로어 제거).

## 알려진 문제

코드를 읽고 확인한 내용이며, 고치면 이 목록에서 지운다.

- `./gradlew lintDebug`가 오류 2건으로 실패한다. 하나는 `MainActivity.onBackPressed()`의 `MissingSuperCall`이고, 다른 하나는 `play-services-ads:21.3.0`이 Play SDK Index에서 문제 버전으로 보고되어 Play Console 게시가 막힌다는 경고다.
- `insertRecordIn/Out()`은 값을 SQL 문자열에 그대로 이어 붙인다. 이름이나 메모에 `'`가 있으면 INSERT가 실패하는데, 예외는 로그로만 남고 화면은 이미 "추가되었습니다" 토스트를 띄운 뒤다. 수정·삭제(`updateIn/Out`, `deleteDataIn/Out`)는 `ContentValues`/whereClause를 쓴다.
- 삭제 후 Adapter 리스트에서 지울 위치를 `getDeleteItemIn/Out()`(ORDER BY 없는 SELECT의 커서 위치)로 구한다. 목록은 `DATE desc`로 정렬되어 있어서 DB에서는 맞는 행이 지워져도 화면에서는 다른 항목이 사라질 수 있다.
- `ORDER BY DATE desc`가 문자열 정렬이라 실제 날짜 순서와 다르다(예: `2025/9/3`이 `2025/10/1`과 `2025/9/25`보다 위).
- 금액 입력은 `maxLength="12"`라서 int 범위를 넘는 값이 저장될 수 있다. 그런 항목을 목록에 그릴 때 `Integer.parseInt()`에서 앱이 죽는다.
- Excel 내보내기는 API 21~29 기기에서 권한이 없어 실패한다. `FileNotFoundException`은 토스트 없이 로그만 남긴다.
- targetSdk 36이라 Android 16 기기에서는 predictive back이 기본으로 켜져 `MainActivity.onBackPressed()`(종료 확인 다이얼로그)가 호출되지 않는다. `OnBackPressedCallback`으로 옮기거나 manifest에서 `android:enableOnBackInvokedCallback="false"`로 끄면 된다.
