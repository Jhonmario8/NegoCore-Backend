# Database schema (reference documentation)

One `.sql` file per table, generated straight from the JPA entities under
[`infrastructure/output/jpa/entity`](../../src/main/java/com/negocore/infrastructure/output/jpa/entity)
using Hibernate's offline schema-generation API — **no database connection is
opened to produce these**, local or remote. See
[`SchemaDocGenerator`](../../src/test/java/com/negocore/tools/SchemaDocGenerator.java)
for how.

## Important caveats

- **This is documentation, not a migration tool.** These files aren't run by
  the app, by a build step, or by anything else — nothing here is applied to
  any database automatically. The app manages its schema via
  `spring.jpa.hibernate.ddl-auto` (see the root [README](../../README.md)),
  independently of this folder.
- **They're only as fresh as the last time someone regenerated them.** If an
  entity changes and nobody runs the command below, these files silently go
  stale — there's no check that keeps them in sync.
- **They describe what Hibernate would create from the entities today**, not
  necessarily the exact live schema in any particular database — the two
  should match if `ddl-auto: validate` is passing, but this folder doesn't
  verify that.

## Regenerating

```bash
./gradlew generateSchemaDocs
```

This writes/overwrites every `docs/schema/<table>.sql` file. Commit the
result like any other source change.
