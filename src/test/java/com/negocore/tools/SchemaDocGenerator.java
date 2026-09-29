package com.negocore.tools;

import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import org.hibernate.jpa.HibernatePersistenceProvider;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates one .sql file per JPA entity/table under docs/schema/, directly
 * from the entity mappings via Hibernate's offline schema-generation API.
 * No database connection is opened, local or remote — the dialect alone is
 * enough for Hibernate to produce the DDL.
 *
 * Not a JUnit test: has no {@code @Test} method, so it never runs as part of
 * {@code ./gradlew test} or {@code build}/CI. Run it explicitly with:
 *
 * <pre>./gradlew generateSchemaDocs</pre>
 *
 * Regenerate after changing any entity under infrastructure/output/jpa/entity.
 */
public final class SchemaDocGenerator {

    private static final String DIALECT = "org.hibernate.dialect.PostgreSQLDialect";
    private static final Path OUTPUT_DIR = Path.of("docs", "schema");

    private static final Map<String, String> ENTITY_TO_TABLE = new LinkedHashMap<>();

    static {
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.AuditLogEntity", "audit_logs");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.BusinessEntity", "businesses");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.CategoryEntity", "category");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.ClientEntity", "clients");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.DebtEntity", "debts");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.DebtPaymentEntity", "debt_payments");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.ExpenseEntity", "expenses");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.OrderEntity", "orders");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.OrderItemEntity", "order_items");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.PayableEntity", "payables");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.PayablePaymentEntity", "payable_payments");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.ProductEntity", "products");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.ProviderEntity", "providers");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.PurchaseEntity", "purchases");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.PurchaseItemEntity", "purchase_items");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.SaleEntity", "sales");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.SaleItemEntity", "sale_items");
        ENTITY_TO_TABLE.put("com.negocore.infrastructure.output.jpa.entity.UserEntity", "users");
    }

    public static void main(String[] args) throws IOException {
        String script = generateScript();
        Map<String, String> ddlByTable = splitByTable(script);

        Files.createDirectories(OUTPUT_DIR);

        for (Map.Entry<String, String> entry : ENTITY_TO_TABLE.entrySet()) {
            String entityFqcn = entry.getKey();
            String tableName = entry.getValue();
            String ddl = ddlByTable.get(tableName);
            if (ddl == null) {
                throw new IllegalStateException(
                        "No generated DDL found for table '" + tableName + "' (entity " + entityFqcn
                                + "). Did its @Table name change? Update ENTITY_TO_TABLE in this class.");
            }

            String header = "-- Generated automatically by `./gradlew generateSchemaDocs`\n"
                    + "-- from " + entityFqcn + " on " + LocalDate.now() + ".\n"
                    + "-- Reference documentation only: this file is not run anywhere (not by\n"
                    + "-- the app, not by a migration tool) and is not kept in sync automatically.\n"
                    + "-- Regenerate after changing this entity.\n\n";

            Path outFile = OUTPUT_DIR.resolve(tableName + ".sql");
            Files.writeString(outFile, header + ddl.trim() + "\n");
            System.out.println("Wrote " + outFile);
        }
    }

    private static String generateScript() {
        StringWriter writer = new StringWriter();

        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", DIALECT);
        // Matches Spring Boot's own default (see HibernateProperties$Naming in
        // spring-boot-autoconfigure) so entities with no explicit @Column name
        // (e.g. ProductEntity.businessId) generate the same snake_case column
        // names the real app produces, instead of Hibernate's raw-name default.
        props.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        props.put("jakarta.persistence.schema-generation.scripts.action", "create");
        props.put("jakarta.persistence.schema-generation.scripts.create-target", writer);
        props.put("jakarta.persistence.schema-generation.create-source", "metadata");

        new HibernatePersistenceProvider().generateSchema(persistenceUnitInfo(), props);

        return writer.toString();
    }

    private static Map<String, String> splitByTable(String script) {
        Map<String, String> result = new LinkedHashMap<>();
        Pattern createTable = Pattern.compile(
                "create table (?:if not exists )?(\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = createTable.matcher(script);

        List<Integer> starts = new ArrayList<>();
        List<String> names = new ArrayList<>();
        while (matcher.find()) {
            starts.add(matcher.start());
            names.add(matcher.group(1).toLowerCase());
        }

        for (int i = 0; i < starts.size(); i++) {
            int from = starts.get(i);
            int to = (i + 1 < starts.size()) ? starts.get(i + 1) : script.length();
            result.put(names.get(i), script.substring(from, to));
        }
        return result;
    }

    private static PersistenceUnitInfo persistenceUnitInfo() {
        return new PersistenceUnitInfo() {
            @Override
            public String getPersistenceUnitName() {
                return "schemaDocGenerator";
            }

            @Override
            public String getPersistenceProviderClassName() {
                return HibernatePersistenceProvider.class.getName();
            }

            @Override
            public PersistenceUnitTransactionType getTransactionType() {
                return PersistenceUnitTransactionType.RESOURCE_LOCAL;
            }

            @Override
            public DataSource getJtaDataSource() {
                return null;
            }

            @Override
            public DataSource getNonJtaDataSource() {
                return null;
            }

            @Override
            public List<String> getMappingFileNames() {
                return List.of();
            }

            @Override
            public List<URL> getJarFileUrls() {
                return List.of();
            }

            @Override
            public URL getPersistenceUnitRootUrl() {
                return null;
            }

            @Override
            public List<String> getManagedClassNames() {
                return List.copyOf(ENTITY_TO_TABLE.keySet());
            }

            @Override
            public boolean excludeUnlistedClasses() {
                return true;
            }

            @Override
            public SharedCacheMode getSharedCacheMode() {
                return SharedCacheMode.UNSPECIFIED;
            }

            @Override
            public ValidationMode getValidationMode() {
                return ValidationMode.AUTO;
            }

            @Override
            public Properties getProperties() {
                return new Properties();
            }

            @Override
            public String getPersistenceXMLSchemaVersion() {
                return "3.1";
            }

            @Override
            public ClassLoader getClassLoader() {
                return Thread.currentThread().getContextClassLoader();
            }

            @Override
            public void addTransformer(ClassTransformer transformer) {
                // no-op: nothing needs to instrument classes for offline DDL generation
            }

            @Override
            public ClassLoader getNewTempClassLoader() {
                return null;
            }
        };
    }

    private SchemaDocGenerator() {
    }
}
