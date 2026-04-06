# Utilizacion IA (Claude)
Opte por la utilización de la IA para acelerar desarrollo y validar estructura del proyecto.

## Asistente Utilizado
Claude code
model: Sonnet 4.6

## Resumen
* El codigo base del proyecto (estructura de carpetas) lo he creado manualmente
alineado a la estructura de una arquitectura hexagonal, domain, usesCases, port y adapter.
* En la primer revision implementé 3 endpoints: 
  * create (post), 
  * List (get), 
  * Get/id (),
y la Integración con postgres via Spring Data JPA
    * cree un container para Postgresql.
  
* Empece a utilizar claude a partir del codigo base para validar la estructura,
que me ayude a no violar ninguna regla de la arquitectura hexa.
* Claude me sugerió algunos fixes que estaba teniendo en el codigo base como algunas annotation Spring boot en el port 
y restructuracion de la carpeta port dentro del application.
* Luego continue usando la IA para el resto de las funcionalidades:
  * put, delete, casos de errores, proceso automatico.
  * en el archivo prompts.md se detallan los prompts utilizados.
