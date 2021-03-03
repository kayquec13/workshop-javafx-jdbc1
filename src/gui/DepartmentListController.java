package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentServices;

public class DepartmentListController implements Initializable {
	//Dependencia da classe
	private DepartmentServices service;
	
	@FXML
	private TableView<Department> tableViewDepartments;
	
	@FXML
	private TableColumn<Department, Integer> tablerColumnId;
	@FXML
	private TableColumn<Department, String> tablerColumnName;
	@FXML
	private Button btNew;
	
	//lista para retornar os departamentos
	private ObservableList<Department> obsList;
	
	// abrindo a tela de novo para cadastrar o novo departamento
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		//instancia um obj Controler vazio
		Department obj = new Department();
		createDialogForm(obj ,"/gui/departmentForm.fxml", parentStage);
	}
	
	//inicializar o comportamento das colunas
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializenodes();		
	}
	//injetando a dependencia Com a classe Department service, que contem o método que retorna a lista de departamentos 
	public void setDepartmentServiice(DepartmentServices service) {
		this.service = service;
	}
	
	private void initializenodes() {		
		//inicializar o comportamento das colunas
		tablerColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tablerColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		//Pega a referencia para o stage atua,
		Stage stage = (Stage) Main.getmainScene().getWindow();
		//Método para acompanhar a janela
		tableViewDepartments.prefHeightProperty().bind(stage.heightProperty());
	}

	//acessa o serviço, carrega o departamento e joga la lista obs
	public void updateTableView() {
		//verifica se o service é null
		if(service == null) {
			throw new IllegalStateException("Service was null");
		}
		//recebe o service.findAll
		List<Department> list = service.findAll();
		//carrega a lista no obs lista
		obsList = FXCollections.observableArrayList(list);
		//carrega o item na tableview
		tableViewDepartments.setItems(obsList);
	}
	
	//Cria o Stage com a caixa de texo para adicionar novo departamento.
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			//Recebe o nome da View
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			//Lê  e injeta o obj Department Vazio e atualiza
			DepartmentFormControler controller = loader.getController();
			controller.setDepartment(obj);
			controller.setDepartmentServices(new DepartmentServices());
			controller.updateFormData();
			// Cria um novo stage
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com as informações do departamento");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
			
		}
		catch(IOException e) {
			Alerts.showAlert("IO Exception", "Error Loading view", e.getMessage(), AlertType.ERROR);
		}
	}	
}
