# WorkLink API2

API REST para la gestión de turnos del trabajo, solicitudes y usuarios de una empresa, desarrollada con Java Spring Boot y desplegada con Docker.

---

## Tecnologías

- **Java 21** + **Spring Boot**
- **MariaDB** como base de datos relacional
- **Docker** + **Docker Compose** para el despliegue en contenedores
- **APIMán** como API Gateway
- **JWT** para autenticación y autorización
- **ModelMapper** para el mapeo entre entidades y DTOs
- **GitHub Actions** para CI/CD con Newman (Postman CLI)
- **AWS** para el despliegue en producción

---

## Entidades

- **User** — gestión de usuarios de la empresa
- **Rol** — roles y niveles de acceso
- **Turn** — turnos de trabajo (mañana, tarde, noche, vacaciones...)
- **ApplicationType** — tipos de solicitud (vacaciones, días de exceso, horas, cambios de turno...)
- **TurnAssigned** — asignación de turnos a usuarios
- **Application** — solicitudes de los usuarios
- **UserBalance** — balance de días y horas de cada usuario por año

---

## Perfiles de Spring Boot

| Perfil | Uso |
|--------|-----|
| `dev` | Desarrollo local (MariaDB en Docker en puerto 3307) |
| `docker` | Despliegue local con docker-compose (MariaDB por nombre de contenedor) |
| `ci` | Pipeline de GitHub Actions (MariaDB en puerto estándar 3306) |
| `prod` | Despliegue en AWS |

---

## Despliegue local

### Requisitos
- Docker Desktop instalado y en ejecución
- IntelliJ IDEA o similar

### Pasos

```bash
# Limpiar la base de datos y levantar los contenedores
docker-compose down -v
docker-compose up -d
```

La API arrancará en `http://localhost:8081` y APIMán en `http://gateway.local.gd:8080`.

---

## API Gateway — APIMán

La API está expuesta a través de APIMán como gateway. Todas las peticiones deben incluir la API Key como query param:

```
http://gateway.local.gd:8080/Yitan/WorkLink/3.0/{endpoint}?apikey={KEY}
```

### Políticas configuradas
- **Rate Limiting**: 1000 peticiones por cliente por minuto
- **Quota**: 1000 peticiones por cliente por día

---

## Autenticación

La API usa **JWT**. Para acceder a los endpoints protegidos:

1. Hacer `POST /login` con email y contraseña
2. Usar el token devuelto en el header `Authorization: Bearer {token}`

---

## Versionado de la API — V1 vs V2

Se ha implementado versionado en los endpoints de **Rol** para mejorar la funcionalidad sin romper la compatibilidad con los consumidores existentes que usan V1.

### GET /api/v1/rol vs GET /api/v2/rol

| | V1 | V2 |
|---|---|---|
| Filtro por nombre | Búsqueda exacta (`equals`) | Búsqueda parcial e insensible a mayúsculas (`contains` + `toLowerCase`) |

**Justificación**: En V1 el filtro por nombre requiere conocer el nombre exacto del rol, lo que resulta poco práctico en interfaces de búsqueda. V2 permite búsquedas parciales — por ejemplo `?name=ad` devuelve roles como "Admin" o "Administrador" — mejorando la experiencia del consumidor sin modificar V1.

### POST /api/v1/rol vs POST /api/v2/rol

| | V1 | V2 |
|---|---|---|
| Validación de nombre único | No valida duplicados | Lanza excepción si ya existe un rol con el mismo nombre (`existsByNameIgnoreCase`) |

**Justificación**: V1 permite crear roles con nombres duplicados, lo que puede generar inconsistencias en el sistema. V2 añade una validación de unicidad insensible a mayúsculas antes de persistir, evitando duplicados como "Admin" y "admin" coexistiendo.

### PUT /api/v1/rol/{id} vs PUT /api/v2/rol/{id}

| | V1 | V2 |
|---|---|---|
| DTO de entrada | `RolInDto` (sin campo `active`) | `RolInV2Dto` (incluye campo `active`) |
| Validación de nombre | No valida duplicados | Valida que no exista otro rol con el mismo nombre (excluyendo el propio) |

**Justificación**: V1 no permite modificar el estado `active` del rol ni valida conflictos de nombre. V2 introduce un DTO específico que permite actualizar el estado del rol y añade una comprobación que garantiza que el nuevo nombre no colisione con otros roles existentes, usando `equalsIgnoreCase` para cubrir variaciones de capitalización.

### DELETE /api/v1/rol/{id} vs DELETE /api/v2/rol/{id}

| | V1 | V2 |
|---|---|---|
| Tipo de borrado | Borrado físico (`deleteById`) | Borrado lógico (`setActive(false)`) |
| Respuesta | `204 No Content` (vacío) | `200 OK` con el rol desactivado |

**Justificación**: El borrado físico de V1 elimina el registro permanentemente de la base de datos, lo que puede causar problemas de integridad referencial si hay usuarios u otras entidades relacionadas con ese rol. V2 implementa un **borrado lógico** — el rol se desactiva (`active = false`) pero se mantiene en la base de datos, preservando la integridad de los datos históricos y permitiendo reactivar el rol si fuera necesario.

---

## CI/CD — GitHub Actions

El repositorio incluye un pipeline en `.github/workflows/postman-tests.yml` que se ejecuta automáticamente en cada push o pull request a las ramas `developer`, `main` y `master`.

### Pasos del pipeline

1. Checkout del repositorio
2. Configuración de Java 21 (Temurin)
3. Arranque de MariaDB como servicio
4. Arranque de Spring Boot con perfil `ci`
5. Espera a que la API esté lista (healthcheck)
6. Instalación de Newman (Postman CLI)
7. Ejecución de la colección de Postman completa
8. Muestra el log de Spring Boot si hay algún fallo

### Ejecución manual con Newman

```bash
npm install -g newman
newman run postman/WorkLink.postman_collection.json \
  -e postman/local.postman_environment.json
```

---

## Tests — Postman

La colección incluye tests para todas las entidades cubriendo:

- Códigos de respuesta correctos (200, 201, 204, 400, 403, 404)
- Estructura y tipos de datos de la respuesta
- Validación de valores devueltos
- Protección de rutas sin token (403)
- Casos de error (404 con ID inexistente, 400 con datos inválidos)

Los tests se pueden ejecutar carpeta por carpeta desde el runner de Postman o todos a la vez con la colección completa.