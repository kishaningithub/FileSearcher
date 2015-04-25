package com.kishan.FileSearcher.ux.desktop;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import com.kishan.FileSearcher.DataSizeUnit;
import com.kishan.FileSearcher.FileSearchTask;
import com.kishan.FileSearcher.dto.SearchInputDTO;

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
	
	@FXML
	private Button stopBtn;
	
	@FXML
	private ToggleButton pauseBtn;
	
	@FXML
	private CheckBox isRegExChkBox;
	
	@FXML
	public ChoiceBox<String> dataUnitCbo;
	
	@FXML
	private TextField maxFileSizeTxt;

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

		final ObservableList<String> observableResultList = FXCollections.observableArrayList();
		searchResultsLstVw.setItems(observableResultList);
		searchBtn.setDisable(true);
		stopBtn.setDisable(false);
		pauseBtn.setDisable(false);
		searchResultsLstVw.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				SearchInputDTO searchInputDTO = new SearchInputDTO();
				searchInputDTO.setStartDirectory(startDirectory.toPath());
				searchInputDTO.setSearchTxt(searchTxt);
				String maxFileSize = maxFileSizeTxt.getText().trim();
				searchInputDTO.setAnySize(maxFileSize.isEmpty());
				if(!searchInputDTO.isAnySize()){
					searchInputDTO.setMaxFileSizeInBytes(Long.parseLong(maxFileSize), DataSizeUnit.valueOf(dataUnitCbo.getValue()));
				}
				searchInputDTO.setRegEx(isRegExChkBox.selectedProperty().get());
				return new FileSearchTask(searchInputDTO, observableResultList);
			}
		};
		service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				searchStatusLbl.textProperty().unbind();
				searchBtn.setDisable(false);
				stopBtn.setDisable(true);
				pauseBtn.setDisable(true);
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