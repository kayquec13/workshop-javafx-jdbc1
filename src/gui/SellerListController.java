package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;


public class SellerListController implements Initializable, DataChangeListener {
	// Dependencia da classe
	private SellerService service;

	@FXML
	private TableView<Seller> tableViewSellers;
	@FXML
	private TableColumn<Seller, Integer> tablerColumnId;
	@FXML
	private TableColumn<Seller, String> tablerColumnName;
	@FXML
	private TableColumn<Seller, String> tablerColumnEmail;	
	@FXML
	private TableColumn<Seller, Date> tablerColumnBirthDate;	
	@FXML
	private TableColumn<Seller, Double> tablerColumnBaseSalary;		
	@FXML
	private TableColumn<Seller, Seller> tableColumnEDIT;
	@FXML
	private TableColumn<Seller, Seller> tableColumnREMOVE;
	@FXML
	private Button btNew;

	// lista para retornar os departamentos
	private ObservableList<Seller> obsList;

	// abrindo a tela de novo para cadastrar o novo departamento
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		// instancia um obj Controler vazio
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

	// inicializar o comportamento das colunas
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializenodes();
	}

	// injetando a dependencia Com a classe Seller service, que contem o método
	// que retorna a lista de departamentos
	public void setSellerServiice(SellerService service) {
		this.service = service;
	}

	private void initializenodes() {
		// inicializar o comportamento das colunas
		tablerColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tablerColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tablerColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tablerColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tablerColumnBirthDate, "dd/MM/yyyy");
		tablerColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tablerColumnBaseSalary, 2);
		
		
		
		// Pega a referencia para o stage atua,
		Stage stage = (Stage) Main.getmainScene().getWindow();
		// Método para acompanhar a janela
		tableViewSellers.prefHeightProperty().bind(stage.heightProperty());
	}

	// acessa o serviço, carrega o departamento e joga la lista obs
	public void updateTableView() {
		// verifica se o service é null
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		// recebe o service.findAll
		List<Seller> list = service.findAll();
		// carrega a lista no obs lista
		obsList = FXCollections.observableArrayList(list);
		// carrega o item na tableview
		tableViewSellers.setItems(obsList);

		initEditButtons();
		initRemoveButtons();
	}

	// Cria o Stage com a caixa de texo para adicionar novo departamento.
	private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) {
		try {
			// Recebe o nome da View
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			// Lê e injeta o obj Seller Vazio e atualiza
			SellerFormControler controller = loader.getController();
			controller.setSeller(obj);
			controller.setSellerServices(new SellerService());
			controller.subscribeSellerService(this);
			controller.updateFormData();

			// Cria um novo stage
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com as informações do Vendedor");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error Loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Deseja realmente deletar este departamento ?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Serviço esta nulo");
			}
			
			try {
				service.remove(obj);
				updateTableView();
			}
			catch(DbIntegrityException e){
				Alerts.showAlert("Erro ao remover objeto", null, e.getMessage(), AlertType.ERROR);
			}
			
		}
		
	}

}
