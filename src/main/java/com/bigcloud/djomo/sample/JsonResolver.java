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

import com.bigcloud.djomo.Json;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Providers;

/**
 * Customize the Json used to serialize objects using a JAX-RS ContextResolver.
 * 
 * This class gets Providers injected by the JAX-RS runtime, and in turn injects
 * the WidgetDao provided by WidgetDaoResolver into the Json when it is lazily
 * constructed. This allows the Json to construct filter instances that require
 * a WidgetDao that is managed by the JAX-RS runtime.
 * 
 * @author Alex Vigdor
 *
 */
public class JsonResolver implements ContextResolver<Json> {
	@Inject
	Providers providers;
	Json json;

	@Override
	public Json getContext(Class<?> type) {
		if (json == null) {
			json = Json.builder()
					.inject(providers
							.getContextResolver(WidgetDao.class, MediaType.WILDCARD_TYPE)
							.getContext(null))
					.build();
		}
		return json;
	}

}
