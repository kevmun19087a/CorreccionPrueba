package servidor;

public class Tarjeta {
    private Usuario usuario;
    private double saldo;

    public Tarjeta(Usuario usuario) {
        this.usuario = usuario;
        this.saldo = 0.0;
    }

    public void asignarTarjeta(Usuario usuario) {
        this.usuario = usuario;
        this.saldo = 0.0;
    }

    public synchronized void cargarSaldo(double saldo) {
        if (saldo > 0) {
            this.saldo += saldo;
        }
    }

    public synchronized boolean pagarPasaje() {
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

