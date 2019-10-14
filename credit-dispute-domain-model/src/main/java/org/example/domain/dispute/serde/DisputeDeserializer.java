package org.example.domain.dispute.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.domain.dispute.Dispute;

public class DisputeDeserializer implements Deserializer<Dispute> {

	public DisputeDeserializer() {

	}

	@Override
	public void close() {
	}

	@Override
	public void configure(Map arg0, boolean arg1) {
	}

	@Override
	public Dispute deserialize(String arg0, byte[] arg1) {
		ObjectMapper mapper = new ObjectMapper();
		Dispute dispute = null;
		try {
			dispute = mapper.readValue(arg1, Dispute.class);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return dispute;
	}

}
