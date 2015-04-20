package com.kishan.FileSearcher.ux.desktop;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import com.kishan.FileSearcher.AppBusiness;
import com.kishan.FileSearcher.dto.SearchResultDTO;

/**
 * The Class MasterSceneController.
 */
public class MasterSceneController 
{

	/** The lbl. */
	@FXML
	private Label searchStatusLbl;

	/** The output file path tf. */
	@FXML
	private TextField directoryTF;

	/** The search tf. */
	@FXML
	private TextField searchTF;

	/** The search results lst vw. */
	@FXML
	private ListView<String> searchResultsLstVw;

	/** The search btn. */
	@FXML
	private Button searchBtn;

	/** The service. */
	private Service<Void> service;

	/** The primary stage. */
	private Stage primaryStage;

	/**
	 * Gets the primary stage.
	 *
	 * @return the primary stage
	 */
	protected Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * Sets the primary stage.
	 *
	 * @param primaryStage the new primary stage
	 */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	/**
	 * On click select directory.
	 *
	 * @param event the event
	 */
	public void onClickSelectDirectory(ActionEvent event)
	{
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File startDirectory = new File(directoryTF.getText().trim());
		if(startDirectory.exists()){
			directoryChooser.setInitialDirectory(startDirectory);
		}
		File selectedDirectory = directoryChooser.showDialog(getPrimaryStage());
		if(selectedDirectory != null){
			directoryTF.setText(selectedDirectory.getAbsolutePath());
		}
	}

	/**
	 * On btn click.
	 *
	 * @param event the event
	 */
	public void onSearchBtnClick(ActionEvent event)
	{
		final File startDirectory = new File(directoryTF.getText().trim());
		final String searchTxt = searchTF.getText().trim();

		if(!startDirectory.exists()){
			searchStatusLbl.setText("Invalid directory.");
			return;
		}

		searchBtn.setDisable(true);
		service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						AppBusiness fileSearch = new AppBusiness(startDirectory, searchTxt, 1 * 1024 * 1024 /* 1MB */);
						// Get user input - End
						updateMessage("Searching please wait...");
						List<SearchResultDTO> resultLst = fileSearch.search();
						updateMessage("Search performed sucessfully.");
						searchResultsLstVw.setItems(FXCollections.observableArrayList(fileSearch.getSearchResultsAsStringLst(resultLst)));
						searchResultsLstVw.getSelectionModel().selectedItemProperty().addListener(
								new ChangeListener<String>() {
									public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
										try {
											Desktop.getDesktop().open(new File(new_val));
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								});
						return null;
					}

				};
			}
		};
		service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				searchStatusLbl.textProperty().unbind();
				searchBtn.setDisable(false);
			}
		});
		service.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				System.out.print(event);

			}

		});
		searchStatusLbl.textProperty().bind(service.messageProperty());
		service.restart();
	}


}