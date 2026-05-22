package vue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Medicament;
import service.MedicamentService;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class MedicamentController implements Initializable {

    @FXML private TextField txtCode;
    @FXML private TextField txtNom;
    @FXML private ComboBox<String> cmbType;
    @FXML private TextField txtQuantite;
    @FXML private DatePicker datePicker;
    @FXML private CheckBox chkDisponible;
    @FXML private TextField txtRecherche;
    @FXML private TableView<Medicament> tableMedicaments;
    @FXML private TableColumn<Medicament, Integer> colId;
    @FXML private TableColumn<Medicament, String> colCode;
    @FXML private TableColumn<Medicament, String> colNom;
    @FXML private TableColumn<Medicament, String> colType;
    @FXML private TableColumn<Medicament, Integer> colQuantite;
    @FXML private TableColumn<Medicament, Date> colDateExpiration;
    @FXML private TableColumn<Medicament, Boolean> colDisponible;
    @FXML private Label lblTotal;
    @FXML private Label lblDisponibles;
    @FXML private Label lblNonDisponibles;

    private MedicamentService service = new MedicamentService();
    private ObservableList<Medicament> liste = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantiteStock"));
        colDateExpiration.setCellValueFactory(new PropertyValueFactory<>("dateExpiration"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        cmbType.setItems(FXCollections.observableArrayList(
            "Antibiotique", "Antidouleur", "Vitamines",
            "Antihistaminique", "Antidiabétique", "Autres"
        ));

        chargerMedicaments();

        tableMedicaments.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) remplirFormulaire(newVal);
            }
        );
    }

    private void chargerMedicaments() {
        liste.clear();
        List<Medicament> tous = service.listerTous();
        liste.addAll(tous);
        tableMedicaments.setItems(liste);
        mettreAJourStatistiques(tous);
    }

    private void mettreAJourStatistiques(List<Medicament> tous) {
        int total = tous.size();
        int disponibles = 0;
        for (Medicament m : tous) {
            if (m.isDisponible()) disponibles++;
        }
        int nonDisponibles = total - disponibles;
        lblTotal.setText(String.valueOf(total));
        lblDisponibles.setText(String.valueOf(disponibles));
        lblNonDisponibles.setText(String.valueOf(nonDisponibles));
    }

    private void remplirFormulaire(Medicament m) {
        txtCode.setText(m.getCode());
        txtNom.setText(m.getNom());
        cmbType.setValue(m.getType());
        txtQuantite.setText(String.valueOf(m.getQuantiteStock()));
        chkDisponible.setSelected(m.isDisponible());
        if (m.getDateExpiration() != null) {
            LocalDate date = m.getDateExpiration().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
            datePicker.setValue(date);
        }
    }

    @FXML
    private void ajouter() {
        Medicament m = new Medicament();
        m.setCode(txtCode.getText());
        m.setNom(txtNom.getText());
        m.setType(cmbType.getValue());
        m.setQuantiteStock(Integer.parseInt(txtQuantite.getText()));
        m.setDisponible(chkDisponible.isSelected());
        if (datePicker.getValue() != null) {
            Date date = Date.from(datePicker.getValue()
                .atStartOfDay(ZoneId.systemDefault()).toInstant());
            m.setDateExpiration(date);
        }
        service.ajouter(m);
        chargerMedicaments();
        viderFormulaire();
    }

    @FXML
    private void modifier() {
        Medicament m = tableMedicaments.getSelectionModel().getSelectedItem();
        if (m != null) {
            m.setCode(txtCode.getText());
            m.setNom(txtNom.getText());
            m.setType(cmbType.getValue());
            m.setQuantiteStock(Integer.parseInt(txtQuantite.getText()));
            m.setDisponible(chkDisponible.isSelected());
            if (datePicker.getValue() != null) {
                Date date = Date.from(datePicker.getValue()
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());
                m.setDateExpiration(date);
            }
            service.modifier(m);
            chargerMedicaments();
            viderFormulaire();
        }
    }

    @FXML
    private void supprimer() {
        Medicament m = tableMedicaments.getSelectionModel().getSelectedItem();
        if (m != null) {
            service.supprimer(m.getId());
            chargerMedicaments();
            viderFormulaire();
        }
    }

    @FXML
    private void actualiser() {
        chargerMedicaments();
    }

    @FXML
    private void rechercher() {
        String motCle = txtRecherche.getText();
        if (motCle.isEmpty()) {
            chargerMedicaments();
        } else {
            liste.clear();
            List<Medicament> resultats = service.rechercher(motCle);
            liste.addAll(resultats);
            tableMedicaments.setItems(liste);
            mettreAJourStatistiques(resultats);
        }
    }

    private void viderFormulaire() {
        txtCode.clear();
        txtNom.clear();
        cmbType.setValue(null);
        txtQuantite.clear();
        datePicker.setValue(null);
        chkDisponible.setSelected(false);
    }
}