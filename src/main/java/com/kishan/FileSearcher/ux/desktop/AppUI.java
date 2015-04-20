package com.kishan.FileSearcher.ux.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The Class AppUI.
 */
public class AppUI extends Application
{
	
	/* (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/fxml/MasterScene.fxml"));
			Parent parent = (Parent) fxmlLoader.load();
			MasterSceneController masterSceneController = fxmlLoader.getController();
			masterSceneController.setPrimaryStage(primaryStage);
			
			Scene scene = new Scene(parent);
			scene.getStylesheets().add(getClass().getResource("/view/css/application.css").toExternalForm());
		
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}