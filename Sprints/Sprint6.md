## Sprint planning

En lo que a este sprint respecta queremos trabajar sobre lo siguiente:

- Finalizar lo relacionado con la interfaz gráfica. Para esto, necesitamos terminar con el componente visual "board" y añadir el componente "shipsBar" para facilitar la interacción del jugador con el sistema a la hora de colocar los barcos. Nos es difícil estimar el grado de dificultad y la cantidad de tiempo que nos llevarán estas tareas, pues tenemos dudas importantes con respecto a ellas. Nuestras dudas se basan sobre todo en cómo tenemos que hacer estos componentes para que no existan problemas al implementar la funcionalidad online, pues es importante que estas clases sepan que tableros pueden mostrar ocultos y cuales no, lo cual es diferente para cada ordenador con el que está jugando simultáneamente. Para resolver este problema, hemos concertado una tutoría con nuestro profesor el miércoles a las 09:30 en la que confíamos todos los aspectos mencionados en este punto se nos clarifiquen. Este trabajo está reflejado en las historias de usuario número 24, 20 y 25.  

- Consideramos también que es importante que en este sprint comencemos con la funcionalidad online del juego. También nos es difícil estimar la dificultad y el tiempo necesario que esto conllevará, pues no tenemos ni experiencia ni base teórica en cómo hacer juegos multijugador en red. Hemos pensado que lo más importante es que durante sprint adquiramos los conocimientos esenciales sobre el package java.net y estructuremos una idea de cómo podemos construir todo lo necesario para hacer el juego online, así, en los sprint siguientes podremos trabajar de manera fluida sobre lo que a esta funcionalidad respecta. Este trabajo está reflejado en la historia de usuario número 19, cuya estimación es infinito y que por tanto tras la tutoría debemos dividir. 

- Una vez la interfaz gráfica esté terminada, será necesario implementar un nuevo controlador con los métodos que sean convenientes para poder compilar el juego en modo GUI. Estimamos que esto no nos conllevará mucho tiempo, pues a medida que construimos los componentes visuales vamos quedando indicados que funciones son necesarias y qué deben hacer. Por este mismo motivo creemos que la dificultad de esto tampoco será elevada. Además, una vez este controlador esté terminado podremos compilar el juego en modo GUI, lo cual se considerará un importante avance. 

- Por último, en este sprint también es importante que quedemos terminadas las estrategias automáticas. Realmente, solo queda terminar la del modo difícil, pero estamos teniendo problemas, pues esta estrategia necesita tener información que no puede obtener de forma "usual", pues esto rompería la encapsulación. De todas formas, la estrategia esta pensada y por tanto estimamos que esto tampoco nos conllevará demasiado tiempo o trabajo. También tenemos pensado plantear estas dudas al profesor en la tutotía anteriormente mencionada. Este guión y el anterior están dentro de los aspectos de refactorización y trabajo necesario para la compilación del juego.

En conclusión, somos conscientes de la carga de trabajo que hemos puesto a este sprint, pero hemos decidido hacerlo así para terminar la parte mas "dura" que queda del proyecto, pues la fecha de entrega se va acercando, al igual que la fecha de los exámenes, y es importante que en el último sprint tengamos tiempo para preparar la documentación. 
El principal problema que vemos es que no tenemos información suficiente para estimar de forma exacta el tiempo y trabajo necesario para todo lo que nos hemos popuesto, pero damos por hecho que en la tutoría que hemos pedido todos los aspectos que dificultan esta estimación se aclaren. 


## Sprint review  

Durante este sprint hemos conseguido cumplir lo propuesto a excepción de un pequeño problema relacionado con la modalidad de juego online. A pesar de que ,como comentamos en el sprint planning, la estimación de trabajo y tiempo para estos quince días no había sido muy precisa debido a la falta de información sobre como realizar la parte restante de la interfaz gráfica, hemos conseguido finalizar todo a lo que esta respecta, pues ya se puede jugar una partida completa, con o sin bots y de hasta cuatro jugadores en cualquier nivel de dificultad. También se ha conseguido tener disponibles los comandos save y load desde la interfaz gráfica, algo que no estaba incluido en la planificación pero que hemos decidido realizar para dejar cerrado todo lo que tiene que ver con la interfaz.  

En lo que al juego online respecta, se ha conseguido establecer una red local cliente-servidor a la que puedes conectarse varios jugadores, pero no se ha completado el desarrollo de las funciones que se encargan de que el juego comience en una partida online. 
Sobre las diferentes estrategias automáticas de las que hablamos en el sprint, se han conseguido terminar todas las propuestas y además incorporarlas a la interfaz gráfica para que jugar partidas con bots sea posible.  

Durante este sprint hemos realizado también una refactorización total del controlador, hemos creado una interfaz que implementan los distintos controladores como el de modo consola, modo GUI, el del servidor y el del cliente.

Se han realizado múltiples tests q en JUnit que nos han ayudado a comprobar que la fase de colocación de los barcos, la fase de ataque, la fase de registro de jugadores y las jugadas automáticas funcionan correctamente. También hemos añadido nuevos diagramas de clases e historias de usuarios  y actualizado los anteriores (han sido necesarias varias actualizaciones, pues habíamos refactorizado muchas clases de nuestro proyecto).

En definitiva, todo lo que se había incluido en la planificación ha sido completado, a excepción de la funcionalidad total del juego en modo online. Esto es algo con lo que ya contábamos pues, como se comentó en la planificación, su estimación no fue muy precisa.


## Sprint retrospective

A lo largo de este sprint nos hemos enfrentado a ciertas dificultades, como cierto malestar por enfermedad que ha experimentado algún miembro del grupo. A pesar de ello, consideramos que el trabajo se ha desarrollado satisfactoriamente, pues ha habido una mayor implicación por parte de todos los miembros del grupo debido a que se aproxima la fecha de entrega y los exámenes de otras asignaturas. Esto ha resultado en una mayor eficiencia y un mejor aprovechamiento del tiempo. Esperamos continuar trabajando de la misma forma.

Nos hemos centrado más en el desarrollo del código que en la elaboración de la documentación, pues es cierto que había un gran número de características que el juego aún no tenía y era necesario implementar. Los planes más detallados para elaborar la documentación restante se especificarán en el próximo Sprint planning.

Durante las clases de IS2 restantes trabajaremos en el proyecto de forma conjunta para terminar el desarrollo y especificar detalles para una mayor homogeneidad en el diseño e implementación del juego. Debido a esto, posiblemente reduzcamos el número de sesiones de programación por parejas. Además, teniendo en cuenta la proximidad de los exámenes finales, supone una mayor dificultad tener este tipo de coordinación. 

Sin embargo, continuaremos manteniendo una frecuente comunicación interna, ya que consideramos que es crucial en esta última etapa del proyecto. Realizaremos cuatro Daily Scrum a la semana para asegurarnos de que no decaiga la motivación de los miembros del grupo.

