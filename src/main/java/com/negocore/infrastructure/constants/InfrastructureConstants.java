package com.negocore.infrastructure.constants;

public class InfrastructureConstants {
    public static final String MSG_AUTHENTICATION_STATE_CHANGE_NOT_SUPPORTED = "No se permite cambiar el estado de autenticación.";
    public static final String MSG_TOKEN_EXPIRED = "Tu sesión ha expirado. Inicia sesión nuevamente.";
    public static final String MSG_TOKEN_INVALID = "Tu sesión no es válida. Inicia sesión nuevamente.";
    public static final String KEY_STATUS = "status";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_PATH = "path";
    public static final String EMPTY_STRING = "";
    public static final String UTF_8 = "UTF-8";
    public static final int BAD_REQUEST = 400;
    public static final String MSG_INVALID_DATA = "Los datos enviados no son válidos.";
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int CONFLICT = 409;
    public static final int NOT_FOUND = 404;
    public static final int PAYLOAD_TOO_LARGE = 413;

    public static final String MSG_MALFORMED_REQUEST = "La solicitud tiene un formato inválido o contiene valores no permitidos.";
    public static final String MSG_MISSING_PARAMETER = "Falta el parámetro obligatorio: ";
    public static final String MSG_MISSING_FILE = "Falta el archivo obligatorio.";
    public static final String MSG_FILE_TOO_LARGE = "El archivo supera el tamaño máximo permitido.";
    public static final String MSG_DATA_CONFLICT = "No se pudo completar la operación porque los datos entran en conflicto con registros existentes.";
}
