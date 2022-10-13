/*******************************************************************************
 * Copyright 2022 Alex Vigdor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.bigcloud.djomo.sample;

import jakarta.ws.rs.core.Response;

/**
 * If you would like to try and break things, this provides helpful logging of
 * 500 response causes
 * 
 * @author Alex Vigdor
 *
 */
public class ErrorMapper implements jakarta.ws.rs.ext.ExceptionMapper<Throwable> {
	@Override
	public Response toResponse(Throwable t) {
		t.printStackTrace();
		return Response.serverError()
				.entity(t.getMessage())
				.build();
	}

}
