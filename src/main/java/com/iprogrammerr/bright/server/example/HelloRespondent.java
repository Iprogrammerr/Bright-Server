package com.iprogrammerr.bright.server.example;

import com.iprogrammerr.bright.server.request.MatchedRequest;
import com.iprogrammerr.bright.server.respondent.Respondent;
import com.iprogrammerr.bright.server.response.Response;
import com.iprogrammerr.bright.server.response.template.BadRequestResponse;
import com.iprogrammerr.bright.server.response.template.OkResponse;

public final class HelloRespondent implements Respondent {

	@Override
	public Response response(MatchedRequest request) {
		Response response;
		try {
			int id = request.pathVariables().numberValue("id").intValue();
			String message = "Hello number " + id;
			response = new OkResponse(message);
		} catch (Exception e) {
			response = new BadRequestResponse(e.getMessage());
		}
		return response;
	}
}
