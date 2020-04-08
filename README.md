# Ktor Client - OAuth Feature

A Kotlin Multiplatform library for automatically handling OAuth refreshes with Ktor 

## Usage

Just install the feature in your `HttpClient` and tell it how to: 

1. Get the access token
    - Get it from your cache or somewhere where you can update the token
2. How to refresh the token
    - This function should get your new token update the token which `getToken` is using 

```kotlin
HttpClient(yourEngine) {
    ...
    install(OAuthFeature) {
        getToken = { ... }
        refreshToken = { ... }
    }
}
```

This would add an `Authorization: Bearer ${getToken()}` header to your requests and would call `refreshToken()` when the request receives a `401 Unauthorized`. 


## Installation

Check the table below for the compatibilty across Kotlin versions

| Library    | Kotlin  |
| ---------- | ------- |
| 0.1.0      | 1.3.70  |

Add the jcenter repository on your Project-level gradle
```kotlin
allprojects {
    repositories {
        ...
        jcenter()
    }
}
```

On the module-level, add the library as a dependency

```kotlin
kotlin {
    ...
    sourceSets["commonMain"].dependencies {
        implementation("com.kuuuurt:ktor-client-oauth-feature:0.1.0")
    }
}
```

This uses Gradle Module Metadata so enable it in your `settings.gradle` file

```kotlin
enableFeaturePreview("GRADLE_METADATA")
```

## Maintainers

- Kurt Renzo Acosta - [kurt.r.acosta@gmail.com](mailto:kurt.r.acosta@gmail.com)

## Contributing

Feel free to dive in! [Open an issue](https://github.com/kuuuurt/ktor-client-oauth-feature/issues) or submit PRs.

## License

[Apache-2.0](LICENSE) © Kurt Renzo Acosta
