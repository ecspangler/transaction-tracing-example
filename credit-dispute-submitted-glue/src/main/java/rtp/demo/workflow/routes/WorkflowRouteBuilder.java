package rtp.demo.workflow.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import rtp.demo.workflow.transformer.ParseMessage;
import org.example.domain.dispute.serde.DisputeDeserializer;

@Component
public class WorkflowRouteBuilder extends RouteBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(WorkflowRouteBuilder.class);

	private String kafkaBootstrap = System.getenv("BOOTSTRAP_SERVERS");
	private String targetTopic = System.getenv("TARGET_TOPIC");
	private String consumerMaxPollRecords = System.getenv("CONSUMER_MAX_POLL_RECORDS");
	private String consumerCount = System.getenv("CONSUMER_COUNT");
	private String consumerSeekTo = System.getenv("CONSUMER_SEEK_TO");
	private String consumerGroup = System.getenv("CONSUMER_GROUP");
	private String bcHost = System.getenv("BC_HOST");

	@Override
	public void configure() throws Exception {
		LOG.info("Configuring Creditor Core Banking Routes");
		String startCase = "rest:post:/services/rest/server/containers/CreditDisputeProcessingEngine_1.0.0-SNAPSHOT/cases/ProcessingEngine.Workflow/instances?" +
				bcHost +
				"&produces=application/json";

		KafkaComponent kafka = new KafkaComponent();
		kafka.setBrokers(kafkaBootstrap);
		this.getContext().addComponent("kafka", kafka);

		from("kafka:" + targetTopic + "?brokers=" + kafkaBootstrap + "&maxPollRecords="
				+ consumerMaxPollRecords + "&consumersCount=" + consumerCount + "&seekTo=" + consumerSeekTo
				+ "&groupId=" + consumerGroup
				+ "&valueDeserializer=" + DisputeDeserializer.class.getName()) .routeId("FromKafka")
				.setHeader("disputeMsg",simple("${body.grpHdr.msgId}"))
						.log("\n/// Checking my glue >>> ${body}:::::::${body.grpHdr.msgId}")
				        .bean(ParseMessage.class,"process")
						.log("parsed message for case ${body}")
				        .to(startCase).log("To Direct BC${header.disputeMsg}")
						.setHeader("KEY",simple("${header.disputeMsg}"))
						.to("kafka:credit-dispute-case-list?brokers="+kafkaBootstrap).log("To Direct BC${header.disputeMsg}");



	}
}
