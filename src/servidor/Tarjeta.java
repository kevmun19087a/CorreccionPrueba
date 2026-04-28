package servidor;
// Creacion de las variables y operaciones en la clase tarjeta
public class Tarjeta {
    private Usuario usuario;
    private double saldo;
// Saldo inicial de la tarjeta
    public Tarjeta(Usuario usuario) {
        this.usuario = usuario;
        this.saldo = 0.0;
    }

    public void asignarTarjeta(Usuario usuario) {
        this.usuario = usuario;
        this.saldo = 0.0;
    }
// Metodo sincronizado para cargar saldo de forma segura
    public synchronized void cargarSaldo(double saldo) {
        if (saldo > 0) {
            this.saldo += saldo;
        }
    }

    public synchronized boolean pagarPasaje() {
// Calcula la tarifa: mitad de precio si es usuario preferencial
        double tarifa = usuario.isPreferencial() ? 0.35 / 2.0 : 0.35;
        if (this.saldo >= tarifa) {
            this.saldo -= tarifa;
            return true;
        }
        return false;
    }

    public double getSaldo() { return saldo; }
    public Usuario getUsuario() { return usuario; }
}

