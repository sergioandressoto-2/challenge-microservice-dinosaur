* Actúa como un arquitecto de software senior especializado en Java, Spring Boot y Arquitectura Hexagonal (Ports and Adapters).

Este proyecto es el codebase en Spring Boot que implementa un ABM (Alta, Baja, Modificación) de dinosaurios. El objetivo del proyecto es aplicar correctamente los principios de arquitectura hexagonal.

Quiero que analices la estructura del proyecto y valides si la arquitectura está correctamente implementada.

Por favor revisa los siguientes aspectos:

1. Estructura de paquetes
    - Validar que la separación entre dominio, aplicación e infraestructura sea correcta.
    - Verificar que el dominio no dependa de infraestructura ni frameworks.

2. Dominio
    - Revisar entidades, value objects y lógica de negocio.
    - Verificar si el dominio es independiente de Spring.

3. Puertos (Ports)
    - Identificar los puertos de entrada (inbound) y salida (outbound).
    - Validar si las interfaces están correctamente definidas en la capa de aplicación o dominio.

4. Adaptadores (Adapters)
    - Revisar controladores REST como adaptadores de entrada.
    - Revisar repositorios o integraciones como adaptadores de salida.

5. Casos de uso
    - Analizar si los casos de uso están correctamente implementados como servicios de aplicación.

6. Dependencias
    - Detectar violaciones a la arquitectura hexagonal.
    - Identificar dependencias incorrectas entre capas.

7. Buenas prácticas
    - Sugerir mejoras en organización del código.

* Define dentro del archivo DinosaurRepositoryPort una metodo que se llame existsByName y luego implementa
en el archivo DbAdapter donde devuelva true si existe en la base el name del dinosaur.
Luego invoca al metodo dentro del DinosaurService para validar antes de invocar al metodo save.

* Para los casos de uso controla el domainException en el httpAdapter y devuelve un error entendible  hacia el consumidor de la API.

* implementa el casos de uso put para actualizar dinosaur y el delete para eliminar dinosaur
siguiendo la estructura del proyecto.
Para la actualizacion tene en cuenta la validacion que no se puede actualizar un dinosaur en estado EXTINCT
devuelve un domainException.

* defini una DinosaurNotFoundException y controla la excepcion  en caso del delete y el update devolviendo un codigo 404.

* Define e implementa Unit test con Junit y Assertion en el dominio y los casos de uso usando mockito.
Se requiere un nivel de cobertura alto.

* Define e implementa Test de integracion utilizando @SpringBootTest + Testcontainers para la base de datos Postgres y los enpoints implementados.
* implementa un proceso automatico @schedule que se dispare cada 10 minutos (levantar tiempo por properties) que realice
al llegar a las 24 horas antes del extintionDate los dinosaurs en estado ALIVE deben pasar a estado ENDANGERED y al llegar a la fedha y hora 
del ExtintionDate debe pasar al estado EXTINCT independientemente de su estado anterior.
* quiero dockerizar el proyecto que imagen me recomendas puede ser alguna de bitnami
* agregame dockerfile y docker-compose.yml pero quiero en lugar de los values de las properties en los environment variables creame un .env
  para que lea de ahi
* Creame un service dentro del docker-compose.yml que cree un container a partir de la imagen rabbitmq:alpine y se cree un topico llamado NotificationStatus
* creame el exchange en el broker y el servicio (app) cree la notification.status.queue y el binding con routing key
* Implementa un caso de uso donde en cada actualizacion del status del dinosaur se deje un mensaje la cola notification.status.queue
con el formato json siguiente 
{
   "dinosaurId": 1,
   "newStatus": "ENDANGERED",
   "timestamp": "2023-10-01T09:00:00"
}
