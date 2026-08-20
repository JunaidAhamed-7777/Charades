# 📁 Charades - Project Structure

*Generated on: 8/20/2026, 3:51:21 PM*

## 📋 Quick Overview

| Metric | Value |
|--------|-------|
| 📄 Total Files | 64 |
| 📁 Total Folders | 43 |
| 🌳 Max Depth | 10 levels |

## ⭐ Important Files

- 🟡 🚫 **.gitignore** - Git ignore rules
- 🔴 📖 **README.md** - Project documentation

## 📊 File Statistics

### By File Type

- 📄 **.kt** (Other files): 43 files (67.2%)
- ⚙️ **.xml** (XML files): 5 files (7.8%)
- 📄 **.kts** (Other files): 3 files (4.7%)
- 📄 **.properties** (Other files): 3 files (4.7%)
- 🖼️ **.png** (PNG images): 2 files (3.1%)
- 🚫 **.gitignore** (Git ignore): 1 files (1.6%)
- ⚙️ **.json** (JSON files): 1 files (1.6%)
- 📄 **.txt** (Text files): 1 files (1.6%)
- ⚙️ **.toml** (TOML files): 1 files (1.6%)
- 📄 **.jar** (Other files): 1 files (1.6%)
- 📄 **.** (Other files): 1 files (1.6%)
- 📄 **.bat** (Other files): 1 files (1.6%)
- 📖 **.md** (Markdown files): 1 files (1.6%)

### By Category

- **Other**: 52 files (81.3%)
- **Config**: 7 files (10.9%)
- **Docs**: 2 files (3.1%)
- **Assets**: 2 files (3.1%)
- **DevOps**: 1 files (1.6%)

### 📁 Largest Directories

- **root**: 64 files
- **app**: 53 files
- **app\src\main**: 52 files
- **app\src**: 52 files
- **app\src\main\java\com\yourdomain\freecharades**: 43 files

## 🌳 Directory Structure

```
Charades/
├── 🟡 🚫 **.gitignore**
├── 🚀 app/
│   ├── 📄 build.gradle.kts
│   └── 📁 src/
│   │   └── 📂 main/
│   │   │   ├── ⚙️ AndroidManifest.xml
│   │   │   ├── 📦 assets/
│   │   │   │   ├── 📂 categories/
│   │   │   │   │   └── ⚙️ bundled_categories.json
│   │   │   │   └── 📄 privacy_policy.txt
│   │   │   ├── 📂 java/
│   │   │   │   └── 📂 com/
│   │   │   │   │   └── 📂 yourdomain/
│   │   │   │   │   │   └── 📂 freecharades/
│   │   │   │   │   │   │   ├── 📂 data/
│   │   │   │   │   │   │   │   ├── 📂 local/
│   │   │   │   │   │   │   │   │   ├── 📄 AppDatabase.kt
│   │   │   │   │   │   │   │   │   ├── 📄 Converters.kt
│   │   │   │   │   │   │   │   │   ├── 📂 dao/
│   │   │   │   │   │   │   │   │   │   ├── 📄 CategoryDao.kt
│   │   │   │   │   │   │   │   │   │   └── 📄 WordDao.kt
│   │   │   │   │   │   │   │   │   └── 📂 entity/
│   │   │   │   │   │   │   │   │   │   ├── 📄 CategoryEntity.kt
│   │   │   │   │   │   │   │   │   │   └── 📄 WordEntity.kt
│   │   │   │   │   │   │   │   ├── 📂 remote/
│   │   │   │   │   │   │   │   │   ├── 📂 dto/
│   │   │   │   │   │   │   │   │   │   ├── 📄 CategoryDto.kt
│   │   │   │   │   │   │   │   │   │   ├── 📄 ManifestDto.kt
│   │   │   │   │   │   │   │   │   │   └── 📄 WordDto.kt
│   │   │   │   │   │   │   │   │   ├── 📄 GitHubApi.kt
│   │   │   │   │   │   │   │   │   └── 📄 NetworkModule.kt
│   │   │   │   │   │   │   │   ├── 📂 repository/
│   │   │   │   │   │   │   │   │   ├── 📄 CategoryRepository.kt
│   │   │   │   │   │   │   │   │   └── 📄 UpdateManager.kt
│   │   │   │   │   │   │   │   └── 📂 util/
│   │   │   │   │   │   │   │   │   ├── 📄 ConnectivityHelper.kt
│   │   │   │   │   │   │   │   │   └── 📄 FileUtils.kt
│   │   │   │   │   │   │   ├── 📂 domain/
│   │   │   │   │   │   │   │   ├── 📂 model/
│   │   │   │   │   │   │   │   │   ├── 📄 Category.kt
│   │   │   │   │   │   │   │   │   └── 📄 Word.kt
│   │   │   │   │   │   │   │   └── 📂 usecase/
│   │   │   │   │   │   │   │   │   ├── 📄 CheckForUpdatesUseCase.kt
│   │   │   │   │   │   │   │   │   ├── 📄 GetCategoriesUseCase.kt
│   │   │   │   │   │   │   │   │   └── 📄 GetWordsForCategoryUseCase.kt
│   │   │   │   │   │   │   ├── 📄 FreeCharadesApp.kt
│   │   │   │   │   │   │   ├── 📄 MainActivity.kt
│   │   │   │   │   │   │   ├── 🎨 ui/
│   │   │   │   │   │   │   │   ├── 🧩 components/
│   │   │   │   │   │   │   │   │   ├── 📄 CategoryButton.kt
│   │   │   │   │   │   │   │   │   ├── 📄 CountdownText.kt
│   │   │   │   │   │   │   │   │   └── 📄 SourceFooter.kt
│   │   │   │   │   │   │   │   ├── 📂 navigation/
│   │   │   │   │   │   │   │   │   └── 📄 AppNavHost.kt
│   │   │   │   │   │   │   │   ├── 📂 screens/
│   │   │   │   │   │   │   │   │   ├── 📂 categoryDetail/
│   │   │   │   │   │   │   │   │   │   ├── 📄 CategoryDetailScreen.kt
│   │   │   │   │   │   │   │   │   │   └── 📄 CategoryDetailViewModel.kt
│   │   │   │   │   │   │   │   │   ├── 📂 game/
│   │   │   │   │   │   │   │   │   │   ├── 📄 CountdownOverlay.kt
│   │   │   │   │   │   │   │   │   │   ├── 📄 GameScreen.kt
│   │   │   │   │   │   │   │   │   │   ├── 📄 GameViewModel.kt
│   │   │   │   │   │   │   │   │   │   └── 📄 GuessCounter.kt
│   │   │   │   │   │   │   │   │   ├── 📂 menu/
│   │   │   │   │   │   │   │   │   │   ├── 📄 MenuScreen.kt
│   │   │   │   │   │   │   │   │   │   └── 📄 MenuViewModel.kt
│   │   │   │   │   │   │   │   │   ├── 📂 privacy/
│   │   │   │   │   │   │   │   │   │   └── 📄 PrivacyPolicyScreen.kt
│   │   │   │   │   │   │   │   │   └── 📂 settings/
│   │   │   │   │   │   │   │   │   │   ├── 📄 SettingsScreen.kt
│   │   │   │   │   │   │   │   │   │   └── 📄 SettingsViewModel.kt
│   │   │   │   │   │   │   │   └── 📂 theme/
│   │   │   │   │   │   │   │   │   ├── 📄 Color.kt
│   │   │   │   │   │   │   │   │   ├── 📄 Shape.kt
│   │   │   │   │   │   │   │   │   ├── 📄 Theme.kt
│   │   │   │   │   │   │   │   │   └── 📄 Type.kt
│   │   │   │   │   │   │   └── 📂 util/
│   │   │   │   │   │   │   │   ├── 📄 Constants.kt
│   │   │   │   │   │   │   │   └── 📄 ShuffleUtils.kt
│   │   │   └── 📂 res/
│   │   │   │   ├── 📂 drawable/
│   │   │   │   ├── 📂 font/
│   │   │   │   ├── 📂 mipmap-anydpi-v26/
│   │   │   │   │   └── ⚙️ ic_launcher.xml
│   │   │   │   ├── 📂 mipmap-hdpi/
│   │   │   │   ├── 📂 mipmap-mdpi/
│   │   │   │   │   ├── 🖼️ ic_launcher_round.png
│   │   │   │   │   └── 🖼️ ic_launcher.png
│   │   │   │   ├── 📂 mipmap-xhdpi/
│   │   │   │   ├── 📂 mipmap-xxhdpi/
│   │   │   │   ├── 📂 mipmap-xxxhdpi/
│   │   │   │   └── 📂 values/
│   │   │   │   │   ├── ⚙️ colors.xml
│   │   │   │   │   ├── ⚙️ strings.xml
│   │   │   │   │   └── ⚙️ themes.xml
├── 📄 build.gradle.kts
├── 📂 gradle/
│   ├── ⚙️ libs.versions.toml
│   └── 📂 wrapper/
│   │   ├── 📄 gradle-wrapper.jar
│   │   └── 📄 gradle-wrapper.properties
├── 📄 gradle.properties
├── 📄 gradlew
├── 📄 gradlew.bat
├── 📄 local.properties
├── 🔴 📖 **README.md**
└── 📄 settings.gradle.kts
```

## 📖 Legend

### File Types
- 🚫 DevOps: Git ignore
- 📄 Other: Other files
- ⚙️ Config: XML files
- ⚙️ Config: JSON files
- 📄 Docs: Text files
- 🖼️ Assets: PNG images
- ⚙️ Config: TOML files
- 📖 Docs: Markdown files

### Importance Levels
- 🔴 Critical: Essential project files
- 🟡 High: Important configuration files
- 🔵 Medium: Helpful but not essential files
