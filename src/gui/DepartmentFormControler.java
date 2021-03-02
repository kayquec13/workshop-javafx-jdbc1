package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class DepartmentFormControler implements  Initializable {
	
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
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtId, 30);
	}
	
}
