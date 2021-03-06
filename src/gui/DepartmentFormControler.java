package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
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
import model.exception.ValidationException;
import model.services.DepartmentServices;

public class DepartmentFormControler implements Initializable {

	private Department entity;
	private DepartmentServices service;
	
	private List<DataChangeListener> dataChangelisteners = new ArrayList<>();

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private Label labelErroName;
	@FXML
	private Button btSave;
	@FXML
	private Button btCalcel;

	public void setDepartment(Department entity) {
		this.entity = entity;
	}

	public void setDepartmentServices(DepartmentServices service) {
		this.service = service;
	}
	
	//listener, quando um objeto/ met�rdo a implementa fica disponivel para receber um evento da classe
	public void subscribeDepartmentService(DataChangeListener listener) {
		dataChangelisteners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("A entidade esta nula ps: onBtSaveAction");
		}
		if (service == null) {
			throw new IllegalStateException("O service esta nula ps: onBtSaveAction");
		}

		try {
			//Quando o objeto for salvo com sucesso a aplica��o ir� chamar o listeners do m�todo setDepartmentService
			entity = getFormData();
			service.saveOrUpdate(entity);
			//M�todo que notifica o listener
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), AlertType.ERROR);
		}
		catch(ValidationException e) {
			setErrorMessages(e.getErrors());
		}

	}

	private void notifyDataChangeListeners() {		
		for(DataChangeListener listener : dataChangelisteners) {
			listener.onDataChanged();
		}
	}

	private Department getFormData() {
		Department obj = new Department();

		ValidationException exception = new ValidationException("Validation Erros");
		// Pega o Id do form e tranforma o id de string para int
		obj.setId(Utils.tryparseToInt(txtId.getText()));
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addErrors("name", "O campo n�o pode ser vazio");
		}
		obj.setName(txtName.getText());
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
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
		if (entity == null) {
			throw new IllegalStateException("A Entidade departamento � nula");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	
	private void setErrorMessages(Map<String, String> error) {
		Set<String> fields = error.keySet();
		
		if(fields.contains("name")) {
			labelErroName.setText(error.get("name"));
		}
	}
}
