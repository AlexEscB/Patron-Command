package modelos.command;

import modelos.CuentaBancaria;
import modelos.OperacionInvalidaException;

/**
 * Rol: CONCRETE COMMAND - RetirarCommand.
 *
 * Empaqueta la peticion "retirar este monto de ESTA cuenta".
 * deshacer() aplica la OPERACION INVERSA: depositar el mismo monto.
 */
public class RetirarCommand implements Command {

    private final CuentaBancaria cuenta;
    private final double monto;
    private boolean ejecutada;

    public RetirarCommand(CuentaBancaria cuenta, double monto) {
        this.cuenta = cuenta;
        this.monto = monto;
    }

    @Override
    public void ejecutar() throws OperacionInvalidaException {
        cuenta.retirar(monto);
        ejecutada = true;
    }

    @Override
    public void deshacer() throws OperacionInvalidaException {
        if (!ejecutada) {
            return;
        }
        cuenta.depositar(monto); // operacion inversa
        ejecutada = false;
    }

    @Override
    public String descripcion() {
        return "Retiro de $" + CuentaBancaria.money(monto) + " de la cuenta " + cuenta.getNumero()
                + " (" + cuenta.getTitular() + ")";
    }
}
