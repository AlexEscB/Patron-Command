package modelos.command;

import modelos.OperacionInvalidaException;

/**
 * Rol: COMMAND (interfaz).
 *
 * Define el contrato comun de TODAS las ordenes del sistema bancario.
 * Solo el invoker (CajeroAutomatico) conoce esta interfaz: nunca ve
 * la clase concreta ni los detalles de la cuenta.
 *
 * Metodos requeridos por el enunciado: ejecutar() y deshacer().
 * Metodos auxiliares (con valor por defecto) para el historial y la bitacora:
 *   - modificaEstado(): permite NO apilar en el historial las consultas,
 *     que no alteran la cuenta (evita ensuciar el deshacer).
 *   - descripcion(): texto legible para la bitacora del canal.
 */
public interface Command {

    /** Ejecuta la operacion sobre el receptor. */
    void ejecutar() throws OperacionInvalidaException;

    /** Revierte la operacion ejecutada (operacion inversa o copia de respaldo). */
    void deshacer() throws OperacionInvalidaException;

    /** true si el comando altera el estado de la cuenta. */
    default boolean modificaEstado() {
        return true;
    }

    /** Descripcion legible para la bitacora del canal. */
    default String descripcion() {
        return getClass().getSimpleName();
    }
}
