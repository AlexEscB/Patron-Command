package modelos.command;

import modelos.CuentaBancaria;

/**
 * Rol: CONCRETE COMMAND - ConsultarSaldoCommand.
 *
 * Consulta el saldo de una cuenta. Es un comando que NO modifica el estado:
 * por eso sobrescribe modificaEstado() en false, de modo que el invocador
 * no lo apila en el historial de deshacer (el deshacer solo aplica a
 * operaciones que alteran la cuenta).
 */
public class ConsultarSaldoCommand implements Command {

    private final CuentaBancaria cuenta;

    public ConsultarSaldoCommand(CuentaBancaria cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    public void ejecutar() {
        System.out.println("      [CuentaBancaria " + cuenta.getNumero() + "] saldo consultado: $"
                + CuentaBancaria.money(cuenta.consultarSaldo()));
    }

    @Override
    public void deshacer() {
        System.out.println("      [Consulta de saldo] no hay estado que deshacer");
    }

    @Override
    public boolean modificaEstado() {
        return false;
    }

    @Override
    public String descripcion() {
        return "Consulta de saldo de la cuenta " + cuenta.getNumero();
    }
}
