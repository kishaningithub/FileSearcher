package com.kishan.FileSearcher.ux.desktop;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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

	/** The stop btn. */
	@FXML
	private Button stopBtn;
	
	/** The is reg ex chk box. */
	@FXML
	private CheckBox isRegExChkBox;
	
	/** The data unit cbo. */
	@FXML
	public ChoiceBox<String> dataUnitCbo;
	
	/** The max file size txt. */
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
	 * On open path.
	 *
	 * @param event the event
	 */
	public void onOpenPath(ActionEvent event)
	{
		if(Desktop.isDesktopSupported()){
			Desktop deskTop = Desktop.getDesktop();
			for(String value:searchResultsLstVw.getSelectionModel().getSelectedItems()){
				try {
					deskTop.open(new File(value));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * On copy file path.
	 *
	 * @param event the event
	 */
	public void onCopyFilePath(ActionEvent event)
	{
		List<String> fileLst = searchResultsLstVw.getSelectionModel().getSelectedItems();
		if(!fileLst.isEmpty()){
			Clipboard clipBoard = Clipboard.getSystemClipboard();
			ClipboardContent clipboardContent = new ClipboardContent();
			clipboardContent.putString(String.join("\n", fileLst));
			clipBoard.setContent(clipboardContent);
		}
	}
	
	/**
	 * On copy files.
	 *
	 * @param event the event
	 */
	public void onCopyFiles(ActionEvent event)
	{
		List<String> fileLst = searchResultsLstVw.getSelectionModel().getSelectedItems();
		if(!fileLst.isEmpty()){
			Clipboard clipBoard = Clipboard.getSystemClipboard();
			ClipboardContent clipboardContent = new ClipboardContent();
			clipboardContent.putFilesByPath(fileLst);
			clipBoard.setContent(clipboardContent);
		}
	}

	/**
	 * On btn click.
	 *
	 * @param keyEvent the key event
	 */
	public void onSearch(Event keyEvent)
	{
		searchResultsLstVw.managedProperty().bind(searchResultsLstVw.visibleProperty());
		searchResultsLstVw.setVisible(true);
		final ObservableList<String> observableResultList = FXCollections.observableArrayList();
		searchResultsLstVw.setItems(observableResultList);
		stopBtn.setDisable(false);
		if(service == null){
			searchResultsLstVw.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			service = new Service<Void>() {
				@Override
				protected Task<Void> createTask() {
					SearchInputDTO searchInputDTO = new SearchInputDTO();
					searchInputDTO.setStartDirectory(Paths.get(directoryTF.getText().trim()));
					searchInputDTO.setSearchTxt(searchTF.getText().trim());
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
					stopBtn.setDisable(true);
				}
			});
		}
		searchStatusLbl.textProperty().unbind();
		searchStatusLbl.textProperty().bind(service.messageProperty());
		service.restart();
	}
	
	/**
	 * On stop search.
	 *
	 * @param event the event
	 */
	public void onStopSearch(ActionEvent event)
	{
		if(service != null){
			service.cancel();
		}
	}
	
}