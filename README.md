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

and then, add the dependency to your module's `build.gradle.kts` (or `build.gradle`):

``` kotlin
// build.gradle.kts
implementation("com.github.Zhuinden.simple-stack-compose-integration:0.2.0")
```

or

``` groovy
// build.gradle
implementation 'com.github.Zhuinden.simple-stack-compose-integration:0.2.0'
```

As Compose requires Java-8 bytecode, you need to also add this:

``` groovy
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
kotlinOptions {
    jvmTarget = '1.8'
    useIR = true
}
buildFeatures {
    compose true
}
```

## What does it do?

Provides defaults for Composable-driven navigation and animation support.

``` kotlin
class MainActivity : AppCompatActivity() {
    private val composeStateChanger = AnimatingComposeStateChanger() // <--

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val backstack = Navigator.configure()
            .setStateChanger(AsyncStateChanger(composeStateChanger))  // <--
            .install(this, androidContentFrame, History.of(FirstKey()))

        setContent {
            BackstackProvider(backstack) {  // <--
                MaterialTheme {
                    Box(Modifier.fillMaxSize()) {
                        composeStateChanger.RenderScreen()  // <--
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
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
data class SecondKey(private val noArgsPlaceholder: String = ""): ComposeKey() {
    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        SecondScreen(modifier)
    }
}
```

## What about ViewModels?

You can use `ScopedServices` for that.

``` kotlin
abstract class ComposeKey : DefaultComposeKey(), Parcelable, DefaultServiceProvider.HasServices {
    override val saveableStateProviderKey: Any = this

    override fun getScopeTag(): String = javaClass.name

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
data class DogListKey(private val noArgsPlaceholder: String = "") : ComposeKey() {
    override fun bindServices(serviceBinder: ServiceBinder) {
        super.bindServices(serviceBinder)
        with(serviceBinder) {
            add(DogListViewModel(lookup<DogDataSource>(), backstack))
        }
    }

    @Composable
    override fun ScreenComposable(modifier: Modifier) {
        val viewModel = rememberService<DogListViewModel>()

        val dogs by viewModel.dogList.subscribeAsState(OptionalWrapper.absent())

        DogListScreen(dogs.value)
    }
```

## License

    Copyright 2021 Gabor Varadi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
