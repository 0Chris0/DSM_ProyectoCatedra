# Proyecto de Cátedra - Desarrollo de Software para Móvil (DSM)
## Universidad Don Bosco — Escuela de Ingeniería en Computación
### Fase 1: Módulo de Autenticación y Navegación Base (15%)

---
### WICHO BURGUER APP
---

## 👥 Integrantes del Grupo

| Integrante | Carné | % de Participación |
| :--- | :---: | :---: |
| Daniel Alexander Girón Cornejo | GC221469 | 100% |
| Christian Augusto Maravilla Melendez | MM250405 | 100% |
| Eduardo Josué Ortiz Orellana | OO172577 | 100% |
| Mariana Maytee López Gómez | LG252169 | 100% |

---

## 📱 Descripción del Proyecto
Este repositorio contiene el desarrollo de la primera fase de la aplicación **Wicho**, correspondiente al módulo de autenticación (**Login**) implementado en **Android Studio** utilizando **Kotlin** y diseño de interfaces en **XML**.

### Requerimientos e Implementaciones:
- [x] **Captura de Credenciales:** Campos para usuario/correo y contraseña con control y validación de datos.
- [x] **Persistencia de Sesión:** Integración de `SessionManager` para recordar el estado del usuario (*Remember me*).
- [x] **Opciones OAuth y Recuperación:** Botones con recursos dedicados para Google, GitHub y Microsoft (`google.png`, `github.png`, `microsoft.png`), junto con enlace para recuperación de clave.
- [x] **Navegación al Menú Principal:** Redirección tras autenticación hacia `MainActivity` (`activity_main.xml`), desplegando el logotipo oficial de la app (`logo_wicho.png`) y el menú base inicial.

---

## 🏗️ Estructura del Código Fuente
* **`LoginActivity.kt` / `activity_login.xml`:** Manejo del ciclo de vida del login, validaciones en pantalla y captura de eventos.
* **`MainActivity.kt` / `activity_main.xml`:** Pantalla principal post-login que aloja el logo de la aplicación (`logo_wicho.png`) y el contenedor del menú.
* **`AuthRepository.kt`:** Capa de datos encargada de la lógica de autenticación y validación de credenciales.
* **`SessionManager.kt`:** Administración de almacenamiento persistente para la sesión activa.

---
