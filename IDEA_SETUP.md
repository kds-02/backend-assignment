# IntelliJ IDEA 설정 가이드

## 코드 변경이 반영되지 않는 문제 해결

### 1. 자동 빌드 활성화

**설정 경로:** `File` → `Settings` (또는 `Ctrl+Alt+S`) → `Build, Execution, Deployment` → `Compiler`

- ✅ **"Build project automatically"** 체크박스 활성화
- ✅ **"Compile independent modules in parallel"** 체크박스 활성화 (선택사항)

### 2. 실행 전 빌드 설정

**설정 경로:** `File` → `Settings` → `Build, Execution, Deployment` → `Build Tools` → `Gradle`

- ✅ **"Build and run using"**: `Gradle` 선택
- ✅ **"Run tests using"**: `Gradle` 선택
- ✅ **"Gradle JVM"**: 프로젝트와 동일한 JDK 버전 선택

### 3. 실행 설정 확인

**Run/Debug Configurations** (`Shift+Alt+F10` 또는 `Run` → `Edit Configurations...`)

- ✅ **"Before launch"** 섹션에서:
  - `Build` 작업이 있는지 확인
  - 없으면 `+` 버튼 클릭 → `Build` 선택

### 4. 수동 빌드 단축키

코드 변경 후 다음 중 하나를 실행:

- **빌드만:** `Ctrl+F9` (Build Project)
- **재빌드:** `Ctrl+Shift+F9` (Rebuild Project)
- **실행:** `Shift+F10` (Run)

### 5. Gradle 빌드 확인

터미널에서 다음 명령어로 수동 빌드:

```bash
./gradlew clean build -x test
```

### 6. 캐시 문제 해결

문제가 계속되면:

1. **IntelliJ IDEA 캐시 삭제:**
   - `File` → `Invalidate Caches...` → `Invalidate and Restart`

2. **Gradle 캐시 삭제:**
   ```bash
   ./gradlew clean
   rm -rf .gradle
   ```

3. **프로젝트 재임포트:**
   - `File` → `Reload Gradle Project`

### 7. Spring Boot DevTools (선택사항)

핫 리로드를 원하면 `build.gradle`에 다음을 추가:

```gradle
dependencies {
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
}
```

주의: DevTools는 클래스 변경 시 자동 재시작하지만, 설정 파일 변경이나 빈 정의 변경은 수동 재시작이 필요합니다.

