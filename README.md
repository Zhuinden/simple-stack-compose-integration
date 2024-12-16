# Simple Stack Compose Integration

Default behavior for Jetpack Compose using Simple-Stack.

## Using Simple Stack Compose Integration

In order to use Simple Stack Compose Integration, you need to add `jitpack` to your project root `build.gradle.kts`
(or `build.gradle`):

``` kotlin
// build.gradle.kts
allprojects {
    repositories {
        // ...
        maven { setUrl("https://jitpack.io") }
    }
    // ...
}
```

or

``` groovy
// build.gradle
allprojects {
    repositories {
        // ...
        maven { url "https://jitpack.io" }
    }
    // ...
}
```

In newer projects, you need to also update the `settings.gradle` file's `dependencyResolutionManagement` block:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }  // <--
        jcenter() // Warning: this repository is going to shut down soon
    }
}
```


and then, add the dependency to your module's `build.gradle.kts` (or `build.gradle`):

``` kotlin
// build.gradle.kts
implementation("com.github.Zhuinden:simple-stack-compose-integration:0.13.0")
```

or

``` groovy
// build.gradle
implementation 'com.github.Zhuinden:simple-stack-compose-integration:0.13.0'
```

As Compose requires Java-8 bytecode, you need to also add this:

``` groovy
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
        languageVersion = '1.9'
    }
    buildFeatures {
        compose true
    }
    composeOptions {
        kotlinCompilerExtensionVersion '1.4.3'
    }
}

kotlin.sourceSets.all {
    languageSettings.enableLanguageFeature("DataObjects")
}
```

## What does it do?

Provides defaults for Composable-driven navigation and animation support.

``` kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeNavigator {
                createBackstack(
                    History.of(InitialKey()),
                    scopedServices = DefaultServiceProvider()
                )
            }
        }
    }
}
```

and

``` kotlin
abstract class ComposeKey: DefaultComposeKey(), Parcelable {
    override val saveableStateProviderKey: Any = this // data class + parcelable!
}
```

and

``` kotlin
@Immutable
@Parcelize
data object SecondKey: ComposeKey() {
    operator fun invoke() = this
    
    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        SecondScreen(modifier)
    }
}
```

## What about ViewModels?

While Jetpack ViewModels are also supported, but it is recommended to use `ScopedServices` as provided by **Simple-Stack**, because `ScopedServices` have more powerful feature set than `ViewModel`.

``` kotlin
abstract class ComposeKey : DefaultComposeKey(), Parcelable, DefaultServiceProvider.HasServices {
    override val saveableStateProviderKey: Any = this

    override fun getScopeTag(): String = toString()

    override fun bindServices(serviceBinder: ServiceBinder) {
    }
}
```

and

``` kotlin
val backstack = Navigator.configure()
                    .setScopedServices(DefaultServiceProvider())
                    // ...
```

and

``` kotlin
@Immutable
@Parcelize
data object DogListKey: ComposeKey() {
    operator fun invoke() = this
    
    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(DogListViewModel(lookup<DogDataSource>(), backstack)) // <--
        }
    }

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        val viewModel = remember { backstack.lookup<DogListViewModel>() } // <--

        val dogs by viewModel.dogList.observeAsState()

        DogListScreen(dogs)
    }
}
```

## Note about using Enum parameters in keys

Unfortunately, `enum.hashCode()` is not stable across process death. so Enum classes shouldn't be passed directly to keys as arguments.

It is preferable to preserve them as a private String, and expose the value as an enum vie a custom getter.

```kotlin
// THIS BREAKS!
// data class DemoKey(val enum: DemoEnum): DefaultComposeKey // <-- breaks!

// DO THIS INSTEAD
data class DemoKey(private val enumName: String): DefaultComposeKey {
    constructor(enum: DemoEnum): this(enum.name)
    
    val enum: DemoEnum get() = DemoEnum.valueOf(enumName)
}
```

Unfortunately, this is a limitation of the JVM, and not of Simple-Stack, meaning it's something we need to remember to do.

## License

    Copyright 2021-2024 Gabor Varadi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
