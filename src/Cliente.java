

import java.util.Arrays;
import java.util.List;

import modelos.CuentaBancaria;
import modelos.command.Command;
import modelos.command.ConsultarSaldoCommand;
import modelos.command.DepositarCommand;
import modelos.command.RetirarCommand;
import modelos.command.TransferirCommand;
import modelos.invocadores.CajeroAutomatico;

/**
 * Rol: CLIENTE (Cliente).
 *
 * Es quien arma el sistema: crea los RECEPTORES (cuentas), crea los
 * CONCRETE COMMANDS con esos receptores, crea los INVOKERS (canales)
 * y configura que comando se ejecuta segun la operacion solicitada.
 *
 * El cliente es el unico que conoce las clases concretas; los canales
 * (invokers) y las cuentas (receivers) no se conocen entre si.
 */
public class Cliente {

    public static void main(String[] args) {

        System.out.println("==================================================================");
        System.out.println(" SISTEMA BANCARIO - PATRON DE DISENO COMMAND");
        System.out.println(" Universidad Distrital Francisco Jose de Caldas");
        System.out.println("==================================================================\n");

        // ---------- 1. RECEPTORES (la logica de negocio vive aqui) ----------
        CuentaBancaria corriente = new CuentaBancaria("100-123", "Ana Restrepo", 250_000);
        CuentaBancaria ahorro = new CuentaBancaria("200-987", "Carlos Medina", 1_000_000);

        System.out.println("-- CUENTAS REGISTRADAS --");
        System.out.println("   Corriente " + corriente.getNumero() + " - " + corriente.getTitular()
                + " | saldo inicial: $" + CuentaBancaria.money(corriente.consultarSaldo()));
        System.out.println("   Ahorro    " + ahorro.getNumero() + " - " + ahorro.getTitular()
                + " | saldo inicial: $" + CuentaBancaria.money(ahorro.consultarSaldo()));
        System.out.println();

        // ---------- 2. INVOKERS (los canales de atencion) ----------
        CajeroAutomatico cajero = new CajeroAutomatico("CAJERO AUTOMATICO - Terminal 3");
        CajeroAutomatico digital = new CajeroAutomatico("CANAL DIGITAL - App movil");
        System.out.println("-- CANALES DE ATENCION CONFIGURADOS --");
        System.out.println("   " + cajero.getCanal());
        System.out.println("   " + digital.getCanal());
        System.out.println();

        // =====================================================================
        // TRANSACCION 1: DEPOSITAR en la cuenta corriente (cajero automatico)
        // =====================================================================
        System.out.println("### TRANSACCION 1 - DEPOSITAR $500.000 (cajero automatico)");
        cajero.ejecutar(new DepositarCommand(corriente, 500_000));
        System.out.println();

        // =====================================================================
        // TRANSACCION 2: RETIRAR de la cuenta corriente (cajero automatico)
        // =====================================================================
        System.out.println("### TRANSACCION 2 - RETIRAR $150.000 (cajero automatico)");
        cajero.ejecutar(new RetirarCommand(corriente, 150_000));
        System.out.println();

        // =====================================================================
        // TRANSACCION 3: TRANSFERIR entre cuentas (canal digital)
        // =====================================================================
        System.out.println("### TRANSACCION 3 - TRANSFERIR $200.000 (canal digital)");
        digital.ejecutar(new TransferirCommand(corriente, ahorro, 200_000));
        System.out.println();

        // =====================================================================
        // TRANSACCION 4: CONSULTAR SALDO (los dos canales)
        // =====================================================================
        System.out.println("### TRANSACCION 4 - CONSULTAR SALDOS (los dos canales)");
        cajero.ejecutar(new ConsultarSaldoCommand(corriente));
        digital.ejecutar(new ConsultarSaldoCommand(ahorro));
        System.out.println();

        // =====================================================================
        // LOTE: el canal ejecuta varias operaciones consecutivas
        // =====================================================================
        System.out.println("### LOTE - el cajero ejecuta 3 operaciones consecutivas");
        List<Command> lote = Arrays.asList(
                new DepositarCommand(corriente, 50_000),
                new RetirarCommand(corriente, 30_000),
                new ConsultarSaldoCommand(corriente));
        cajero.ejecutarSecuencia(lote);
        System.out.println();

        // =====================================================================
        // DESHACER / REHACER sobre la ultima operacion del canal digital
        // =====================================================================
        System.out.println("### DESHACER - se revierte la transferencia (canal digital)");
        digital.deshacer();
        System.out.println();

        System.out.println("### REHACER - la transferencia vuelve a aplicarse (canal digital)");
        digital.rehacer();
        System.out.println();

        // =====================================================================
        // TRANSACCION 5: operacion RECHAZADA por el receptor (saldo insuficiente)
        // =====================================================================
        System.out.println("### TRANSACCION 5 - RETIRO DE $900.000 (debe ser rechazado)");
        cajero.ejecutar(new RetirarCommand(corriente, 900_000));
        System.out.println();

        // =====================================================================
        // ESTADO FINAL Y BITACORA
        // =====================================================================
        System.out.println("### ESTADO FINAL DE LAS CUENTAS");
        System.out.println("   Corriente " + corriente.getNumero() + ": $"
                + CuentaBancaria.money(corriente.consultarSaldo()));
        System.out.println("   Ahorro    " + ahorro.getNumero() + ": $"
                + CuentaBancaria.money(ahorro.consultarSaldo()));
        System.out.println("   Operaciones pendientes de deshacer en el cajero: "
                + cajero.operacionesPendientes());
        System.out.println("   Operaciones pendientes de deshacer en el canal digital: "
                + digital.operacionesPendientes());
        System.out.println();

        System.out.println("### BITACORA DE OPERACIONES POR CANAL");
        System.out.println("   -- " + cajero.getCanal() + " --");
        for (String linea : cajero.getBitacora()) {
            System.out.println("   " + linea);
        }
        System.out.println("   -- " + digital.getCanal() + " --");
        for (String linea : digital.getBitacora()) {
            System.out.println("   " + linea);
        }
        System.out.println("==================================================================");
    }
}
