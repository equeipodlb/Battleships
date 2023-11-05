## Sprint planning

En este sprint trabajaremos simultaneamente en la interfaz gráfica, el jugador automático, poder guardar y cargar partida, así como en la historia número 18, que se encarga de que dos jugadores no puedan llamarse igual. Para la interfaz gráfica nos centraremos en mostrar un tablero y poder colocar los barcos arrastrandolos por la pantalla hasta el tablero. En el jugador automático tendremos que desarrollar estrategias de colocación de barcos y de ataque. Para guardar y cargar partida nos guiaremos del patron memento.  
Durante este sprint también depuraremos la versión del modo consola que ya tenemos funcional haciéndo la interacción del usuario con esta más intuitiva (añadir si nos encontramos en la fase de colocación de barcos o en la fase de ataque, durante la fase de colocación mostrar la longitud del barco a colocar...)

A sabiendas de que las historias de usuario que hemos decidido desarrollar durante este sprint están estimadas con un valor considerablemente alto, creemos que esto no nos provocará demasiados problemas, pues gran parte del trabajo a realizar ya ha sido previamente pensado para otros proyectos de TP2 y contamos con dicha experiencia y conocimientos esenciales, en los cuales confiamos para agilizar el proceso de desasrrollo de todo lo indicado anteriormente. 

## Sprint review

Durante este sprint, hemos sido capaces de lograr los siguientes objetivos:
  - La funcionalidad de guardar y cargar partida, siendo esta la historia de usuario número 9. Para ello añadimos dos nuevos comandos, y acordamos entre todo el grupo establecer unas condiciones necesarias para poder guardar la partida (haber completado la fase de colocación de todos los jugadores, ya que antes de esto pensamos que no tiene sentido guardar la partida).
  - La historia de usuario número 18 también se completo en este sprint, para ello hubo que hacer cambios y refactorizar el controlador y parte del código. Para comprobar y asegurarnos de la corrección de los cambios introducidos, desarrollamos un test con JUnit (el cuál resultó satisfactorio).
  - Respecto al jugador aútomático se introdujeron nuevas estrategias y 3 nuevas clases, haciendo uso del polimorfismo visto en clase para reutilizar código ya desarrollado. Los jugadores aútomático desarrollan estrategias de distinta dificultad. La última estrategia (Hard) falta implementarla, ya que será necesario refactorizar partes del proyecto (cosa que no quisimos hacer justo antes del fin del sprint).
  - La interfaz gráfica se ha desarrollado en gran parte y se ha avanazado mucho desde que se comenzó el sprint. Se han desarrollado distintos componentes, aunque su funcionalidad no quedará definida hasta que no se desarrolle el nuevo controlador. Concretamente, se han desarrollado componentes visuales para el tablero del juego, la barra de estado,  la barra de jugadores y la ventana de configuración. Pensamos que serán necesarios algunos componentes más, aunque esto se discutirá en el siguiente Sprint planning.


## Sprint retrospective

Durante este sprint no hemos contado con los diagramas UML ni ningún tipo de diseño común y por ello, como propuesta de mejora, queremos aumentar su uso en los próximos Sprints y llevar un diseño cojunto de cada una de las partes.

Por otro lado, la programación por parejas al estilo Driver-Navigator, que nos sirve para evitar distintos conflictos y asegurar la correcta implementación de algunos métodos, nos ha seguido funcionando de manera eficaz y vamos a mantenerla.

Además la comunicación interna entre los distintos miembros del grupo sigue siendo abundante, con sus respectivos Daily Scrums, revisiones y con una buena labor del Scrum Master.

Mantenemos la estructura del Sprint Retrospective según el método StarFish.

También la estimación que realizamos al principio del Sprint, ha sido mucho más realista y acertada que la de los anteriores Sprints y nos ha permitido llevar a cabo el trabajo esperado reflejado en el Sprint Planning.

En lineas generales estamos satisfechos con este Sprint aunque consideramos que nos queda todavía una gran parte de trabajo por desempeñar y varias ideas que materializar.
