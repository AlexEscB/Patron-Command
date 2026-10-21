package modelos;


/**
 * Excepcion de negocio del receptor (Receiver).
 * Se usa para que un comando invalido NO se ejecute y, por lo tanto,
 * no entre al historial de deshacer del invocador.
 */
public class OperacionInvalidaException extends Exception {

    private static final long serialVersionUID = 1L;

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
