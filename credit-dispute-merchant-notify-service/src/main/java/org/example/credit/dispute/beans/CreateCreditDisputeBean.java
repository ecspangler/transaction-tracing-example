package org.example.credit.dispute.beans;

import java.math.BigDecimal;
import java.util.logging.Logger;

import org.example.domain.dispute.Dispute;

public class CreateCreditDisputeBean {

	private static final Logger LOG = Logger.getLogger(CreateCreditDisputeBean.class.getName());

	// Placeholder method for fraud evaluation or call to fraud system
	public Dispute evaluate(Dispute dispute) {

		LOG.info("Dispute: " + dispute);


		LOG.info("Dispute: " + dispute);

		return dispute;
	}

}
