package org.example.credit.dispute.producer;

import java.util.Properties;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.example.domain.dispute.Dispute;

public class CreditDisputesProducer {
	private static final Logger LOGGER = Logger.getLogger(CreditDisputesProducer.class.getName());

	private CreditDisputesProducerConfig config;
	private KafkaProducer<String, Dispute> producer;
	private Properties props;

	public CreditDisputesProducer() {
		this.config = CreditDisputesProducerConfig.fromEnv();
		this.props = CreditDisputesProducerConfig.createProperties(config);
		this.producer = new KafkaProducer<>(props);
	}

	public void sendMessage(String key, Dispute dispute) throws InterruptedException {
		LOGGER.info("Sending message: {}" + dispute);
		producer.send(new ProducerRecord<>(config.getTopic(), key, dispute));

		LOGGER.info("Message sent.");
	}

	public void closeProducer() {
		producer.close();
	}

}
