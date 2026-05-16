package cl.duoc.ms_usuarios.Security;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/*
 * Esta clase es casi identica a la de ms-login.
 * La copiamos aqui para que ms-usuarios pueda leer
 * los tokens JWT que genera ms-login, sin necesidad
 * de llamar a ms-login por HTTP cada vez.
 *
 * IMPORTANTE: el jwt.secret en application.properties
 * debe ser exactamente el mismo que en ms-login.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Saca todos los datos del token (lanza excepcion si el token es invalido)
    public Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    // Devuelve true si el token tiene buena firma y no esta vencido
    public boolean esTokenValido(String token) {
        try {
            extraerClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // --- Metodos para sacar datos puntuales del token ---

    public Integer extraerId(String token) {
        return extraerClaims(token).get("id", Integer.class);
    }

    public String extraerEmail(String token) {
        return extraerClaims(token).getSubject();
    }

    public String extraerNombre(String token) {
        return extraerClaims(token).get("nombre", String.class);
    }

    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    /*
     * Saca el token limpio del header Authorization.
     * El header llega como: "Bearer eyJhbGci..."
     * Este metodo devuelve solo: "eyJhbGci..."
     *
     * Devuelve null si el header es invalido.
     */
    public String obtenerTokenDelHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
