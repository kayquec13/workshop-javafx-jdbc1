package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentServices;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}

	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml",(DepartmentListController controller) -> {
			controller.setDepartmentServiice(new DepartmentServices());
			controller.updateTableView();
		});
	}
	//Método que passa o nome da tela para o LoadView
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {} );
	}

	
	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	
	
	private synchronized <T> void loadView(String absoluteName, Consumer<T> InitializingAction) {
		try {
			//FXMLLoader Carrega uma tela
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			
			VBox newVBox = loader.load();
			//Pega a Scene principal
			Scene mainScene = Main.getmainScene();
			//Acessa a tag content
			VBox mainVBox = (VBox)((ScrollPane) mainScene.getRoot()).getContent();
			//Dentro da tag Content acessa o primeiro Children
			Node mainMenu = mainVBox.getChildren().get(0);
			//limpa os Children da VBox MainView
			mainVBox.getChildren().clear();		
			// Adiciona o menu da MainView
			mainVBox.getChildren().add(mainMenu);
			// Adiciona a coleção de children da view que for chamada
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			//Essas 2 linhas executam as funções que passamos como argumento na função loader
			T controller = loader.getController();
			InitializingAction.accept(controller);
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	


}
