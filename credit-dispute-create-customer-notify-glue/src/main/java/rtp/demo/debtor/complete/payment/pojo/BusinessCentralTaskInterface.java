package rtp.demo.debtor.complete.payment.pojo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

public interface BusinessCentralTaskInterface {

	@PUT
	@Path("/services/rest/server/containers/CreditDisputeProcessingEngine_1.0.0-SNAPSHOT/cases/instances/{caseId}/tasks/Credit%20Card%20Dispute%20Create%20Customer%20Notified")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void triggerAdhocTask(@PathParam("caseId") String caseId, String body);
}
