package org.example.credit.dispute.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.example.domain.dispute.serde.DisputeSerializer;
import org.example.credit.dispute.beans.CreditDisputeRegZBean;
import org.example.domain.dispute.serde.DisputeDeserializer;

@Component
public class CreditDisputeRegZRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(CreditDisputeRegZRouteBuilder.class);

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");

	private String kafkaDisputeSubmittedTopic = System.getenv("DISPUTE_SUBMITTED_TOPIC");
	private String kafkaDisputeRegZCalculatedTopic = System.getenv("DISPUTE_REG_Z_CALCULATED_TOPIC");

	private String consumerMaxPollRecords = System.getenv("CONSUMER_MAX_POLL_RECORDS");
	private String consumerCount = System.getenv("CONSUMER_COUNT");
	private String consumerSeekTo = System.getenv("CONSUMER_SEEK_TO");
	private String consumerGroup = System.getenv("CONSUMER_GROUP");

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Create Credit Dispute Routes");

		KafkaComponent kafka = new KafkaComponent();
		kafka.setBrokers(kafkaBootstrap);

		this.getContext().addComponent("kafka", kafka);

		from("kafka:" + kafkaDisputeSubmittedTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
				+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
				+ "&groupId=" + consumerGroup + "&valueDeserializer=" + DisputeDeserializer.class.getName())
						.log("\\n/// Dispute Reg Z Service - Dispute Message >>> ${body}")
						.log(" Dispute >>> ${body}").bean(CreditDisputeRegZBean.class, "evaluate")
						.log(" Dispute >>> ${body}").to("kafka:" + kafkaDisputeRegZCalculatedTopic
								+ "?serializerClass=" + DisputeSerializer.class.getName());

	}

}
