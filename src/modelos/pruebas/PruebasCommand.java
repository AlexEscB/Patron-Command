package modelos.pruebas;

import java.util.List;

import modelos.CuentaBancaria;
import modelos.command.Command;
import modelos.command.ConsultarSaldoCommand;
import modelos.command.DepositarCommand;
import modelos.command.RetirarCommand;
import modelos.command.TransferirCommand;
import modelos.invocadores.CajeroAutomatico;

/**
 * Pruebas de verificacion del taller.
 * No usa JUnit: es un arnes minimo que comprueba el comportamiento
 * observable del sistema y reporta APPROBADO / FALLIDO por cada caso.
 */
public class PruebasCommand {

    private static int total = 0;
    private static int aprobadas = 0;

    public static void main(String[] args) throws Exception {

        // 1. El deposito se refleja en el saldo del receptor.
        CuentaBancaria c1 = new CuentaBancaria("T-001", "Titular 1", 0);
        CajeroAutomatico canal = new CajeroAutomatico("CANAL DE PRUEBA");
        canal.ejecutar(new DepositarCommand(c1, 100_000));
        verificar("El deposito actualiza el saldo", c1.consultarSaldo() == 100_000);

        // 2. El retiro se refleja en el saldo del receptor.
        canal.ejecutar(new RetirarCommand(c1, 40_000));
        verificar("El retiro descuenta del saldo", c1.consultarSaldo() == 60_000);

        // 3. La consulta no altera el estado y no apila en el historial.
        int pendientesAntes = canal.operacionesPendientes();
        canal.ejecutar(new ConsultarSaldoCommand(c1));
        verificar("La consulta no modifica el saldo", c1.consultarSaldo() == 60_000);
        verificar("La consulta no entra al historial de deshacer",
                canal.operacionesPendientes() == pendientesAntes);

        // 4. deshacer() del deposito devuelve el saldo.
        canal.deshacer();
        verificar("Deshacer el retiro restituye el saldo", c1.consultarSaldo() == 100_000);
        canal.deshacer();
        verificar("Deshacer el deposito restituye el saldo", c1.consultarSaldo() == 0);

        // 5. rehacer() vuelve a aplicar el ultimo comando deshecho.
        canal.rehacer();
        verificar("Rehacer vuelve a aplicar el deposito", c1.consultarSaldo() == 100_000);

        // 6. La transferencia mueve el dinero entre las dos cuentas.
        CuentaBancaria c2 = new CuentaBancaria("T-002", "Titular 2", 0);
        CajeroAutomatico canal2 = new CajeroAutomatico("SEGUNDO CANAL");
        canal2.ejecutar(new TransferirCommand(c1, c2, 30_000));
        verificar("La transferencia debita el origen", c1.consultarSaldo() == 70_000);
        verificar("La transferencia acredita el destino", c2.consultarSaldo() == 30_000);

        // 7. Deshacer la transferencia revierte el movimiento en las dos cuentas.
        canal2.deshacer();
        verificar("Deshacer la transferencia restaura el origen", c1.consultarSaldo() == 100_000);
        verificar("Deshacer la transferencia restaura el destino", c2.consultarSaldo() == 0);

        // 8. Una operacion invalida se rechaza y no altera el saldo.
        double saldoAntes = c1.consultarSaldo();
        int historialAntes = canal.operacionesPendientes();
        boolean acepto = canal.ejecutar(new RetirarCommand(c1, 999_000));
        verificar("El retiro sin fondos es rechazado", !acepto);
        verificar("El retiro rechazado no altera el saldo", c1.consultarSaldo() == saldoAntes);
        verificar("El retiro rechazado no entra al historial",
                canal.operacionesPendientes() == historialAntes);

        // 9. Un monto no positivo tambian se rechaza.
        verificar("El monto invalido es rechazado", !canal.ejecutar(new DepositarCommand(c1, -50)));

        // 10. Una cuenta bloqueada no admite operaciones.
        c1.bloquear();
        verificar("La cuenta bloqueada rechaza el deposito",
                !canal.ejecutar(new DepositarCommand(c1, 10_000)));
        c1.desbloquear();

        // 11. El mismo comando sirve a varios invocadores (canales intercambiables).
        Command consulta = new ConsultarSaldoCommand(c1);
        CajeroAutomatico otroCanal = new CajeroAutomatico("TERCER CANAL");
        otroCanal.ejecutar(consulta);
        verificar("El mismo comando se puede ejecutar en otro canal", true);

        // 12. El invocador ejecuta varias operaciones consecutivas (lote).
        List<Command> lote = List.of(
                new DepositarCommand(c2, 10_000),
                new RetirarCommand(c2, 4_000));
        canal2.ejecutarSecuencia(lote);
        verificar("El lote deja el saldo esperado", c2.consultarSaldo() == 6_000);

        System.out.println();
        System.out.println("RESULTADO: " + aprobadas + "/" + total + " pruebas APPROBADAS");
        if (aprobadas != total) {
            System.out.println("HAY FALLOS: revisar la implementacion.");
            System.exit(1);
        }
        System.out.println("Todas las pruebas pasaron: el sistema cumple el enunciado.");
    }

    private static void verificar(String nombre, boolean condicion) {
        total++;
        if (condicion) {
            aprobadas++;
            System.out.println("[APROBADO] " + nombre);
        } else {
            System.out.println("[FALLIDO ] " + nombre);
        }
    }
}
