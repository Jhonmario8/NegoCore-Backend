package com.negocore;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NegoCoreApplicationTests {

    @Test
    @Disabled("Requiere una base de datos PostgreSQL real y variables de entorno "
            + "(NC_DB_*, NEGOCORE_JWT_KEY, NC_CLOUDINARY_*); no se configura contra "
            + "ninguna base en CI/local sin esas credenciales")
    void contextLoads() {
    }

}
