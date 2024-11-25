package io.confluent.producer;

import io.confluent.model.avro.Order;
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
  private KafkaTemplate<String, Order> topicTemplate;

  @Scheduled(initialDelayString = "${initialDelay.in.milliseconds}", fixedRateString = "${fixedRate.in.milliseconds}")
  @SuppressWarnings("unused")
  public void produceHeartbeatEvents() {

    try (final var producer = topicTemplate.getProducerFactory().createProducer()) {
      final var partitions = producer.partitionsFor(topicName);

      for (PartitionInfo p : partitions) {
        topicTemplate.send(new ProducerRecord<>(topicName, p.partition(), null,
            Order.newBuilder().setOrderId("XXX").setBookId("XXX").setCardNumber("XXX").setQuantity(0).build()));
      }

      LOGGER.info("Produced to " + topicName);
    }
  }
}
