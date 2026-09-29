package com.negocore;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requiere una base de datos PostgreSQL real y variables de entorno "
        + "(NC_DB_*, NEGOCORE_JWT_KEY, NC_CLOUDINARY_*); @Disabled a nivel de método no "
        + "evita que @SpringBootTest levante el ApplicationContext (Spring lo prepara antes "
        + "de que JUnit decida saltar el método), así que se deshabilita la clase completa "
        + "para no intentar conectarse a ninguna base en CI/local sin esas credenciales")
class NegoCoreApplicationTests {

    @Test
    void contextLoads() {
    }

}
