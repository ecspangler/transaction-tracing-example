package org.example.credit.dispute.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.example.credit.dispute.producer.CreditDisputeProducer;
import org.example.domain.dispute.Dispute;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

public class CreditDisputeService extends AbstractVerticle {

	private static final Logger LOGGER = Logger.getLogger(CreditDisputeService.class.getName());

	@Override
	public void start() {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());

		router.post("/disputes-service/disputes").handler(this::createDisputes);

		router.get("/*").handler(StaticHandler.create());

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
		LOGGER.info("THE HTTP APPLICATION HAS STARTED");
	}

	private void createDisputes(RoutingContext routingContext) {
		Disputes disputes = routingContext.getBodyAsJson().mapTo(Disputes.class);
		LOGGER.info("Creating disputes: " + payments);

		CreditDisputesProducer creditDisputesProducer = new CreditDisputesProducer();
		payments.getPayments().forEach(payment -> {

			try {
				// Payment key generated based on timestamp for the reference example
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
				LocalDateTime now = LocalDateTime.now();
				dispute.setDisputeId("DISPUTE" + formatter.format(now));

				debtorPaymentsProducer.sendMessage(payment.getPaymentId(), payment);
				debitPaymentRepository.addPayment(new DebitPayment(payment));

			} catch (Exception e) {
				LOGGER.severe("Error publishing payment to topic");
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		});

		HttpServerResponse response = routingContext.response();
		response.putHeader(CONTENT_TYPE, "application/json; charset=utf-8");
		response.putHeader("Access-Control-Allow-Origin", "*");

		response.end(Json.encode(payments));
	}

}