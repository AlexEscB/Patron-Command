package modelos;


/**
 * Rol: RECEIVER (CuentaBancaria).
 *
 * Contiene TODA la logica de negocio real: depositar, retirar, transferir
 * y consultar. No conoce el patron Command ni a los canales que la invocan.
 * Es un objeto de dominio "tonto": solo responde a lo que se le pide.
 */
public class CuentaBancaria {

    private final String numero;
    private final String titular;
    private double saldo;
    private boolean bloqueada;

    public CuentaBancaria(String numero, String titular, double saldoInicial) {
        this.numero = numero;
        this.titular = titular;
        this.saldo = saldoInicial;
        this.bloqueada = false;
    }

    // ---------- Logica de negocio (solo la conoce el receptor) ----------

    public void depositar(double monto) throws OperacionInvalidaException {
        verificarOperativa();
        if (monto <= 0) {
            throw new OperacionInvalidaException("el monto a depositar debe ser mayor que cero");
        }
        saldo += monto;
        System.out.println("      [CuentaBancaria " + numero + "] deposito de $" + money(monto)
                + ". Saldo: $" + money(saldo));
    }

    public void retirar(double monto) throws OperacionInvalidaException {
        verificarOperativa();
        if (monto <= 0) {
            throw new OperacionInvalidaException("el monto a retirar debe ser mayor que cero");
        }
        if (monto > saldo) {
            throw new OperacionInvalidaException("saldo insuficiente en la cuenta " + numero
                    + " (disponible: $" + money(saldo) + ")");
        }
        saldo -= monto;
        System.out.println("      [CuentaBancaria " + numero + "] retiro de $" + money(monto)
                + ". Saldo: $" + money(saldo));
    }

    public void transferir(CuentaBancaria destino, double monto) throws OperacionInvalidaException {
        verificarOperativa();
        if (destino == null) {
            throw new OperacionInvalidaException("la cuenta destino no fue indicada");
        }
        if (destino == this) {
            throw new OperacionInvalidaException("no se puede transferir a la misma cuenta");
        }
        if (monto <= 0) {
            throw new OperacionInvalidaException("el monto a transferir debe ser mayor que cero");
        }
        if (monto > saldo) {
            throw new OperacionInvalidaException("saldo insuficiente para transferir $" + money(monto)
                    + " (disponible: $" + money(saldo) + ")");
        }
        saldo -= monto;
        System.out.println("      [CuentaBancaria " + numero + "] debito por transferencia de $"
                + money(monto) + " hacia la cuenta " + destino.getNumero() + ". Saldo: $"
                + money(saldo));
        try {
            destino.depositar(monto);
        } catch (OperacionInvalidaException error) {
            saldo += monto; // compensacion: el comando no deja la cuenta a medias
            throw error;
        }
    }

    public double consultarSaldo() {
        return saldo;
    }

    public void bloquear() {
        this.bloqueada = true;
    }

    public void desbloquear() {
        this.bloqueada = false;
    }

    private void verificarOperativa() throws OperacionInvalidaException {
        if (bloqueada) {
            throw new OperacionInvalidaException("la cuenta " + numero + " esta bloqueada");
        }
    }

    // ---------- Datos de solo lectura ----------

    public String getNumero() {
        return numero;
    }

    public String getTitular() {
        return titular;
    }

    public boolean isBloqueada() {
        return bloqueada;
    }

    // ---------- Formato de dinero (independiente de la configuracion regional) ----------

    public static String money(double valor) {
        String digitos = Long.toString(Math.abs(Math.round(valor)));
        StringBuilder salida = new StringBuilder();
        int cuenta = 0;
        for (int i = digitos.length() - 1; i >= 0; i--) {
            salida.append(digitos.charAt(i));
            cuenta++;
            if (cuenta % 3 == 0 && i > 0) {
                salida.append('.');
            }
        }
        return (valor < 0 ? "-" : "") + salida.reverse().toString();
    }
}
