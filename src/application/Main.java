package application;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Main extends Application {

	public enum Direction{
		UP, DOWN, LEFT, RIGHT
	}


	//Size of the block
	public static final int blockSize = 40;

	//Size of the application itself
	public static final int appWidth =  20 * blockSize;
	public static final int appHeight = 15 * blockSize;

	//Default direction
	private Direction direction = Direction.RIGHT;
	//Used to ensure that snake doesn't move in two directions at the same time
	private boolean moved = false;
	//If the application is running
	private boolean running = false;
	//Animation for the snake
	private Timeline timeline = new Timeline();
	//List of nodes that can be displayed
	private ObservableList<Node> snake;

	/**
	 * @return
	 */
	private Parent createContent() {
		Pane root = new Pane();
		root.setPrefSize(appWidth, appHeight);

		Group snakeBody = new Group();
		snake = snakeBody.getChildren();

		//Creating food for the snake
		Rectangle food = new Rectangle (blockSize, blockSize);
		food.setFill(Color.BLUE);
		food.setTranslateX((int)(Math.random() * (appWidth-blockSize)) / blockSize * blockSize);
		food.setTranslateY((int)(Math.random() * (appHeight-blockSize)) / blockSize * blockSize);

		KeyFrame frame = new KeyFrame(Duration.seconds(0.1), event -> {
			if(!running)
				return;

			boolean toRemove = snake.size() > 1;

			Node tail = toRemove ? snake.remove(snake.size()-1) : snake.get(0);

			double tailX = tail.getTranslateX();
			double tailY = tail.getTranslateY();

			//Switch statement for Direction Enums which determine which Enum means and the function of them
			switch (direction) {

			case DOWN:
				tail.setTranslateX(snake.get(0).getTranslateX());
				tail.setTranslateY(snake.get(0).getTranslateY() + blockSize);

				break;

			case LEFT:
				tail.setTranslateX(snake.get(0).getTranslateX() - blockSize);
				tail.setTranslateY(snake.get(0).getTranslateY());

				break;

			case RIGHT:
				tail.setTranslateX(snake.get(0).getTranslateX() + blockSize);
				tail.setTranslateY(snake.get(0).getTranslateY());

				break;

			case UP:
				tail.setTranslateX(snake.get(0).getTranslateX());
				tail.setTranslateY(snake.get(0).getTranslateY() - blockSize);

				break;

			}
			moved = true;
			if (toRemove)
				snake.add(0, tail);

			//Detects collision against body
			for (Node rectangle : snake){
				if (rectangle != tail && tail.getTranslateX() == rectangle.getTranslateX() && 
						tail.getTranslateY() == rectangle.getTranslateY()) {
					restartGame();
					break;
				}

			}
			//Collision detection against the walls of the screen
			if (tail.getTranslateX() < 0 || tail.getTranslateX() >= appWidth || 
					tail.getTranslateY() < 0 || tail.getTranslateY() >= appHeight){
				restartGame();

			}

			//Detects when the snake collides with the food and adds it to the snake body
			if(tail.getTranslateX() == food.getTranslateX() && tail.getTranslateY() == food.getTranslateY()){
				food.setTranslateX((int)(Math.random() * (appWidth - blockSize)) / blockSize * blockSize);
				food.setTranslateY((int)(Math.random() * (appHeight - blockSize)) / blockSize * blockSize);

				Rectangle rectangle = new Rectangle (blockSize, blockSize);

				rectangle.setTranslateX(tailX);
				rectangle.setTranslateY(tailY);

				snake.add(rectangle);

			}

		});

		timeline.getKeyFrames().add(frame);
		timeline.setCycleCount(Timeline.INDEFINITE);

		root.getChildren().addAll(food, snakeBody);



		return root;
	}	


	private void stopGame(){
		running = false;
		timeline.stop();
		snake.clear();

	}

	private void startGame(){
		direction = Direction.RIGHT;
		Rectangle head = new Rectangle(blockSize, blockSize);
		snake.add(head);
		timeline.play();
		running = true;
	}



	private void restartGame() {
		stopGame();
		startGame();

	}




	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene (createContent());
		scene.setOnKeyPressed(event -> {
			if (!moved)
				return;
			

			if (moved){
				switch (event.getCode()){

				case W:

					if (direction != Direction.DOWN)
						direction = Direction.UP;

					break;

				case S:

					if (direction != Direction.UP)
						direction = Direction.DOWN;

					break;

				case A:

					if (direction != Direction.RIGHT)
						direction = Direction.LEFT;

					break;

				case D:

					if (direction != Direction.LEFT)
						direction = Direction.RIGHT;

				default:
					break;
				}

			}

			moved = false;
		});

		primaryStage.setTitle("Snake");
		primaryStage.setScene(scene);
		primaryStage.show();
		startGame();
	}


}
