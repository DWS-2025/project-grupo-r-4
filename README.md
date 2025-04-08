# ToyVerse - Plataforma de Compra y Gestión de Juguetes

## Descripción general

**ToyVerse** es una aplicación web desarrollada en Java con Spring Boot que permite a los usuarios navegar por un catálogo de juguetes, realizar compras, escribir reseñas y gestionar sus cuentas. Está diseñada para ofrecer una experiencia intuitiva tanto para usuarios normales como administradores, permitiendo la interacción con productos mediante imágenes, descripciones y valoraciones.

---

## Integrantes del equipo

| Nombre           | Apellidos             | Correo oficial UGR                | GitHub                |
|------------------|-----------------------|-----------------------------------|------------------------|
| Jorge            | Punzón Chichón        | j.punzon.2022@alumnos.urjc.es     | [@usuario1](https://github.com/usuario1) |
| Hugo             | Margalef Nieto        | h.margalef.2022@alumnos.urjc.es   | [@usuario2](https://github.com/usuario2) |



---

## 🧱 Entidades principales

- **User**: representa a un usuario del sistema.De momento solo existe un usuario "user".
- **Product**: representa un juguete. Incluye nombre, descripción, tipo, precio e imagen.
- **Purchase**: representa una compra realizada por un usuario.
- **Review**: permite a los usuarios dejar comentarios y valoraciones sobre los productos.
- **Image**: representa la imagen del producto al que esta asignado.


### Relaciones entre entidades

- Un **usuario** puede tener **muchas compras**.
- Un **producto** puede tener **muchas reseñas**.
- Un **usuario** puede escribir **muchas reseñas**.

---

## Permisos de los usuarios

| Tipo de usuario | Permisos |
|-----------------|----------|
| **User** | Añadir/modificar/eliminar productos, ver todas las compras . Es dueño de la gestión global. |

---

## 🖼️ Entidades con imágenes

- **Product**: Cada producto tiene asociada una imagen visible en el catálogo y en el detalle del producto.

---

## Diagrama de Entidades

+-----------+            1        *            +---------+
|  Product  | -------------------------------> | Review  |
+-----------+                                   +---------+

+-----------+            *        *            +---------+
|  Product  | <--------------------------------> |  User   |
+-----------+                                   +---------+

+-----------+            *        *            +------------+
|  Product  | <--------------------------------> | Purchase  |
+-----------+                                   +------------+

+---------+              *        1            +-----------+
| Review  | ----------------------------------> | Product   |
+---------+                                    +-----------+

+---------+              *        1            +---------+
| Review  | ----------------------------------> |  User   |
+---------+                                    +---------+

+---------+              1        *            +---------+
|  User   | ----------------------------------> | Review  |
+---------+                                    +---------+

+---------+              1        *            +------------+
|  User   | ----------------------------------> | Purchase  |
+---------+                                    +------------+

+---------+              *        *            +-----------+
|  User   | <---------------------------------> | Product   |
+---------+                                    +-----------+

+------------+           *        *            +-----------+
| Purchase   | <--------------------------------> | Product  |
+------------+                                    +-----------+

+------------+           *        1            +---------+
| Purchase   | --------------------------------> |  User   |
+------------+                                    +---------+

(Se aprecia mejor en la preview)

class User {
  +long id
  +String name
  +String password
  +String address
  +String phone
  +int numReviews
}

class Review {
  +long reviewId
  +int rating
  +String review
}

class Purchase {
  +long id
  +double price
}

class Product {
  +long id
  +String name
  +double price
  +String image
  +String description
  +String type
}

User "1" -- "many" Review : writes
User "1" -- "many" Purchase : makes
User "many" -- "many" Product : favorites
Review "many" -- "1" Product : about
Review "many" -- "1" User : by
Purchase "many" -- "many" Product : contains
Purchase "many" -- "1" User : doneBy

---
## 📊 Desarrollo colaborativo

### Contribuciones personales

#### Hugo Margalef

**Commits más relevantes**:

1. [Creación de los RestController]
   Se crearon los restController con las exigencias pedidas en la práctica.
2. [Inicialización de la base de datos]
   Se inició la base de datos y se consiguió insertar los productos e imágenes en esta.
3. [Visualización de las imágenes]
   Se pudo visualizar las imágenes en la página web.
4. [Cambio de la estructura completa para Dtos]
   Se crearon las entidades y se modificaron los services y controllers para que se usasen Dtos.
5. [Filtrar una entidad por varias de sus variables]
   Los productos se consiguió que se pudiesen filtrar por tipo y por precio.


- #### Nombre2 Apellido2

**Commits más relevantes**:

1. [Crear controlador de compras](https://github.com/usuario1/project-toyverse/commit/abc123)
2. [Redirigir correctamente tras compra](https://github.com/usuario1/project-toyverse/commit/def456)
3. [Manejo de errores en PurchaseService](https://github.com/usuario1/project-toyverse/commit/ghi789)
4. [Formulario HTML de compra](https://github.com/usuario1/project-toyverse/commit/jkl012)
5. [Conversión de ProductDTO a Product](https://github.com/usuario1/project-toyverse/commit/mno345)





