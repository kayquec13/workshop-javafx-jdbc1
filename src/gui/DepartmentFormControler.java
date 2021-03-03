package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentServices;

public class DepartmentFormControler implements  Initializable {
	
	private Department entity;
	private DepartmentServices service;
	
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;	
	@FXML
	private Label labelErroName;
	@FXML
	private  Button btSave;
	@FXML
	private  Button btCalcel;
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentServices(DepartmentServices service) {
		this.service = service;
	}
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(entity == null) {
			throw new IllegalStateException("A entidade esta nula ps: onBtSaveAction");
		}
		if(service == null){
			throw new IllegalStateException("O service esta nula ps: onBtSaveAction");
		}
		
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			Utils.currentStage(event).close();
		}
		catch(DbException e){
			Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), AlertType.ERROR);
		}
		
	}
	private Department getFormData() {
		Department obj = new Department();
		
		//Pega o Id do form e tranforma o id de string para  int
		obj.setId(Utils.tryparseToInt( txtId.getText()));
		obj.setName(txtName.getText());
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
				
	@Override
	public void initialize(URL url, ResourceBundle rb) {	
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtId, 30);
	}
	
	public void updateFormData() {
		if(entity == null) {
			throw new IllegalStateException("A Entidade departamento é nula");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}	
}
