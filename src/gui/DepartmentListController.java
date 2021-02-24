package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	
	@FXML
	public void onBtNewAction() {
		System.out.println("onBtNewAction");
	}
	
	//inicializar o comportamento das colunas
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializenodes();		
	}
	//injetando a dependencia
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
}
