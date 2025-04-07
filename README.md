# ToyVerse - Plataforma de Compra y Gestión de Juguetes

## Descripción general

**ToyVerse** es una aplicación web desarrollada en Java con Spring Boot que permite a los usuarios navegar por un catálogo de juguetes, realizar compras, escribir reseñas y gestionar sus cuentas. Está diseñada para ofrecer una experiencia intuitiva tanto para usuarios normales como administradores, permitiendo la interacción con productos mediante imágenes, descripciones y valoraciones.

---

## Integrantes del equipo

| Nombre           | Apellidos        | Correo oficial UGR                | GitHub                |
|------------------|------------------|-----------------------------------|------------------------|
| Jorge            | Punzón Chichón   | j.punzon.2022@alumnos.urjc.es     | [@usuario1](https://github.com/usuario1) |
| Nombre2          | Apellido2        | aluXXXXXXX@correo.ugr.es          | [@usuario2](https://github.com/usuario2) |



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

Se incluirá un diagrama en la carpeta `docs/diagrama_entidades.png` que muestre:
- Clases principales (`User`, `Product`, `Purchase`, `Review`)
- Atributos y relaciones entre ellas (uno a muchos, muchos a muchos, etc.)

---



## 🧰 Uso

### Navegación y Compra

- Visualiza el catálogo en `/products/`
- Haz clic en un producto para ver sus detalles
- Pulsa "Comprar" para añadirlo a tus compras

### Reseñas

- Accede al detalle de un producto
- Pulsa "Escribir reseña"
- Escribe tu comentario y selecciona una puntuación

---
## 📊 Desarrollo colaborativo

### Contribuciones personales

#### Nombre1 Apellido1

- Implementación de la lógica de compra (`PurchaseController`)
- Integración de la vista de compra con Spring Boot
- Validaciones de formulario y control de errores HTTP

**Commits más relevantes**:

1. [Crear controlador de compras](https://github.com/usuario1/project-toyverse/commit/abc123)
2. [Redirigir correctamente tras compra](https://github.com/usuario1/project-toyverse/commit/def456)
3. [Manejo de errores en PurchaseService](https://github.com/usuario1/project-toyverse/commit/ghi789)
4. [Formulario HTML de compra](https://github.com/usuario1/project-toyverse/commit/jkl012)
5. [Conversión de ProductDTO a Product](https://github.com/usuario1/project-toyverse/commit/mno345)

**Ficheros más editados**:

- `PurchaseController.java` ([blame](https://github.com/usuario1/project-toyverse/blame/main/src/main/java/.../PurchaseController.java))
- `purchase-form.html`
- `PurchaseService.java`
- `ProductDTO.java`
- `purchase.js`

- #### Nombre2 Apellido2

- Implementación de la lógica de compra (`PurchaseController`)
- Integración de la vista de compra con Spring Boot
- Validaciones de formulario y control de errores HTTP

**Commits más relevantes**:

1. [Crear controlador de compras](https://github.com/usuario1/project-toyverse/commit/abc123)
2. [Redirigir correctamente tras compra](https://github.com/usuario1/project-toyverse/commit/def456)
3. [Manejo de errores en PurchaseService](https://github.com/usuario1/project-toyverse/commit/ghi789)
4. [Formulario HTML de compra](https://github.com/usuario1/project-toyverse/commit/jkl012)
5. [Conversión de ProductDTO a Product](https://github.com/usuario1/project-toyverse/commit/mno345)

**Ficheros más editados**:

- `PurchaseController.java` ([blame](https://github.com/usuario1/project-toyverse/blame/main/src/main/java/.../PurchaseController.java))
- `purchase-form.html`
- `PurchaseService.java`
- `ProductDTO.java`
- `purchase.js`

> Este bloque debe repetirse para cada integrante del equipo.

---

## ✅ Estado del proyecto

- [x] Catálogo de productos
- [x] Proceso de compra funcional
- [x] Sistema de reseñas
- [x] Manejo de imágenes


