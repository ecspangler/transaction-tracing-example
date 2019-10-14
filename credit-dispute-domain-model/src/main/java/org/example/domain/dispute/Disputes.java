package org.example.domain.dispute;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Disputes")
@XmlAccessorType(XmlAccessType.FIELD)
public class Disputes implements Serializable {

	private static final long serialVersionUID = 6889694644841796150L;

	private List<Dispute> disputes = new ArrayList<Dispute>();

	public List<Dispute> getDisputes() {
		return disputes;
	}

	public void setDisputes(List<Dispute> disputes) {
		this.disputes = disputes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((disputes == null) ? 0 : disputes.hashCode());
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
		Disputes other = (Disputes) obj;
		if (disputes == null) {
			if (other.disputes != null)
				return false;
		} else if (!disputes.equals(other.disputes))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Disputes [disputes=" + disputes + "]";
	}

}
