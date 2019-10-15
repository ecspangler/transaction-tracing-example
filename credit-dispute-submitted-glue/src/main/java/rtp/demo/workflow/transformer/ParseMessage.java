package rtp.demo.workflow.transformer;

import com.google.gson.Gson;
import org.apache.camel.Body;
import org.example.domain.dispute.Dispute;

import java.io.InputStream;

public class ParseMessage {
    public String process(@Body Dispute body) {

        return "{\"case-data\":"+new Gson().toJson(body)+"}";
    }





}
