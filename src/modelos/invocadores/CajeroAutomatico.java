package modelos.invocadores;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import modelos.OperacionInvalidaException;
import modelos.command.Command;

/**
 * Rol: INVOKER (CajeroAutomatico).
 *
 * Solo conoce la interfaz Command. No sabe si detras hay una cuenta corriente,
 * una cuenta de ahorro o un producto nuevo: por eso el sistema puede crecer
 * sin tocar esta clase (principio abierto/cerrado).
 *
 * Responsabilidades:
 *   - ejecutar comandos (uno a uno o en secuencia),
 *   - apilar en el historial lo que SI modifica el estado,
 *   - deshacer (pila de hechos) y rehacer (pila de deshechos),
 *   - llevar la bitacora del canal.
 *
 * El mismo invocador representa cualquier canal de atencion (cajero automatico,
 * app movil, oficina): cambia solo su etiqueta, no su comportamiento.
 */
public class CajeroAutomatico {

    private final String canal;
    private final Deque<Command> historial = new ArrayDeque<>();
    private final Deque<Command> deshechos = new ArrayDeque<>();
    private final List<String> bitacora = new ArrayList<>();

    public CajeroAutomatico(String canal) {
        this.canal = canal;
    }

    /** Ejecuta un comando. Si el receptor lo rechaza, no entra al historial. */
    public boolean ejecutar(Command comando) {
        System.out.println("   [" + canal + "] Ejecutando: " + comando.descripcion());
        try {
            comando.ejecutar();
        } catch (OperacionInvalidaException error) {
            System.out.println("   [" + canal + "] OPERACION RECHAZADA: " + error.getMessage());
            bitacora.add("RECHAZADA  | " + comando.descripcion() + " | " + error.getMessage());
            return false;
        }
        bitacora.add("EJECUTADA  | " + comando.descripcion());
        if (comando.modificaEstado()) {
            historial.push(comando);
            deshechos.clear(); // una operacion nueva invalida el historial de rehacer
        }
        return true;
    }

    /** Ejecuta varias operaciones consecutivas (lote del canal). */
    public void ejecutarSecuencia(List<Command> comandos) {
        for (Command comando : comandos) {
            ejecutar(comando);
        }
    }

    /** Deshace la ultima operacion que modifico el estado. */
    public boolean deshacer() {
        if (historial.isEmpty()) {
            System.out.println("   [" + canal + "] No hay operaciones que deshacer.");
            return false;
        }
        Command comando = historial.pop();
        try {
            comando.deshacer();
        } catch (OperacionInvalidaException error) {
            System.out.println("   [" + canal + "] No se pudo deshacer: " + error.getMessage());
            historial.push(comando); // el comando vuelve al historial
            return false;
        }
        deshechos.push(comando);
        bitacora.add("DESHACER   | " + comando.descripcion());
        System.out.println("   [" + canal + "] Se deshizo: " + comando.descripcion());
        return true;
    }

    /** Rehace la ultima operacion deshecha. */
    public boolean rehacer() {
        if (deshechos.isEmpty()) {
            System.out.println("   [" + canal + "] No hay operaciones que rehacer.");
            return false;
        }
        Command comando = deshechos.pop();
        try {
            comando.ejecutar();
        } catch (OperacionInvalidaException error) {
            System.out.println("   [" + canal + "] No se pudo rehacer: " + error.getMessage());
            deshechos.push(comando);
            return false;
        }
        historial.push(comando);
        bitacora.add("REHACER   | " + comando.descripcion());
        System.out.println("   [" + canal + "] Se rehizo: " + comando.descripcion());
        return true;
    }

    public int operacionesPendientes() {
        return historial.size();
    }

    public List<String> getBitacora() {
        return bitacora;
    }

    public String getCanal() {
        return canal;
    }
}
