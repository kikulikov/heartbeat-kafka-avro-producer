package io.confluent.producer;

import io.confluent.ksql.avro_schemas.KsqlDataSourceSchema;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HeartbeatProducerComponent {

  private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatProducerComponent.class);

  @Value("${application.topic.name}")
  private String topicName;

  @Autowired
  @SuppressWarnings("unused")
  private KafkaTemplate<String, KsqlDataSourceSchema> topicTemplate;

  @Scheduled(initialDelayString = "${initialDelay.in.milliseconds}", fixedRateString = "${fixedRate.in.milliseconds}")
  @SuppressWarnings("unused")
  public void produceHeartbeatEvents() {

    try (final var producer = topicTemplate.getProducerFactory().createProducer()) {
      final var partitions = producer.partitionsFor(topicName);

      for (PartitionInfo p : partitions) {
        topicTemplate.send(new ProducerRecord<>(topicName, p.partition(), "DUMMY", buildEvent()));
      }

      LOGGER.info("Produced to " + topicName);
    }
  }

  private static KsqlDataSourceSchema buildEvent() {
    return KsqlDataSourceSchema.newBuilder().setTITLE("DUMMY").setRATING(0.0).setRELEASEYEAR(2024)
        .setTIMESTAMP("2024-01-01 00:00:00").build();
  }
}
