# Infinite Tetris

Game Over 없이 무한으로 블록을 쌓고 제거하며 플레이하는 Android 테트리스 게임.

## 게임 컨셉

일반 테트리스와 달리 블록이 화면 2/3 이상 쌓이면 카메라가 스크롤되어 위에 빈 공간이 생기고,
라인을 클리어하면 다시 스크롤되어 아래에 숨겨진 블록들이 드러납니다.
이 과정을 반복하며 점수를 쌓아가는 무한 플레이 방식입니다.

### 핵심 메카닉

| 동작 | 설명 |
|------|------|
| 블록 쌓기 | 일반 테트리스와 동일하게 아래에서 위로 쌓임 |
| 스크롤 (아래) | 블록이 화면 2/3 이상 차면 하단 블록이 화면 밖으로 밀려남. 위에 빈 공간 생성 |
| 스크롤 (위) | 라인 클리어 시 숨겨진 블록이 다시 화면 안으로 올라옴 |
| 게임오버 | 없음 - 무한 플레이 |

## 프로젝트 구조

```
app/src/main/java/com/example/tetris/
├── MainActivity.kt                 # 앱 진입점, 라이프사이클 관리
│
├── model/                           # 데이터 모델
│   ├── Position.kt                  # 2D 좌표 (x, y)
│   ├── Cell.kt                      # 보드 셀 (TetrominoType 보유)
│   ├── TetrominoType.kt             # 7종 블록 타입 (I, O, T, S, Z, J, L)
│   ├── Tetromino.kt                 # 활성 블록 (위치, 회전, SRS 벽차기)
│   ├── GameBoard.kt                 # 보드 (Sparse Map 기반 무한 확장)
│   └── GameState.kt                 # UI 렌더링용 불변 상태 스냅샷
│
├── game/                            # 게임 로직
│   ├── GameEngine.kt                # 핵심 엔진 (이동, 회전, 충돌, 카메라 스크롤)
│   ├── GameAction.kt                # 사용자 입력 액션 (Sealed Class)
│   ├── PieceBag.kt                  # 7-bag 랜덤 블록 생성
│   └── SpeedConfig.kt               # 난이도별 낙하 속도
│
├── viewmodel/
│   └── GameViewModel.kt             # 상태 관리, 코루틴 게임 루프
│
├── ui/
│   ├── screens/
│   │   ├── MenuScreen.kt            # 시작 메뉴 (속도 선택)
│   │   ├── GameScreen.kt            # 게임 화면 레이아웃
│   │   └── PauseOverlay.kt          # 일시정지 오버레이
│   ├── components/
│   │   ├── BoardCanvas.kt           # 보드 렌더링 (Compose Canvas)
│   │   ├── ControlPad.kt            # 터치 조작 패드
│   │   ├── NextPiecePreview.kt      # 다음 블록 미리보기
│   │   └── ScoreDisplay.kt          # 점수/라인/레벨 표시
│   └── theme/
│       ├── Color.kt                 # 색상 팔레트
│       └── Theme.kt                 # Compose 테마
│
└── audio/
    ├── SoundEngine.kt               # 절차적 효과음 합성
    ├── SoundType.kt                 # 효과음 정의 (이동, 회전, 드롭, 클리어)
    └── MusicPlayer.kt               # 펜타토닉 배경 음악 루프
```

## 기술 스택

| 카테고리 | 기술 | 버전 |
|----------|------|------|
| 언어 | Kotlin | 1.9.22 |
| UI 프레임워크 | Jetpack Compose | BOM 2024.02.00 |
| 아키텍처 | MVVM (ViewModel + StateFlow) | - |
| 빌드 | Gradle (Kotlin DSL) + Version Catalog | AGP 8.2.2 |
| 최소 SDK | Android 7.0 (API 24) | - |
| 타겟 SDK | Android 15 (API 35) | - |
| JVM | Java 17 | - |

### 주요 라이브러리

- `androidx.activity:activity-compose` - Compose Activity 통합
- `androidx.lifecycle:lifecycle-viewmodel-compose` - ViewModel
- `androidx.lifecycle:lifecycle-runtime-compose` - 라이프사이클
- `androidx.compose.material3:material3` - Material 3 UI
- `androidx.compose.foundation:foundation` - Canvas, 제스처

### 외부 의존성

**없음** - 모든 기능(오디오 포함)이 Android SDK 내장 API만으로 구현되어 있습니다.

## 조작법

### 방향 버튼 (좌측)

| 버튼 | 동작 |
|------|------|
| ◀ | 왼쪽 이동 (길게 누르면 연속 이동) |
| ▶ | 오른쪽 이동 (길게 누르면 연속 이동) |
| ▼ | 소프트 드롭 (길게 누르면 연속 하강) |

### 액션 버튼 (우측)

| 버튼 | 동작 |
|------|------|
| ↻ ROTATE | 시계 방향 회전 (SRS 벽차기 지원) |
| ⬇ DROP | **짧게 탭**: 즉시 하드드롭 / **길게 누르기**: 빠른 하강, 떼면 정상 속도 |

### 상단 메뉴

| 버튼 | 동작 |
|------|------|
| ♪ ON/OFF | 배경 음악 켜기/끄기 |
| II PAUSE | 일시정지 |

## 점수 체계

| 클리어 라인 수 | 점수 |
|----------------|------|
| 1줄 | 100 |
| 2줄 | 300 |
| 3줄 | 500 |
| 4줄 (테트리스) | 800 |

## 빌드 방법

### 요구사항

- Android Studio Hedgehog (2023.1.1) 이상
- JDK 17
- Android SDK (API 35)

### 디버그 빌드

```bash
# 프로젝트 루트에서
./gradlew assembleDebug

# APK 위치
# app/build/outputs/apk/debug/app-debug.apk
```

### 릴리스 빌드

```bash
./gradlew assembleRelease

# APK 위치
# app/build/outputs/apk/release/app-release.apk
```

### 디바이스 설치

```bash
# USB 연결 또는 에뮬레이터 실행 후
adb install app/build/outputs/apk/release/app-release.apk
```

### Android Studio에서 실행

1. Android Studio에서 프로젝트 폴더 Open
2. 에뮬레이터 또는 실제 디바이스 선택
3. ▶ Run 버튼 클릭

## 아키텍처 개요

```
┌──────────────┐    GameAction     ┌───────────────┐
│   UI Layer   │ ───────────────> │  ViewModel     │
│  (Compose)   │ <─────────────── │  (StateFlow)   │
│              │    GameState      │                │
│ BoardCanvas  │                   │ GameViewModel  │
│ ControlPad   │                   └───────┬───────┘
│ ScoreDisplay │                           │
└──────────────┘                   ┌───────▼───────┐
                                   │  Game Engine   │
                                   │                │
                                   │ GameBoard      │
                                   │ Tetromino      │
                                   │ PieceBag       │
                                   │ Camera Scroll  │
                                   └───────────────┘
```

**단방향 데이터 흐름**: 터치 입력 → GameAction → ViewModel → Engine → GameState → UI 갱신

## 무한 스크롤 시스템

- **보드 저장**: `MutableMap<Int, Array<Cell?>>` (Sparse Map) - 메모리 효율적 무한 확장
- **카메라**: `cameraTopRow`가 뷰포트 상단 Y좌표 결정
- **스크롤 트리거**: 블록이 뷰포트 상단 1/3에 도달하면 카메라 상승
- **스크롤 복귀**: 라인 클리어 후 숨겨진 블록 재노출
- **바닥 고정**: `floorY = 24` 고정, 카메라만 이동
