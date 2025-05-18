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
- **Cart**: representa la cesta en la que se podran comprar los productos.


### Relaciones entre entidades

1.User ↔ Review
Un usuario puede escribir muchas reseñas.

2.User ↔ Purchase
Un usuario puede realizar muchas compras.

3.User ↔ Product
Un usuario puede estar asociado a muchos productos, y un producto puede estar asociado a muchos usuarios.
(Relación muchos a muchos mediante la tabla products_users)

4.Product ↔ Review
Un producto puede tener muchas reseñas.

5.Product ↔ Purchase
Un producto puede estar en muchas compras, y una compra puede contener muchos productos.
(Relación muchos a muchos mediante la tabla purchase_products)

6.Purchase ↔ User
Cada compra pertenece a un único usuario.

7.Review ↔ User
Cada reseña es escrita por un único usuario.

8.Review ↔ Product
Cada reseña pertenece a un único producto.

9.User ↔ Cart
Un usuario tiene un carrito asociado.
(Relación uno a uno)

10.Cart ↔ Product
Un carrito puede contener muchos productos, y un producto puede estar en muchos carritos.
(Relación muchos a muchos mediante la tabla cart_product)




---

## Permisos de los usuarios

| Tipo de usuario | Permisos |
|-----------------|----------|
| **Admin** | Añadir/modificar/eliminar productos, ver todas las compras . Es dueño de la gestión global. |
| **User** | Comprar/añadir reseñas/añadir a la cesta y todo poder sobre su perfil |

---

## 🖼️ Entidades con imágenes

- **Product**: Cada producto tiene asociada una imagen visible en el catálogo y en el detalle del producto.

---

## Diagrama de Entidades

![Imagen](https://github.com/user-attachments/assets/871c24a0-c991-44d4-ac3b-550b43687900)



---
## 📊 Desarrollo colaborativo

### Contribuciones personales

#### Hugo Margalef

**Commits más relevantes**:

1. [Actualizacion productRestontroller]
   Se actualizaron los metodos con respecto a la fase anterior.
2. [Texto enriquecido]
   Adición del texto enriquecido.
3. [Implementacion de la entidad Cart]
   Creacion de cart y comienzo de su uso.
4. [Correción relación NM]
   Correcto funcionamiento de la relación.
5. [Seguridad en BBDD]
   Implementación de seguridad para la Base de datos.


- #### Jorge Punzón

**Commits más relevantes**:

1. [Actualización de los userRestController]
   Se actualizaron los metodos de user con respecto a la fase anterior.
2. [Obtencion de HTTPS]
   Certificado para HTTPS.
3. [Creacion del fichero]
   Adición del fichero para descargar.
4. [Implementación de el borrado de reseña]
   Borrado de reseña completado.
5. [Compras individuales y carrito]
   Funcionamiento completo de compras tanto individuales como con la cesta.





