package modelos.command;

import modelos.CuentaBancaria;
import modelos.OperacionInvalidaException;

/**
 * Rol: CONCRETE COMMAND - TransferirCommand.
 *
 * Empaqueta la peticion "transferir este monto de la cuenta origen a la cuenta destino".
 * Almacena DOS receptores (origen y destino) y el monto.
 *
 * deshacer() aplica la OPERACION INVERSA: se devuelve el dinero
 * (destino -> origen), no se "borra" el movimiento.
 */
public class TransferirCommand implements Command {

    private final CuentaBancaria origen;
    private final CuentaBancaria destino;
    private final double monto;
    private boolean ejecutada;

    public TransferirCommand(CuentaBancaria origen, CuentaBancaria destino, double monto) {
        this.origen = origen;
        this.destino = destino;
        this.monto = monto;
    }

    @Override
    public void ejecutar() throws OperacionInvalidaException {
        origen.transferir(destino, monto);
        ejecutada = true;
    }

    @Override
    public void deshacer() throws OperacionInvalidaException {
        if (!ejecutada) {
            return;
        }
        destino.transferir(origen, monto); // transferencia inversa
        ejecutada = false;
    }

    @Override
    public String descripcion() {
        return "Transferencia de $" + CuentaBancaria.money(monto) + " de la cuenta "
                + origen.getNumero() + " a la cuenta " + destino.getNumero();
    }
}
