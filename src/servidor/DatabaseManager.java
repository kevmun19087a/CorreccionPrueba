package servidor;

import java.sql.*;

public class DatabaseManager {
    private Connection conn;

    public DatabaseManager() throws SQLException {
        String host = System.getenv().getOrDefault("DB_HOST", "localhost");
        String port = System.getenv().getOrDefault("DB_PORT", "3306");
        String dbName = System.getenv().getOrDefault("DB_NAME", "correccion");
        String user = System.getenv().getOrDefault("DB_USER", "root");
        String pass = System.getenv().getOrDefault("DB_PASS", "KBY99ac_");

        String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC", host, port, dbName);
        try {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            conn = DriverManager.getConnection(url, user, pass);
            conn.setAutoCommit(true);
        } catch (SQLException ex) {
            throw new SQLException("No se pudo conectar a la base de datos: " + ex.getMessage(), ex);
        }
    }

    public void init() throws SQLException {
        String ddl = "CREATE TABLE IF NOT EXISTS usuarios ("
                + "cedula VARCHAR(50) PRIMARY KEY,"
                + "nombre VARCHAR(255) NOT NULL,"
                + "correo VARCHAR(255) NOT NULL,"
                + "telefono VARCHAR(50),"
                + "preferencial BOOLEAN NOT NULL"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        try (Statement st = conn.createStatement()) {
            st.execute(ddl);
        }
        String ddlTar = "CREATE TABLE IF NOT EXISTS tarjetas ("
                + "cedula VARCHAR(50) PRIMARY KEY,"
                + "saldo DOUBLE DEFAULT 0.0,"
                + "FOREIGN KEY (cedula) REFERENCES usuarios(cedula) ON DELETE CASCADE"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        try (Statement st = conn.createStatement()) {
            st.execute(ddlTar);
        }
        String checkCol = "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = 'tarjetas' AND COLUMN_NAME = 'asignada'";
        try (PreparedStatement ps = conn.prepareStatement(checkCol)) {
            String catalog = conn.getCatalog();
            ps.setString(1, catalog);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int cnt = rs.getInt(1);
                    if (cnt == 0) {
                        try (Statement st2 = conn.createStatement()) {
                            st2.execute("ALTER TABLE tarjetas ADD COLUMN asignada BOOLEAN NOT NULL DEFAULT FALSE");
                        }
                    }
                }
            }
        }
    }

    public boolean saveUsuario(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios(cedula,nombre,correo,telefono,preferencial) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getCedula());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getCorreo());
            ps.setString(4, u.getTelefono());
            ps.setBoolean(5, u.isPreferencial());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            throw ex;
        }
    }

    public Usuario getUsuario(String cedula) throws SQLException {
        String sql = "SELECT cedula,nombre,correo,telefono,preferencial FROM usuarios WHERE cedula = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String ced = rs.getString("cedula");
                    String nombre = rs.getString("nombre");
                    String correo = rs.getString("correo");
                    String telefono = rs.getString("telefono");
                    boolean pref = rs.getBoolean("preferencial");
                    return new Usuario(ced, correo, telefono, nombre, pref);
                }
            }
        }
        return null;
    }

    public void close() {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    public Double getTarjetaSaldo(String cedula) throws SQLException {
        String sql = "SELECT asignada,saldo FROM tarjetas WHERE cedula = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean asignada = rs.getBoolean("asignada");
                    if (!asignada) return null; // Sin tarjeta asignada
                    return rs.getDouble("saldo");
                }
            }
        }
        return null;
    }

    public boolean createTarjetaRowIfNotExists(String cedula) throws SQLException {
        String sql = "INSERT INTO tarjetas(cedula,asignada,saldo) VALUES(?,FALSE,0.0)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) return false;
            throw ex;
        }
    }

    public boolean assignTarjeta(String cedula) throws SQLException {
        String sql = "UPDATE tarjetas SET asignada = TRUE WHERE cedula = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                String ins = "INSERT INTO tarjetas(cedula,asignada,saldo) VALUES(?,TRUE,0.0)";
                try (PreparedStatement ps2 = conn.prepareStatement(ins)) {
                    ps2.setString(1, cedula);
                    ps2.executeUpdate();
                }
            }
            return true;
        }
    }

    public void updateTarjetaSaldo(String cedula, double saldo) throws SQLException {
        String sql = "UPDATE tarjetas SET saldo = ?, asignada = TRUE WHERE cedula = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, saldo);
            ps.setString(2, cedula);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                String ins = "INSERT INTO tarjetas(cedula,asignada,saldo) VALUES(?,TRUE,?)";
                try (PreparedStatement ps2 = conn.prepareStatement(ins)) {
                    ps2.setString(1, cedula);
                    ps2.setDouble(2, saldo);
                    ps2.executeUpdate();
                }
            }
        }
    }
}

