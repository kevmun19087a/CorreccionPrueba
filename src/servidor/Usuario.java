package servidor;

public class Usuario {
    private String cedula;
    private String correo;
    private String telefono;
    private String nombre;
    private boolean preferencial;

    public Usuario(String cedula, String correo, String telefono, String nombre, boolean preferencial) {
        this.cedula = cedula;
        this.correo = correo;
        this.telefono = telefono;
        this.nombre = nombre;
        this.preferencial = preferencial;
    }

    public String getCedula() {
        return cedula;
    }
    public String getCorreo() {
        return correo;
    }
    public String getTelefono() {
        return telefono;
    }
    public String getNombre() {
        return nombre;
    }
      // Indica si el usuario es preferencial (true o false)
    public boolean isPreferencial() {
        return preferencial;
    }

    @Override
    public String toString() {
        return "Usuario{cedula='" + cedula + "', nombre='" + nombre + "', correo='" + correo + "', telefono='" + telefono + "', preferencial=" + preferencial + "}";
    }
}
