package org.example.credit.dispute.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.example.domain.dispute.serde.DisputeSerializer;
import org.example.credit.dispute.beans.CreateCreditDisputeBean;
import org.example.domain.dispute.serde.DisputeDeserializer;

@Component
public class CreateCreditDisputeRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(CreateCreditDisputeRouteBuilder.class);

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");

	private String kafkaDisputeSubmittedTopic = System.getenv("DISPUTE_SUBMITTED_TOPIC");
	private String kafkaDisputeResolvedTopic = System.getenv("DISPUTE_RESOLVED_TOPIC");
	private String kafkaDisputeMerchantContactTopic = System.getenv("CUSTOMER_MERCHANT_TOPIC");
	private String kafkaDisputeCustomerContactTopic = System.getenv("CUSTOMER_CONTACT_TOPIC");

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
						.log("\\n/// Create Dispute Service - Sending Dispute Message >>> ${body}")
						.log(" Dispute >>> ${body}").bean(CreateCreditDisputeBean.class, "evaluate")
						.log(" Dispute >>> ${body}").to("kafka:" + kafkaDisputeResolvedTopic
								+ "?serializerClass=" + DisputeSerializer.class.getName());

	}

}
