package com.kishan.FileSearcher.ux.desktop;

import com.kishan.FileSearcher.DataSizeUnit;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
			ObservableList<String> dataUnitLst = FXCollections.observableArrayList();
			for(DataSizeUnit dataSizeUnit:DataSizeUnit.values()){
				dataUnitLst.add(dataSizeUnit.toString());
			}
			masterSceneController.dataUnitCbo.setItems(dataUnitLst);
			masterSceneController.dataUnitCbo.setValue(DataSizeUnit.MB.toString());
			
			Scene scene = new Scene(parent);
			scene.getStylesheets().add(getClass().getResource("/view/css/application.css").toExternalForm());
		
			primaryStage.setTitle("File Searcher");
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