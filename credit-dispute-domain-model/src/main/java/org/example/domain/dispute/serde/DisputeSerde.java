package org.example.domain.dispute.serde;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import org.example.domain.dispute.Dispute;

public class DisputeSerde implements Serde<Dispute> {

	private final Serializer<Dispute> serializer;
	private final Deserializer<Dispute> deserializer;

	public DisputeSerde() {
		this.deserializer = new DisputeDeserializer();
		this.serializer = new DisputeSerializer();
	}

	@Override
	public void configure(Map<String, ?> settings, boolean isKey) {
		this.serializer.configure(settings, isKey);
		this.deserializer.configure(settings, isKey);
	}

	@Override
	public void close() {
		this.deserializer.close();
		this.serializer.close();
	}

	@Override
	public Serializer<Dispute> serializer() {
		return this.serializer;
	}

	@Override
	public Deserializer<Dispute> deserializer() {
		return this.deserializer;
	}

}
