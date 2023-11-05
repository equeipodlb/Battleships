# Hundir la Flota
![Hundir la flota](https://coverfiles.alphacoders.com/414/41415.jpg)

Este proyecto es la implementación realizada en Java del tradicional juego Hundir la Flota.
El juego se ha desarrollado en el contexto de la asignatura Ingeniería de Software II, impartida en el segundo curso del Doble Grado de Ingeniería Informática - Matemáticas de la Universidad Complutense de Madrid.

This project is a Java implementation of the classic game Battleships.
The game was developed for the Software Engineering II course (UCM). The main objective of the course was to get acquainted with Agile methodologies such as Scrum while developing a software project in a group of 6 people.

## Tecnologías
El proyecto ha sido desarrollado con las siguientes tecnologías:
* Java versión 1.8.
* JUnit versión 5.7.0.
* Modelado y diseño UML con Modelio versión 4.1.

## Compilación y ejecución
Para compilar y ejecutar el proyecto en Java Eclipse es recomendable seguir los siguientes pasos:

1. Clonar este repositorio, bien sea a través de la herramienta visual de Eclipse o a través de la línea de comandos con
```bash
git clone https://github.com/UCM-FDI-DISIA/2021-is2-dg-coronamap.git
```
2. Importar en Eclipse el proyecto asociado al archivo `HundirLaFlota/.project`.
3. Crear una configuración de ejecución tomando como clase principal el archivo `HundirLaFlota/src/main/Main.java`. Los argumentos disponibles para el programa son los siguientes:

| Argumento | Descripción | Valor por defecto | Tipo |
| --------- | ----------- | ----------------- | ---- |
| help | Muestra un mensaje de ayuda con `-h` o `--help`. | | |
| mode | Selecciona el modo de ejecución del juego con `-m` o `--mode`. Las opciones disponibles son `gui` o `console`. | gui | String |

4. Ejecutar el programa.

Todas las librerías necesarias para la compilación del proyecto se encuentran en el directorio `HundirLaFlota/lib/`, aunque en principio no es necesario realizar ningún cambio en lo que a librerías respecta, pues el archivo `HundirLaFlota/.classpath` contiene toda la configuración necesaria.

**Nota:** para colocar barcos en vertical es necesario utilizar el click derecho al seleccionar el barco. Esta información se habría incluido en la ventana Help, pero por diversas razones no ha sido posible la inclusión de dicha ventana.
