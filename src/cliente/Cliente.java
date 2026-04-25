package cliente;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class Cliente {

    @FXML private TextField cedulaField;
    @FXML private TextArea resultArea;

    @FXML
    private void initialize() {
    }

    @FXML
    private void onBuscar() {
        try {
            String ced = cedulaField.getText().trim();
            String message = "CONSULTAR:" + ced;
            String resp = sendAndReceiveUDP(message);
            if (resp != null && resp.startsWith("OK:")) {
                String payload = resp.substring(3);
                String[] parts = payload.split("\\|", 6);
                FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/cliente/detalle.fxml"));
                Parent root = loader.load();
                DetalleController ctrl = loader.getController();
                ctrl.setData(parts);
                resultArea.setText("");
                Stage stage = new javafx.stage.Stage();
                stage.setTitle("Detalle Usuario - " + parts[1]);
                stage.setScene(new javafx.scene.Scene(root, 250, 270));
                stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                stage.showAndWait();
            } else {
                resultArea.setText(resp);
            }
        } catch (Exception e) {
            resultArea.setText("Error cliente: " + e.getMessage());
        }
    }

    @FXML
    private void onRegistrar() {
        try {
            FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/cliente/register.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Registrar Usuario");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            resultArea.setText("Error al abrir ventana de registro: " + e.getMessage());
        }
    }

    public static String sendAndReceiveUDPStatic(String message) throws Exception {
        int SERVER_PORT = 5000;
        DatagramSocket clientSocket = new DatagramSocket();
        clientSocket.setSoTimeout(5000);
        InetAddress IPAddress = InetAddress.getByName("127.0.0.1");

        byte[] sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, SERVER_PORT);
        clientSocket.send(sendPacket);

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String resp = new String(receivePacket.getData(), 0, receivePacket.getLength());
        clientSocket.close();

        return resp;
    }

    private String sendAndReceiveUDP(String message) throws Exception {
        return sendAndReceiveUDPStatic(message);
    }

}
