# Change log

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