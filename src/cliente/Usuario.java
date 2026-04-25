package cliente;

public class Usuario {
    private String cedula;
    private String nombre;
    private String correo;
    private String telefono;
    private boolean preferencial;

    public Usuario() {}

    public Usuario(String cedula, String nombre, String correo, String telefono, boolean preferencial) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.preferencial = preferencial;
    }

    public String getCedula() {
        return cedula;
    }
    public String getNombre() {
        return nombre;
    }
    public String getCorreo() {
        return correo;
    }
    public String getTelefono() {
        return telefono;
    }
    public boolean isPreferencial() {
        return preferencial;
    }

    @Override
    public String toString() {
        return "Usuario{cedula='" + cedula + "', nombre='" + nombre + "', correo='" + correo + "', telefono='" + telefono + "', preferencial=" + preferencial + "}";
    }
}

