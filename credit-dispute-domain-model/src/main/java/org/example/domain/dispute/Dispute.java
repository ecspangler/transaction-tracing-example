package org.example.domain.dispute;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Dispute")
@XmlAccessorType(XmlAccessType.FIELD)
public class Dispute implements Serializable {

	private static final long serialVersionUID = -2261149654420343746L;

	private String disputeId;
	private String accountNumber;
	private String transactionId;
	private BigDecimal transactionAmount;
	private BigDecimal disputeAmount;
	private String merchantId;

	public String getDisputeId() {
		return disputeId;
	}

	public void setDisputeId(String disputeId) {
		this.disputeId = disputeId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public BigDecimal getDisputeAmount() {
		return disputeAmount;
	}

	public void setDisputeAmount(BigDecimal disputeAmount) {
		this.disputeAmount = disputeAmount;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountNumber == null) ? 0 : accountNumber.hashCode());
		result = prime * result + ((disputeAmount == null) ? 0 : disputeAmount.hashCode());
		result = prime * result + ((disputeId == null) ? 0 : disputeId.hashCode());
		result = prime * result + ((merchantId == null) ? 0 : merchantId.hashCode());
		result = prime * result + ((transactionAmount == null) ? 0 : transactionAmount.hashCode());
		result = prime * result + ((transactionId == null) ? 0 : transactionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dispute other = (Dispute) obj;
		if (accountNumber == null) {
			if (other.accountNumber != null)
				return false;
		} else if (!accountNumber.equals(other.accountNumber))
			return false;
		if (disputeAmount == null) {
			if (other.disputeAmount != null)
				return false;
		} else if (!disputeAmount.equals(other.disputeAmount))
			return false;
		if (disputeId == null) {
			if (other.disputeId != null)
				return false;
		} else if (!disputeId.equals(other.disputeId))
			return false;
		if (merchantId == null) {
			if (other.merchantId != null)
				return false;
		} else if (!merchantId.equals(other.merchantId))
			return false;
		if (transactionAmount == null) {
			if (other.transactionAmount != null)
				return false;
		} else if (!transactionAmount.equals(other.transactionAmount))
			return false;
		if (transactionId == null) {
			if (other.transactionId != null)
				return false;
		} else if (!transactionId.equals(other.transactionId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Dispute [disputeId=" + disputeId + ", accountNumber=" + accountNumber + ", transactionId="
				+ transactionId + ", transactionAmount=" + transactionAmount + ", disputeAmount=" + disputeAmount
				+ ", merchantId=" + merchantId + "]";
	}

}
