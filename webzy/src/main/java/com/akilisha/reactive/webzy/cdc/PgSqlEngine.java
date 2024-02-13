package com.akilisha.reactive.webzy.cdc;

import io.debezium.data.Envelope;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.debezium.data.Envelope.FieldName.*;
import static java.util.stream.Collectors.toMap;

@Slf4j
public class PgSqlEngine {

    public static PgSqlEngine instance = new PgSqlEngine();
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    DebeziumEngine<RecordChangeEvent<SourceRecord>> engine;

    private PgSqlEngine() {
    }

    private static Properties configProperties() {
        final Properties props = new Properties();
        props.setProperty("name", "jdbcsessions-engine");
        props.setProperty("connector.class", "io.debezium.connector.postgresql.PostgresConnector");
        props.setProperty("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore");
        props.setProperty("offset.storage.file.filename", "C:\\Projects\\java\\reactive\\webzy\\cdc\\pgsql\\offset.dat");
        props.setProperty("offset.flush.interval.ms", "60000");
        /* begin connector properties */
        props.setProperty("database.hostname", "localhost");
        props.setProperty("database.port", "5432");
        props.setProperty("database.user", "postgres");
        props.setProperty("database.password", "postgres");
        props.setProperty("database.dbname", "jdbcsessions");
        props.setProperty("database.server.name", "localhost-jdbcsessions");
        props.setProperty("plugin.name", "pgoutput");
        props.setProperty("topic.prefix", "webzy");
        props.setProperty("table.include.list", "jettysessions,todos");
        props.setProperty("schema.history.internal",
                "io.debezium.storage.file.history.FileSchemaHistory");
        props.setProperty("schema.history.internal.file.filename",
                "C:\\Projects\\java\\reactive\\webzy\\cdc\\pgsql\\schemahistory.dat");
        return props;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        PgSqlEngine.instance.listen();
        System.out.println("Press any key and <Enter> to stop the standalone cdc engine...");
        int in = System.in.read();
        System.out.printf("%b - bye\n", in);
        Runtime.getRuntime().exit(0);
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {
        SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();
        Struct sourceRecordChangeValue = (Struct) sourceRecord.value();

        if (sourceRecordChangeValue != null) {
            Envelope.Operation operation = Envelope.Operation.forCode((String) sourceRecordChangeValue.get(OPERATION));

            if (operation != Envelope.Operation.READ) {
                String record = operation == Envelope.Operation.DELETE ? BEFORE : AFTER;
                Struct struct = (Struct) sourceRecordChangeValue.get(record);
                Map<String, Object> payload = struct.schema().fields().stream()
                        .map(Field::name)
                        .filter(fieldName -> struct.get(fieldName) != null)
                        .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                        .collect(toMap(Pair::getKey, Pair::getValue));

                // notify something of new event
                System.out.println(payload);
            }
        }
    }

    public void listen() throws InterruptedException {
        // Define the configuration for the Debezium Engine with MySQL connector...
        final Properties props = configProperties();

        // Create the engine with this configuration ...
        this.engine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(props)
                .notifying(this::handleChangeEvent).build();

        // Run the engine asynchronously ...
        executor.execute(engine);

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("Exiting application gracefully");
                engine.close();
                executor.shutdown();
                while (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.info("Waiting another 5 seconds for the embedded engine to shut down");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }
    // Engine is stopped when the main code is finished
}
