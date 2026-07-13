# SYNTROPY DELTA NEXUS

**Syntropy Delta Nexus** es una plataforma móvil de alto rendimiento diseñada para la gestión operativa en ecosistemas descentralizados, redes Web3, tareas DePIN y monitoreo automatizado de testnets. El sistema está estructurado bajo los principios de **Clean Architecture** (Arquitectura Limpia) con el patrón de presentación **MVVM (Model-View-ViewModel)** en Android utilizando **Jetpack Compose** y base de datos local **Room** (SQLite encriptado).

---

## 🛠️ ARQUITECTURA DEL SISTEMA

La aplicación está completamente modularizada en tres capas bien definidas:

### 1. Capa de Presentación (Presentation)
*   **SyntropyDeltaNexusApp & MainActivity**: Implementa un diseño táctico tipo **Bento Grid** con Material Design 3. Posee un tema oscuro adaptado para operadoras y arquitectos de red.
*   **MainViewModel**: Coordina el estado de la aplicación exponiendo flujos reactivos (`StateFlow`). Gestiona un ciclo continuo de automatización y lectura de datos que actualiza la telemetría cada 6 segundos de manera asíncrona mediante corrutinas de Kotlin.

### 2. Capa de Dominio (Domain)
*   **Modelos de Datos Puros**: `Task` (Tareas/Quests), `LogEntry` (Logs técnicos) y `RpcNode` (Telemetría de Redes).
*   **Casos de Uso (Use Cases)**:
    *   `GetTasksUseCase` y `AddTaskUseCase`: Gestión local de la cola de trabajo.
    *   `GetLogsUseCase` y `AddLogUseCase`: Reporte de telemetría directo al terminal integrado.
    *   `QueryRpcNodesUseCase`: Orquestador de solicitudes RPC de solo lectura con rotación simulada de firmas de red y User-Agents para evitar bloqueos por bots.

### 3. Capa de Datos (Data)
*   **Room Database**: Base de datos SQLite local (`syntropy_nexus_database`) que persiste de manera segura las colas de tareas y logs técnicos.
*   **Repository Implementations**: Mapeo completo entre las entidades de base de datos (`TaskEntity`, `LogEntity`) y los modelos de negocio del dominio para evitar acoplamientos rígidos.

---

## 📂 ESTRUCTURA DE DIRECTORIOS

El código fuente está organizado de la siguiente manera:

```text
/app/src/main/java/com/example/
│
├── MainActivity.kt                # Controlador de interfaz principal (Bento Grid)
├── MainViewModel.kt               # Orquestador reactivo de casos de uso y telemetría
│
├── data/                          # CAPA DE DATOS
│   ├── local/
│   │   ├── AppDatabase.kt         # Instancia segura de base de datos Room
│   │   ├── TaskEntity.kt          # Esquema local de Tareas
│   │   ├── LogEntity.kt           # Esquema local de Logs
│   │   ├── TaskDao.kt             # Consultas SQL para tareas
│   │   └── LogDao.kt              # Consultas SQL para logs
│   └── repository/
│       ├── TaskRepositoryImpl.kt  # Implementación de persistencia de tareas
│       └── LogRepositoryImpl.kt   # Implementación de persistencia de logs
│
├── domain/                        # CAPA DE DOMINIO (Reglas de Negocio)
│   ├── model/
│   │   ├── Task.kt                # Modelo puro de tarea
│   │   ├── LogEntry.kt            # Modelo puro de entrada de log
│   │   └── RpcNode.kt             # Modelo puro de nodo RPC Web3
│   ├── repository/
│   │   ├── TaskRepository.kt      # Contrato de datos de tareas
│   │   └── LogRepository.kt       # Contrato de datos de logs
│   └── usecase/
│       ├── GetTasksUseCase.kt
│       ├── AddTaskUseCase.kt
│       ├── UpdateTaskUseCase.kt
│       ├── DeleteTaskUseCase.kt
│       ├── GetLogsUseCase.kt
│       ├── AddLogUseCase.kt
│       └── QueryRpcNodesUseCase.kt # Monitoreo de redes RPC Web3 de solo lectura
│
└── ui/theme/                      # SISTEMA DE DISEÑO (Material 3)
    ├── Color.kt                   # Paleta táctica (Obsidian, SlateGray, NeonTeal)
    ├── Theme.kt                   # Configuración del esquema oscuro
    └── Type.kt                    # Tipografías y escala de texto
```

---

## ⚡ MÓDULOS INCLUIDOS Y DISEÑO BENTO

La interfaz gráfica se organiza en dos vistas operativas sumamente optimizadas:

### 🖥️ Vista "DEV" (Modo Arquitecto)
*   **Telemetría de Recursos**: Gráficos interactivos de barra lineal que representan el consumo asíncrono de RAM y CPU en tiempo real.
*   **Estado de la Base de Datos**: Inspección en tiempo real de los registros persistidos en SQLite encriptado.
*   **Monitoreo de Nodos RPC**: Latencia medida en milisegundos y altura del bloque actual de redes de prueba (Ethereum Sepolia, Arbitrum Sepolia, Optimism Sepolia, Base Sepolia) con banderas de color dinámicas según la velocidad de la red.
*   **Terminal de Logs**: Visor integrado autoscrollable para rastrear cada paso de automatización completado de manera autónoma.

### 👩‍💻 Vista "OPERATOR" (Modo María)
*   **Gestor de Perfil**: Edición local del nombre de usuario y rango asignado en la consola Nexus.
*   **Cola de Tareas (CRUD)**: Creación instantánea de objetivos (Quests, DePIN, Testnets) con categorización dinámica por etiquetas de color tácticas, posibilidad de marcarlas como completadas o purgarlas.
*   **Explorador Web3 Integrado**: Un componente WebView embebido que cuenta con controles de navegación estándar y accesos directos a exploradores oficiales para interactuar directamente sin salir del panel.

---

## 🚀 INSTRUCCIONES DE DESPLIEGUE EN TERMUX (ANDROID)

Si deseas ejecutar un backend local de Node.js adicional dentro del entorno de Android (Termux) que interactúe con el cliente, sigue estos pasos:

1.  **Instalar dependencias necesarias en Termux**:
    ```bash
    pkg update && pkg upgrade
    pkg install nodejs ts-node sqlite openssl -y
    ```

2.  **Clonar el repositorio y configurar el backend**:
    ```bash
    git clone https://github.com/tu-usuario/syntropy-delta-nexus.git
    cd syntropy-delta-nexus/backend
    npm install
    ```

3.  **Configurar Variables de Entorno**:
    Crea un archivo `.env` con las credenciales necesarias:
    ```env
    PORT=3000
    DATABASE_URL="file:./dev.db"
    JWT_SECRET="syntropy_security_key_2026"
    ```

4.  **Iniciar Base de Datos local con Prisma**:
    ```bash
    npx prisma db push
    ```

5.  **Correr el Servidor mediante PM2 para estabilidad 24/7**:
    ```bash
    npm install -g pm2
    pm2 start src/server.ts --interpreter ts-node --name "delta-nexus-backend"
    pm2 save
    pm2 startup
    ```

El backend local estará listo y expuesto en `http://localhost:3000` listo para procesar colas de trabajo o responder a integraciones con el cliente móvil de Android.
