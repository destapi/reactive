package com.akilisha.reactive.webzy.cdc;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MySqlEngine {

    public static MySqlEngine instance = new MySqlEngine();
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    DebeziumEngine<ChangeEvent<String, String>> engine;

    private MySqlEngine() {
    }

    private static Properties configProperties() {
        final Properties props = new Properties();
        props.setProperty("name", "mysql-engine");
        props.setProperty("connector.class", "io.debezium.connector.mysql.MySqlConnector");
        props.setProperty("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore");
        props.setProperty("offset.storage.file.filename", "C:\\Projects\\java\\reactive\\webzy\\cdc\\mysql\\offset.dat");
        props.setProperty("offset.flush.interval.ms", "60000");
        /* begin connector properties */
        props.setProperty("database.hostname", "localhost");
        props.setProperty("database.port", "3306");
        props.setProperty("database.user", "mysqluser");
        props.setProperty("database.password", "mysqlpw");
        props.setProperty("database.server.id", "85744");
        props.setProperty("plugin.name", "pgoutput");
        props.setProperty("topic.prefix", "mywebzy");
        props.setProperty("schema.history.internal",
                "io.debezium.storage.file.history.FileSchemaHistory");
        props.setProperty("schema.history.internal.file.filename",
                "C:\\Projects\\java\\reactive\\webzy\\cdc\\mysql\\schemahistory.dat");
        return props;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        MySqlEngine.instance.listen();
        System.out.println("Press any key and <Enter> to stop the standalone cdc engine...");
        int in = System.in.read();
        System.out.printf("%b - bye\n", in);
        Runtime.getRuntime().exit(0);
    }

    private void handleChangeEvent(List<ChangeEvent<String, String>> records, DebeziumEngine.RecordCommitter<ChangeEvent<String, String>> committer) throws InterruptedException {
        for (ChangeEvent<String, String> record : records) {
            System.out.println("Key = '" + record.key() + "' value = '" + record.value() + "'");
            committer.markProcessed(record);
        }
    }

    public void listen() throws InterruptedException {
        // Define the configuration for the Debezium Engine with MySQL connector...
        final Properties props = configProperties();

        // Create the engine with this configuration ...
        this.engine = DebeziumEngine.create(Json.class)
                .using(props)
                .notifying(this::handleChangeEvent)
                .build();

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
