# Evolution Simulator

### Simple simulator made for Object Oriented Programming course (PO) at AGH 2023/24.

<img src="screenshots/simulation_preview.gif">

### Concept
You can find detailed description for this project on [GitHub](https://github.com/Soamid/obiektowe-lab/tree/master/proj)

### Technologies used:

- Java
- JavaFX

## Building and running the Project

The project uses Gradle for building and managing dependencies. To build the project, navigate to the project root directory and run the following command in your terminal:

```sh
./gradlew build
```

To run the simulation, use the following command:

```sh
./gradlew run
```

### Introduction

This simulator is based on simple Darwin's Theory - only strongest organisms can survive. Every day animals move, eat and reproduce. When they run out of energy they die. Every animal has genetype and his own energy level.<br>

### Legend
| Image                                       | Meaning        |
|---------------------------------------------|----------------|
| <p align="center"><img src="screenshots/step.png"></p>  | Step tile |
| <p align="center"><img src="screenshots/jungle.png"></p>  | Jungle tile |
| <p align="center"><img src="screenshots/grass.png"></p>  | Grass |
| <p align="center"><img src="screenshots/healthy_animal.png"></p>  | Healthy animal |
| <p align="center"><img src="screenshots/dying_animal.png"></p>  | Dying animal |
| <p align="center"><img src="screenshots/dominant_genotype.png"></p>  | Dominant genotype |
| <p align="center"><img src="screenshots/tracked_animal.png"></p>  | Tracked animal |

### Before simulation
Simulation takes place on the step-map with either small square jungle somewhere on the map or with jungle all across the equator, depending on the starting config. 
Before simulation user can choose his own options: size of the map, starting number of animals and plants, the amount of energy the animals get at the beginning, the amount of energy the animals lose each move and the amount of energy the animals get after eating a plant, etc.

<img src="screenshots/menu.png">

You can also save your config or load a previously saved one.

<img src="screenshots/save_load.png">

### Statistics
Statistics are updated every day of simulation. <br>
You can track for example:
* number of plants and animals
* dominant (the most common) genotype
* average lifespan, child number and current energy<br>

Additionally, a chart is drawn monitoring the number of animals and plants.

<img src="screenshots/simulation_explanation.png">

### Animals with dominant genotype
After pausing the simulation, all alive animals with dominant genotype will be highlighted.

### Save to CSV file
After clicking on "Save to CSV" all statistics will be saved to CSV file.

<img src="screenshots/saved_stats_location.png">

### Authors
Jakub Konopka<br/>
Mateusz Bobula
