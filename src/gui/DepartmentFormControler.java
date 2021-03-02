package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;

public class DepartmentFormControler implements  Initializable {
	
	private Department entity;
	
	
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
	
	@FXML
	public void onBtSaveAction() {
		System.out.println("Save");
	}
	@FXML
	public void onBtCancelAction() {
		System.out.println("Cancel");
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
