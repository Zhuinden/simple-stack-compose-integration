# Change log

-Simple Stack Compose Integration X.XX.X (XXXX-XX-XX)
--------------------------------

- ADDED NEW MAJOR FEATURE (added by @matejdro): Support for `Backstack` managed by Compose.

This allows `Backstack` to be created at any arbitrary nesting level within composables, including nested stacks.

```kotlin
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

Nesting it like so

```kotlin
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            propagateMinConstraints = true
        ) {
            ComposeNavigator(id = "TOP", interceptBackButton = false) {
                createBackstack(
                    History.of(FirstNestedKey()),
                    scopedServices = DefaultServiceProvider()
                )
            }
        }
```

and

```kotlin
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            propagateMinConstraints = true
        ) {
            ComposeNavigator(id = "BOTTOM", interceptBackButton = false) {
                createBackstack(
                    History.of(FirstNestedKey()),
                    scopedServices = DefaultServiceProvider()
                )
            }
        }
```

-Simple Stack Compose Integration 0.11.0 (2023-03-31)
--------------------------------

- UPDATE: simple-stack to 2.7.0, simple-stack-extensions 2.3.0.

-Simple Stack Compose Integration 0.10.0 (2023-02-23)
--------------------------------

- BREAKING CHANGE (recommended by @matejdro): `DefaultComposeKey.RenderComposable()` no longer receives a `Modifier`. This parameter was completely pointless, and a possible source of bugs.
  
- BREAKING CHANGE (recommended by @matejdro): The signature of `AnimationConfiguration` and specifically `ComposableTransition` has changed, and no longer receives `fullWidth` and `fullHeight`. This info can be accessed using `Modifier.drawWithContent {}` and is readily available. Also, `ComposableTransition` now receives `animationProgress` as a `State<Float>`, and not a `Float`. Using `Float` directly results in excessive recompositions during animation, and is bad for performance, therefore this value must be passed lazily. This is effectively a fix for a long-lasting design issue in how animation progress had been handled, created before recomposition-related best practices on deferred read were documented.
  
- CHANGE (required for new features): `core` now has an `api` dependency on `androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1` to support `LocalLifecycleOwner`/`LocalViewModelStoreOwner` per screen.

- NEW FEATURE (added by @matejdro): `core` now supports creating a `LocalLifecycleOwner`/`LocalViewModelStoreOwner` per screen, this change is automatically applied by updating. Please note that this means from now on, AndroidX components scope themselves to the screen, and not the Activity. If you relied on the nearest `LocalViewModelStoreOwner` to be the `Activity`, then this is no longer the case (although in most cases, using Activity-scoped ViewModels for parameter passing typically results in stale state not being reset, and is a common source of bugs.)

- NEW FEATURE: `DefaultComposeKey` now has a `open val modifier: Modifier = Modifier` which is passed to the `ScreenComposable`.

- UPDATE: compileSdk 33.

- UPDATE: simple-stack to 2.6.5, simple-stack-extensions 2.2.5.

- UPDATE: Kotlin to 1.8.10.

- UPDATE: Compose library versions to BOM 2023.01.00.
- 
- UPDATE: Compose Compiler to 1.4.3.


-Simple Stack Compose Integration 0.9.5 (2022-04-21)
--------------------------------

- UPDATE: simple-stack to 2.6.4.

- UPDATE: Compose to 1.1.1.

- UPDATE: Kotlin to 1.6.10.

- Moved to `maven-publish`, ensure that sources-jar gets added.


-Simple Stack Compose Integration 0.9.4 (2021-10-21)
--------------------------------

- UPDATE: Compose to 1.0.4.

- UPDATE: Kotlin to 1.5.31.

-Simple Stack Compose Integration 0.9.3 (2021-08-30)
--------------------------------

- UPDATE: Compose to 1.0.3.

- UPDATE: Kotlin to 1.5.30.

-Simple Stack Compose Integration 0.9.1 (2021-08-10)
--------------------------------

- UPDATE: Compose to 1.0.1.

- UPDATE: Kotlin to 1.5.21.

(Kotlin version is still 1.5.10 as expected by 1.0.0)

-Simple Stack Compose Integration 0.9.0 (2021-07-29)
--------------------------------

- UPDATE: Compose to 1.0.0.

(Kotlin version is still 1.5.10 as expected by 1.0.0)

-Simple Stack Compose Integration 0.5.1 (2021-07-28)
--------------------------------

- UPDATE: Compose to 1.0.0-rc02.

(Kotlin version is still 1.5.10 as expected by 1.0.0-rc02)

-Simple Stack Compose Integration 0.5.0 (2021-07-06)
--------------------------------

- BREAKING CHANGE: Separate `AnimationSpec` from the global transition definition. This allows for different animation specs for different screen transitions.

- BREAKING CHANGE: Kill `AnimationConfiguration.CustomComposableTransitions`. The two `ComposableTransition`s are now top-level property of `AnimationConfiguration` along with `ComposableAnimationSpec`.

- ADD: `AnimationConfiguration.ComposableContentWrapper`, which is a block around the animated content that can be customized.

- UPDATE: Compose to 1.0.0-rc01.

-Simple Stack Compose Integration 0.4.3 (2021-06-23)
--------------------------------

- INTERNAL/FIX: Change usage of `LaunchedEffect` to `DisposableEffect` in ComposeStateChanger. This should potentially fix the elusive issue #7.

-Simple Stack Compose Integration 0.4.2 (2021-06-23)
--------------------------------

- No significant changes.

- Update Compose to 1.0.0-beta09.

-Simple Stack Compose Integration 0.4.1 (2021-06-07)
--------------------------------

- No significant changes.

- Update simple-stack to 2.6.2.

-Simple Stack Compose Integration 0.4.0 (2021-06-04)
--------------------------------

- Update Compose to 1.0.0-beta08.

- Update Kotlin to 1.5.10.

-Simple Stack Compose Integration 0.3.1 (2021-05-19)
--------------------------------

- Update Compose to 1.0.0-beta07.

-Simple Stack Compose Integration 0.3.0 (2021-05-06)
--------------------------------
- Remove `SimpleComposeStateChanger` because new Compose version killed it for some reason.

- Renamed `AnimatingComposeStateChanger` to `ComposeStateChanger`. It is used with `AsyncStateChanger`.

- Update Jetpack Compose to beta06.

- Update Simple-Stack to 2.6.1.

- Update Simple-Stack Extensions to 2.2.1.

-Simple Stack Compose Integration 0.2.0 (2021-03-08)
--------------------------------
- Actually fix Saver :)

-Simple Stack Compose Integration 0.1.3 (2021-03-08)
--------------------------------

- API CHANGE: `DefaultComposeKey` now requires a `saveableStateProviderKey` that is `Any`. The examples return `this` because keys are already immutable and Parcelable (and data class).

Note: Saver should work, but it does not seem to work yet.

-Simple Stack Compose Integration 0.1.2 (2021-03-04)
--------------------------------

- API CHANGE: simplified transition configuration. Now it's a single interface (`ComposableTransition`).

- FIX: Flickering on navigation. (However, CoilImage still flickers.)

Note: Saver still does not work yet.

-Simple Stack Compose Integration 0.1.1 (2021-03-03)
--------------------------------
- API CHANGE: `ComposeStateChanger` -> `AnimatingComposeStateChanger`, must be wrapped as `AsyncStateChanger`.

Please note that it is still flickering and you probably don't want to use it yet.

- ADD: `SimpleComposeStateChanger`, must be wrapped as `SimpleStateChanger`, without animations.

- API CHANGE: Add `fullHeight` to `AnimatingComposeStateChanger`'s transition configuration, because it was missing.

- ADD: `simple-stack-compose-dog-example`.

-Simple Stack Compose Integration 0.1.0 (2021-03-02)
--------------------------------
- Initial release (built against simple-stack 2.5.0, simple-stack-extensions 2.1.0, Compose 1.0.0-beta01).

- Known issue: on forward -> back -> forward animation, the new key's composable seems to flicker in at the start of animation for some reason.