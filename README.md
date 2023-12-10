# StickHero
The package statement declares the package in which the class is located.
The import statements bring in external classes and libraries that are used in the code.
The class Main extends Application, indicating that it is a JavaFX application.
The main method is the entry point of the program. It calls the launch method to start the JavaFX application.
The start method is called when the JavaFX application is launched. It sets up the initial scene and stage.
A Group is a container for nodes in the scene.
It loads a background image and adds it to the group.
It creates the main scene, sets it to the primaryStage, and configures some properties like size and title.
It creates a separate welcome stage and shows it.
This method sets up the welcome stage with a text field, label, and button to get the player's name.
It listens for Enter key press in the text field and simulates a button click.
The button's action event is handled, where it checks if a name is entered, closes the welcome stage, and initiates the game.
The stop method is called when the application is shutting down. It's used here to clean up resources related to the game.

Bonus : We have implemented sound on eating cherries, threads, design patter iterator and observer.


Controls
press F for the stick to fall horizontally
press i to flip on the stick to collect cherries
left mouse button extends stick
right mouse button stick become small
cherries appear randomly
highscore works only for >0
