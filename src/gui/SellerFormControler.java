package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exception.ValidationException;
import model.services.DepartmentServices;
import model.services.SellerService;

public class SellerFormControler implements Initializable {

	private Seller entity;
	private SellerService sellerService;
	private DepartmentServices departmentService;

	private List<DataChangeListener> dataChangelisteners = new ArrayList<>();

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpBrithDate;
	@FXML
	private TextField txtBaseSalary;
	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErroName;
	@FXML
	private Label labelErroEmail;
	@FXML
	private Label labelErroBrithDate;
	@FXML
	private Label labelErroBaseSalary;
	@FXML
	private Button btSave;
	@FXML
	private Button btCalcel;

	private ObservableList<Department> obsList;

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService sellerService, DepartmentServices departmentService) {
		this.sellerService = sellerService;
		this.departmentService = departmentService;
	}

	// listener, quando um objeto/ metórdo a implementa fica disponivel para receber
	// um evento da classe
	public void subscribeSellerService(DataChangeListener listener) {
		dataChangelisteners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("A entidade esta nula ps: onBtSaveAction");
		}
		if (sellerService == null) {
			throw new IllegalStateException("O service esta nula ps: onBtSaveAction");
		}

		try {
			// Quando o objeto for salvo com sucesso a aplicação irá chamar o listeners do
			// método setSellerService
			entity = getFormData();
			sellerService.saveOrUpdate(entity);
			// Método que notifica o listener
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}

	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangelisteners) {
			listener.onDataChanged();
		}
	}

	private Seller getFormData() {
		Seller obj = new Seller();

		ValidationException exception = new ValidationException("Validation Erros");
		// Pega o Id do form e tranforma o id de string para int
		obj.setId(Utils.tryparseToInt(txtId.getText()));
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addErrors("name", "O campo não pode ser vazio");
		}
		obj.setName(txtName.getText());
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addErrors("email", "O campo não pode ser vazio");
		}
		obj.setEmail(txtEmail.getText());
		
		if(dpBrithDate.getValue() == null) {
			exception.addErrors("birthDate", "O campo não pode ser vazio");
		}else {
			Instant instant = Instant.from(dpBrithDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		
				
		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exception.addErrors("baseSalary", "O campo não pode ser vazio");
		}
		obj.setBaseSalary(Utils.tryparseToDouble(txtBaseSalary.getText()));										
		
		obj.setDepartment(comboBoxDepartment.getValue());
		
		if (exception.getErrors().size() > 0) {
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
		Constraints.setTextFieldMaxLength(txtId, 40);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBrithDate, "dd/MM/yyyy");
		
		initializeComboBoxDepartment();
	}

//Pega os dados do obj e joga na caixa do formulario
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("A Entidade Seller é nula");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
		if (entity.getBirthDate() != null) {
			dpBrithDate.setValue(
					LocalDateTime.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()).toLocalDate());
		}
		
		if(entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		}else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}		
	}

	public void loadAssociatedIbject() {
		if (departmentService == null) {
			throw new IllegalStateException("DepartmentService estava nulo");
		}
		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private void setErrorMessages(Map<String, String> error) {
		Set<String> fields = error.keySet();

		labelErroName.setText(fields.contains("name") ? error.get("name"): "");
		
		labelErroBaseSalary.setText(fields.contains("baseSalary") ? error.get("baseSalary"): "");
		
		labelErroEmail.setText(fields.contains("email") ? error.get("email"): "");
		
		labelErroBrithDate.setText(fields.contains("birthDate") ? error.get("birthDate"): "");
		
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
