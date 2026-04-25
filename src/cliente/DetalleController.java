package cliente;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetalleController {

    @FXML private Label cedulaLabel;
    @FXML private Label nombreLabel;
    @FXML private Label correoLabel;
    @FXML private Label telefonoLabel;
    @FXML private Label preferencialLabel;
    @FXML private Label tarjetaLabel;
    @FXML private Label messageLabel;

    private String cedula;

    public void setData(String[] parts) {
        if (parts == null || parts.length < 6) return;
        cedula = parts[0];
        cedulaLabel.setText(cedula);
        nombreLabel.setText(parts[1]);
        correoLabel.setText(parts[2]);
        telefonoLabel.setText(parts[3]);
        preferencialLabel.setText(parts[4]);
        tarjetaLabel.setText(formatSaldoLabel(parts[5]));
    }

    @FXML
    private void handleRecargar(ActionEvent e) {
        try {
            // Cargar diálogo de recarga desde su propio FXML y controlador
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/cliente/recarga.fxml"));
            javafx.scene.Parent root = loader.load();
            RecargaController rc = loader.getController();
            boolean tarjetaExiste = (tarjetaLabel.getText() != null && !tarjetaLabel.getText().toLowerCase().contains("sin"));
            rc.setData(cedula, tarjetaExiste);
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Recargar tarjeta - " + nombreLabel.getText());
            dialog.setScene(new Scene(root));
            rc.setStage(dialog);
            dialog.showAndWait();

            if (rc.isRecharged()) {
                String nuevoSaldo = rc.getNewSaldo();
                tarjetaLabel.setText(formatSaldoLabel(nuevoSaldo));
                messageLabel.setText("Recarga exitosa");
            }
        } catch (Exception ex) {
            messageLabel.setText("Error al abrir diálogo de recarga: " + ex.getMessage());
        }
    }

    @FXML
    private void handlePagar(ActionEvent e) {
        messageLabel.setText("");
        try {
            String req = "PAGAR:" + cedula;
            String resp = Cliente.sendAndReceiveUDPStatic(req);
            if (resp != null && resp.startsWith("OK:")) {
                String payload = resp.substring(3).trim();
                messageLabel.setText("Pago exitoso");
                tarjetaLabel.setText(formatSaldoLabel(payload));
            } else {
                messageLabel.setText(resp == null ? "Respuesta nula del servidor" : resp);
            }
        } catch (Exception ex) {
            messageLabel.setText("Error al pagar: " + ex.getMessage());
        }
    }

    private String formatSaldoLabel(String saldo) {
        if (saldo == null) return "";
        String lower = saldo.toLowerCase();
        if (lower.contains("sin")) return "Sin Tarjeta";
        Matcher m = Pattern.compile("([0-9]+\\.?[0-9]*)").matcher(saldo);
        if (m.find()) {
            try {
                double val = Double.parseDouble(m.group(1));
                DecimalFormat df = new DecimalFormat("0.00");
                return df.format(val);
            } catch (Exception ex) {
                return saldo;
            }
        }
        return saldo;
    }
}

