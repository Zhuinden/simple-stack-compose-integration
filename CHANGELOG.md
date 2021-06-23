# Change log

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