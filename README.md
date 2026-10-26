# Pinata UI Kotlin Example App

This small example project demonstrates the usage of the Piñata Android SDK
([pinata-ui-kotlin](https://github.com/pinata-ai/pinata-ui-kotlin)). It shows how to integrate
Piñata's marketplace components into a native Android app, including the authentication flow and
the navigation driven by the components' events.

> ⚠️ **Security Warning**: For the ease of this example, company secrets are read from
> `local.properties`. In a production app, the company client ID, key or secret must never ship in
> the app. These credentials should be kept secure and only used in a secure backend service.

## Features

- Integration with the Piñata marketplace components: Wallet, Preview and Full Shopping
- Authentication flow with company and user tokens
- Wallet and Preview open Full Shopping on the clicked item through `MarketplaceOpenEvent`

## Prerequisites

- Android Studio, or JDK 17 and the Android SDK with platform 36
- An Android device or emulator (Android 7.0 or higher)

## Installation

1. Clone the repository:

```bash
git clone https://github.com/pinata-ai/pinata-ui-kotlin-example-app.git
cd pinata-ui-kotlin-example-app
```

2. Add your sandbox values to `local.properties` in the root directory (the file is not committed):

```properties
pinata.clientId=your_client_id
pinata.clientKey=your_client_key
pinata.clientSecret=your_client_secret
pinata.userEmail=your_email@example.com
```

### Properties

- `pinata.baseUrl`: The Partner API base URL (defaults to `https://sandbox.pinata.ai/partners/api/v1/`)
- `pinata.clientId`: Your Piñata client ID
- `pinata.clientKey`: Your Piñata client key
- `pinata.clientSecret`: Your Piñata client secret
- `pinata.userEmail`: Default user email for login

They only prefill the login form; you can also type the values in the app. The components run
against the sandbox environment.

## Running the App

Open the project in Android Studio and run the `app` configuration, or:

```bash
./gradlew :app:installDebug
```

## How it works

- **Login** (`LoginActivity`, `PinataService`): gets a company token from `auth/retrieve-token/`
  (signed with the client secret), then the user's token from `auth/retrieve-user-token/`. The
  user token is the `userToken` of every component.
- **Screens** (`MainActivity`): one screen per component, picked from the drawer. Wallet and
  Preview send a `MarketplaceOpenEvent` when the renter wants the marketplace, and the app opens
  Full Shopping, on the clicked item when the event carries one. Back returns to the previous
  screen.

The SDK comes from the Piñata Maven repository; see `settings.gradle.kts` and the
[SDK README](https://github.com/pinata-ai/pinata-ui-kotlin#installation).
