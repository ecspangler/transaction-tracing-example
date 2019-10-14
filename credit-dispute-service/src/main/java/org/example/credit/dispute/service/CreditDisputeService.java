package org.example.credit.dispute.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

import org.example.credit.dispute.producer.CreditDisputesProducer;
import org.example.domain.dispute.Disputes;

public class CreditDisputeService extends AbstractVerticle {

	private static final Logger LOGGER = Logger.getLogger(CreditDisputeService.class.getName());

	@Override
	public void start() {
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());

		router.post("/payments-service/payments").handler(this::createPayments);
		router.post("/transactions-service/queries/transactions").handler(this::getTransactionsByAccount);

		router.get("/*").handler(StaticHandler.create());

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
		LOGGER.info("THE HTTP APPLICATION HAS STARTED");

	}

	private void createPayments(RoutingContext routingContext) {
		Disputes disputes = routingContext.getBodyAsJson().mapTo(Disputes.class);
		LOGGER.info("Creating payments: " + disputes);

		// CreditDisputesProducer debtorPaymentsProducer = new CreditDisputesProducer();
		disputes.getDisputes().forEach(payment -> {

			// Find the routing number and account number for the payee
			// payment = PayeeAccountLookupBean.enrichPayeeAccountInformation(payment);

			try {
				// Payment key generated based on timestamp for the reference example
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
				LocalDateTime now = LocalDateTime.now();
				// payment.setPaymentId("KEYFORTHBANK" + formatter.format(now));

				// debtorPaymentsProducer.sendMessage(payment.getPaymentId(), payment);
				// debitPaymentRepository.addPayment(new DebitPayment(payment));

			} catch (Exception e) {
				LOGGER.severe("Error publishing payment to topic");
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		});

		HttpServerResponse response = routingContext.response();
		response.putHeader(CONTENT_TYPE, "application/json; charset=utf-8");
		response.putHeader("Access-Control-Allow-Origin", "*");

		response.end(Json.encode(disputes));
	}

	private void getTransactionsByAccount(RoutingContext routingContext) {
		TransactionsRequest transactionsRequest = routingContext.getBodyAsJson().mapTo(TransactionsRequest.class);
		LOGGER.info("Retrieving transactions for account: " + transactionsRequest.getAccountNumber());
		// Transactions transactions = new Transactions();
		Disputes disputes = new Disputes();

		// call lookups
		// List<DebitPayment> debitPayments =
		// debitPaymentRepository.getPayments(transactionsRequest.getAccountNumber());
		// List<CreditPayment> creditPayments = creditPaymentRepository
		// .getPayments(transactionsRequest.getAccountNumber());

//		debitPayments.forEach(debitPayment -> {
//			Transaction transaction = new Transaction();
//			transaction.setTransId(debitPayment.getPaymentId());
//			transaction.setAmount(debitPayment.getAmount());
//			transaction.setCreditDebitCode("DEBIT");
//			transaction.setReceiverFirstName(debitPayment.getReceiverFirstName());
//			transaction.setReceiverLastName(debitPayment.getReceiverLastName());
//			transaction.setReceiverEmail(debitPayment.getReceiverEmail());
//			transaction.setReceiverCellPhone(debitPayment.getReceiverCellPhone());
//			transaction.setStatus(debitPayment.getStatus());
//			transactions.getTransactions().add(transaction);
//		});

//		creditPayments.forEach(creditPayment -> {
//			Transaction transaction = new Transaction();
//			transaction.setTransId(creditPayment.getPaymentId());
//			transaction.setAmount(creditPayment.getAmount());
//			transaction.setCreditDebitCode("CREDIT");
//			transaction.setReceiverFirstName(creditPayment.getReceiverFirstName());
//			transaction.setReceiverLastName(creditPayment.getReceiverLastName());
//			transaction.setReceiverEmail(creditPayment.getReceiverEmail());
//			transaction.setReceiverCellPhone(creditPayment.getReceiverCellPhone());
//			transaction.setStatus(creditPayment.getStatus());
//			transactions.getTransactions().add(transaction);
//		});

		// LOGGER.info("Retrieved transactions: " + transactions.getTransactions());

		routingContext.response().putHeader(CONTENT_TYPE, "application/json; charset=utf-8")
				.putHeader("Access-Control-Allow-Origin", "*").end(Json.encodePrettily(disputes));
	}

}