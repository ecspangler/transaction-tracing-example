package org.example.credit.dispute.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.example.domain.dispute.serde.DisputeSerializer;
import org.example.credit.dispute.beans.CreditDisputeMerchantNotifyBean;
import org.example.domain.dispute.serde.DisputeDeserializer;

@Component
public class CreditDisputeMerchantNotifyRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(CreditDisputeMerchantNotifyRouteBuilder.class);

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");

	private String kafkaDisputeMerchantContactTopic = System.getenv("DISPUTE_MERCHANT_CONTACT_TOPIC");
	private String kafkaDisputeMerchantNotifiedTopic = System.getenv("DISPUTE_MERCHANT_NOTIFIED_TOPIC");

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

		from("kafka:" + kafkaDisputeMerchantContactTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
				+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
				+ "&groupId=" + consumerGroup + "&valueDeserializer=" + DisputeDeserializer.class.getName())
						.log("\\n/// Dispute Merchant Notify Service - Dispute Message >>> ${body}")
						.log(" Dispute >>> ${body}").bean(CreditDisputeMerchantNotifyBean.class, "evaluate")
						.log(" Dispute >>> ${body}").to("kafka:" + kafkaDisputeMerchantNotifiedTopic
								+ "?serializerClass=" + DisputeSerializer.class.getName());

	}

}
