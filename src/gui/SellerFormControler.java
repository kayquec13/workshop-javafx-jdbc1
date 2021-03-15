package gui;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exception.ValidationException;
import model.services.SellerService;


public class SellerFormControler implements Initializable {

	private Seller entity;
	private SellerService service;
	
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

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void setSellerServices(SellerService service) {
		this.service = service;
	}
	
	//listener, quando um objeto/ metórdo a implementa fica disponivel para receber um evento da classe
	public void subscribeSellerService(DataChangeListener listener) {
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
			//Quando o objeto for salvo com sucesso a aplicação irá chamar o listeners do método setSellerService
			entity = getFormData();
			service.saveOrUpdate(entity);
			//Método que notifica o listener
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

	private Seller getFormData() {
		Seller obj = new Seller();

		ValidationException exception = new ValidationException("Validation Erros");
		// Pega o Id do form e tranforma o id de string para int
		obj.setId(Utils.tryparseToInt(txtId.getText()));
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addErrors("name", "O campo não pode ser vazio");
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
		Constraints.setTextFieldMaxLength(txtId, 40);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBrithDate, "dd/MM/yyyy");
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
		if(entity.getBirthDate() != null) {
			dpBrithDate.setValue(LocalDateTime.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()).toLocalDate());
		}		
	}
	
	private void setErrorMessages(Map<String, String> error) {
		Set<String> fields = error.keySet();
		
		if(fields.contains("name")) {
			labelErroName.setText(error.get("name"));
		}
	}
}
