# Expo HAS CHANGED

Read the exact versioned docs at https://docs.expo.dev/versions/v57.0.0/ before writing any code.

# UI design rules

- Avoid `useEffect` unless synchronizing with a true external system (e.g. a subscription, `SplashScreen.hideAsync()`). Reacting to a mutation's outcome, deriving a value for render, or handling a user action belongs in a mutation callback (`onSuccess`/`onError`), an event handler, or a render-time computation — not an effect watching state.
- Keep components small. If a component mixes multiple concerns or repeats a pattern (e.g. the same form field markup twice), extract it into its own component rather than letting the file grow.
