package modelos.command;

import modelos.CuentaBancaria;
import modelos.OperacionInvalidaException;

/**
 * Rol: CONCRETE COMMAND - DepositarCommand.
 *
 * Empaqueta una peticion: "depositar este monto en ESTA cuenta".
 * Guarda el receptor y los parametros, y cuando se ejecuta delega
 * la logica en el receptor (no la implementa).
 *
 * deshacer() aplica la OPERACION INVERSA: retirar el mismo monto.
 */
public class DepositarCommand implements Command {

    private final CuentaBancaria cuenta;
    private final double monto;
    private boolean ejecutada;

    public DepositarCommand(CuentaBancaria cuenta, double monto) {
        this.cuenta = cuenta;
        this.monto = monto;
    }

    @Override
    public void ejecutar() throws OperacionInvalidaException {
        cuenta.depositar(monto);
        ejecutada = true;
    }

    @Override
    public void deshacer() throws OperacionInvalidaException {
        if (!ejecutada) {
            return;
        }
        cuenta.retirar(monto); // operacion inversa
        ejecutada = false;
    }

    @Override
    public String descripcion() {
        return "Deposito de $" + CuentaBancaria.money(monto) + " en la cuenta " + cuenta.getNumero()
                + " (" + cuenta.getTitular() + ")";
    }
}
