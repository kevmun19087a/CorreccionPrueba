package cliente;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RecargaController {

    @FXML private TextField montoField;
    @FXML private Button btnRecargar;
    @FXML private Button btnAsignar;
    @FXML private Label messageLabel;

    private String cedula;
    private boolean tarjetaExiste;
    private boolean recharged = false;
    private String newSaldo = null;
    private Stage stage;

    public void setStage(Stage stage) { this.stage = stage; }

    public void setData(String cedula, boolean tarjetaExiste) {
        this.cedula = cedula;
        this.tarjetaExiste = tarjetaExiste;
        btnAsignar.setVisible(!tarjetaExiste);
        btnRecargar.setDisable(!tarjetaExiste);
    }

    @FXML
    private void onAsignar() {
        messageLabel.setText("");
        try {
            String req = "ASIGNAR_TARJETA:" + cedula;
            String resp = Cliente.sendAndReceiveUDPStatic(req);
            if (resp != null && resp.startsWith("OK:")) {
                messageLabel.setText("Tarjeta asignada");
                tarjetaExiste = true;
                btnAsignar.setVisible(false);
                btnRecargar.setDisable(false);
            } else {
                messageLabel.setText(resp == null ? "Sin respuesta del servidor" : resp);
            }
        } catch (Exception ex) {
            messageLabel.setText("Error al asignar tarjeta: " + ex.getMessage());
        }
    }

    @FXML
    private void onRecargar() {
        messageLabel.setText("");
        // Enviar el monto tal cual al servidor y dejar que el servidor valide
        String montoStr = montoField.getText();
        try {
            String req = "CARGAR_TARJETA:" + cedula + "|" + (montoStr == null ? "" : montoStr.trim());
            String resp = Cliente.sendAndReceiveUDPStatic(req);
            if (resp != null && resp.startsWith("OK:")) {
                String payload = resp.substring(3).trim();
                newSaldo = payload;
                recharged = true;
                messageLabel.setText("Recarga exitosa");
                if (stage != null) stage.close();
            } else {
                messageLabel.setText(resp == null ? "Respuesta nula del servidor" : resp);
            }
        } catch (Exception ex) {
            messageLabel.setText("Error al recargar: " + ex.getMessage());
        }
    }

    @FXML
    private void onCancelar() {
        if (stage != null) stage.close();
    }

    public boolean isRecharged() {
        return recharged;
    }
    public String getNewSaldo() {
        return newSaldo;
    }
}

