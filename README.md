# SYNTROPY DELTA NEXUS OMNI

**Syntropy Delta Nexus OMNI** es un sistema operativo móvil de gestión de operaciones (ERP) de grado militar y alta disponibilidad, diseñado para operadores que interactúan con ecosistemas descentralizados, tareas DePIN y redes de prueba (testnets) Web3. Este ecosistema está blindado con mitigaciones de ciberseguridad proactivas para garantizar el anonimato del operador, mitigar la detección heurística de bots y persistir operaciones con total estabilidad.

El sistema sigue de forma estricta los principios de **Clean Architecture** y el patrón de presentación reactiva **MVVM (Model-View-ViewModel)** en Android utilizando **Jetpack Compose**, respaldado por una base de datos local **Room (SQLite)** de alta velocidad.

---

## 🛠️ ARQUITECTURA DETALLADA DEL SISTEMA

La aplicación está segmentada de manera modular para garantizar un acoplamiento laxo y facilitar auditorías de código independientes:

```text
Capa de Presentación (UI/ViewModel)  -->  Capa de Dominio (Modelos/Casos de Uso)  <--  Capa de Datos (Room/Network)
```

1. **Capa de Presentación**:
   * **MainActivity & Componentes Bento**: Grid táctico inteligente y adaptativo que optimiza el espacio de pantalla, distribuyendo la telemetría, el centro de control de perfiles y el navegador de forma armónica.
   * **MainViewModel**: Coordina el flujo continuo de automatización y lectura de datos en hilos de fondo mediante Corrutinas de Kotlin y flujos reactivos (`StateFlow`).

2. **Capa de Dominio (Reglas de Negocio Puras)**:
   * **Modelos**: `Task`, `LogEntry`, `RpcNode` y el nuevo modelo `Session` (conmutación multi-perfil).
   * **ResultWrapper**: Contenedor funcional seguro para encapsular operaciones, aislando excepciones en las llamadas de red y persistencia.
   * **Casos de Uso**: Abstracción pura de procesos como `GetTasksUseCase`, `AddTaskUseCase`, `UpdateTaskUseCase`, `DeleteTaskUseCase` y `QueryRpcNodesUseCase`.

3. **Capa de Datos (Infraestructura)**:
   * **Room SQLite**: Persiste de forma segura las colas de objetivos y registros locales de telemetría.
   * **Ghost-Shield Interceptor**: Motor de evasión activa que inyecta de forma dinâmica User-Agents de sistemas reales, enmascara el tráfico simulando enrutamiento proxy Socks5/HTTPS y aplica retardos de mimetización de comportamiento humano ("Human-Like Delay").

---

## 🛡️ ESPECIFICACIONES DEL MÓDULO GHOST-SHIELD

Para evadir auditorías automáticas y bloqueos de red heurísticos (anti-bot) de los proveedores Web3, el módulo **Ghost-Shield** implementa las siguientes contramedidas:

* **Rotación Dinámica de Firmas de Red (User-Agents)**: Cada ciclo de sincronización y solicitud utiliza una cabecera de navegador real diferente (Android, Windows, iOS, Mac).
* **Mánager de Proxies Residenciales**: Interceptor de red que gestiona túneles de forma dinámica y balancea la conexión hacia un proxy de respaldo en caso de latencia crítica o desconexión del nodo principal.
* **Human-Like Delay**: Algoritmo asíncrono que introduce latencias pseudo-aleatorias de entre 1200ms y 3200ms en hilos de fondo para romper la regularidad rítmica del tráfico que normalmente delata a los scripts automatizados.

---

## 📁 ESTRUCTURA DE DIRECTORIOS

El código fuente del sistema está estructurado bajo las directrices estrictas de Clean Architecture:

```text
/app/src/main/java/com/example/
│
├── MainActivity.kt                # UI Bento Grid y Navegador Embebido de Alta Fidelidad
├── MainViewModel.kt               # Orquestador Reactivo y Motor Periódico del Ghost-Shield
│
├── data/                          # CAPA DE DATOS (Infraestructura)
│   ├── local/
│   │   ├── AppDatabase.kt         # Instancia segura de persistencia SQLite (Room)
│   │   ├── TaskEntity.kt          # Esquema local de Objetivos / Quests
│   │   ├── LogEntity.kt           # Esquema local de Registros de Telemetría
│   │   ├── TaskDao.kt             # Métodos de consulta de la cola táctica
│   │   └── LogDao.kt              # Consultas para registros de auditoría
│   ├── network/
│   │   └── GhostShieldInterceptor.kt # Motor de enmascaramiento y evasión heurística
│   └── repository/
│       ├── TaskRepositoryImpl.kt  # Mapeo y persistencia física de tareas
│       └── LogRepositoryImpl.kt   # Mapeo y persistencia física de logs
│
├── domain/                        # CAPA DE DOMINIO (Lógica de Negocio Pura)
│   ├── model/
│   │   ├── Task.kt                # Entidad de negocio para tareas tácticas
│   │   ├── LogEntry.kt            # Modelo de registros de auditoría
│   │   ├── RpcNode.kt             # Modelo de nodo de consulta Web3
│   │   ├── Session.kt             # Manejo de Perfiles (ADMIN vs OPERADOR)
│   │   └── ResultWrapper.kt       # Contenedor seguro de excepciones global
│   ├── repository/
│   │   ├── TaskRepository.kt      # Contratos de persistencia de tareas
│   │   └── LogRepository.kt       # Contratos de persistencia de logs
│   └── usecase/
│       ├── GetTasksUseCase.kt     # Casos de uso desacoplados para control CRUD
│       ├── AddTaskUseCase.kt
│       ├── UpdateTaskUseCase.kt
│       ├── DeleteTaskUseCase.kt
│       ├── GetLogsUseCase.kt
│       ├── AddLogUseCase.kt
│       └── QueryRpcNodesUseCase.kt # Monitoreo de latencia de redes Web3 de solo lectura
│
└── ui/theme/                      # SISTEMA DE DISEÑO (Material 3 Obsidian / Neon Teal)
    ├── Color.kt                   # Paleta visual táctica de alto contraste para visores nocturnos
    ├── Theme.kt                   # Configuración del esquema de color oscuro
    └── Type.kt                    # Escala tipográfica y fuentes monoespaciadas
```

---

## 🚀 MANUAL DE DESPLIEGUE EN ENTORNOS DISTRIBUIDOS (TERMUX)

Si deseas levantar el servidor local de base de datos adicional y sincronización remota dentro del mismo dispositivo Android usando Termux, sigue este procedimiento de despliegue militar:

### 1. Preparación del Sistema Operativo
Instala las dependencias y librerías criptográficas esenciales en Termux:
```bash
pkg update && pkg upgrade -y
pkg install nodejs ts-node sqlite openssl -y
```

### 2. Clonación y Configuración del Backend Local
Descarga la rama oficial y prepara las variables de entorno para el túnel local:
```bash
git clone https://github.com/tu-usuario/syntropy-delta-nexus-omni.git
cd syntropy-delta-nexus-omni/backend
npm install
```

### 3. Ficheros de Configuración de Seguridad (`.env`)
Configura el puerto y la firma criptográfica JWT para evitar infiltraciones:
```env
PORT=3000
DATABASE_URL="file:./omni_secure.db"
JWT_SECRET="syntropy_military_grade_key_2026"
```

### 4. Inicialización de Persistencia Prisma
Ejecuta la migración estructural de la base de datos de respaldo local:
```bash
npx prisma db push
```

### 5. Configuración de PM2 para Alta Disponibilidad 24/7
Garantiza que el servidor continúe en ejecución persistente en segundo plano de manera ininterrumpida:
```bash
npm install -g pm2
pm2 start src/server.ts --interpreter ts-node --name "omni-backend"
pm2 save
pm2 startup
```

---

## ⛓️ INTEGRACIÓN DE CI/CD CON GITHUB ACTIONS

El sistema incluye un pipeline automatizado de integración y despliegue continuo localizado en `.github/workflows/ci-cd.yml` el cual realiza de forma autónoma:

1. **Checkout automático** de la rama activa.
2. **Setup de JDK 17** (Temurin distribution).
3. **Ejecución de Suite de Test Unitarios** en entornos JVM aislados para el cliente Android.
4. **Ensamblado del APK** (`./gradlew assembleDebug`).
5. **Carga automática de artefactos** para descarga y auditoría inmediata del binario compilado.
