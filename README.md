# Price Tracker App 📈

A modern, real-time Android application built with **Clean Architecture**, **Multi-module** structure, and **Jetpack Compose**. This app displays a paginated list of stocks and provides real-time price updates via a simulated WebSocket connection.

## 🚀 Features

- **Multi-Module Architecture**: Highly decoupled structure for scalability and faster build times.
- **Real-time Updates**: Simulated live pricing using OkHttp WebSockets and an Echo server.
- **Infinite Scrolling**: Efficient list handling using the **Paging 3** library.
- **Type-Safe Navigation**: Secure navigation between screens using Kotlin Serialization.
- **Reactive UI**: State-driven UI built entirely with Jetpack Compose.
- **Robust Testing**: Comprehensive unit tests for ViewModels using MockK and Turbine.

---

## 🛠 Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/)
- **Real-time**: [WebSockets](https://square.github.io/okhttp/4.x/okhttp/okhttp3/-web-socket/) (Postman Echo)
- **Pagination**: [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-p0)
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
- **Serialization**: [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- **Testing**: [JUnit](https://junit.org/junit4/), [MockK](https://mockk.io/), [Turbine](https://github.com/cashapp/turbine)

---

## 🏗 Architecture

The project follows **Clean Architecture** and the **SOLID** principles to ensure the code is maintainable and testable.

### Module Breakdown:
*   **`:domain`**: The core "Boss" module. Contains Business Models and Repository Interfaces. No Android dependencies.
*   **`:data`**: The "Worker" module. Implements Repository interfaces, handles Mappers, and manages Paging sources.
*   **`:network`**: Handles REST API configurations and Retrofit services.
*   **`:socket`**: Manages the WebSocket client, simulation logic, and real-time data streams.
*   **`:home`**: Feature module containing the Explore/Stock List UI and ViewModels.
*   **`:app`**: The **Composition Root**. Glues all modules together, manages top-level navigation, and Hilt injection.

### Dependency Flow:
`UI (:home)` → `Domain (:domain)` ← `Data (:data)` → `Network/Socket (:network, :socket)`

---

## 🧪 Testing

The app emphasizes logic verification through unit tests. We use **StandardTestDispatcher** to precisely control coroutine timing.

- **ViewModel Tests**: Verify state transitions and dependency interactions.
- **Flow Tests**: Use **Turbine** to validate asynchronous data streams from the Socket service.
