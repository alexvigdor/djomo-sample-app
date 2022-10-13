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

import java.util.Map;
import java.util.UUID;

import com.bigcloud.djomo.Models;
import com.bigcloud.djomo.annotation.Parse;
import com.bigcloud.djomo.annotation.Visit;
import com.bigcloud.djomo.api.Model;
import com.bigcloud.djomo.filter.TypeParserTransform;
import com.bigcloud.djomo.filter.TypeVisitorTransform;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Providers;

/**
 * <p>
 * This service offers two approaches to storing and retrieving widgets. A basic
 * approach simply passes a raw widget model through to the DAO that contains a
 * list of uuids for related widgets.
 * <p>
 * The collapse and expand endpoints demonstrate how to use djomo type
 * transforms to have the JSON REST API deal in a list of nested widgets,
 * instead of just UUIDs.
 * 
 * @author Alex Vigdor
 *
 */
@Path("widgets")
public class WidgetService {
	WidgetDao dao;

	@Inject
	public WidgetService(Providers providers) {
		this.dao = providers.getContextResolver(WidgetDao.class, MediaType.WILDCARD_TYPE).getContext(null);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Widget update(@Valid Widget widget) {
		dao.set(widget.id(), widget);
		return widget;
	}

	@POST
	@Path("collapse")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Widget collapse(@Parse(WidgetCollapser.class) @Valid Widget widget) {
		dao.set(widget.id(), widget);
		return widget;
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Widget getWidget(@PathParam("id") UUID id) {
		return dao.get(id);
	}

	@GET
	@Path("{id}/expand")
	@Visit(value = WidgetExpander.class, path = "relatedWidgets[*]")
	@Produces(MediaType.APPLICATION_JSON)
	public Widget expandWidget(@PathParam("id") UUID id) {
		return dao.get(id);
	}

	/**
	 * 
	 * Reconstructs a nested widget model from UUID foreign keys during
	 * serialization.
	 *
	 */
	public static class WidgetExpander extends TypeVisitorTransform<UUID> {
		WidgetDao dao;

		public WidgetExpander(WidgetDao dao) {
			this.dao = dao;
		}

		@Override
		public Widget transform(UUID in) {
			return dao.get(in);
		}

	}

	/**
	 * 
	 * Deconstructs a nested widget model into component objects for storage with
	 * uuid pointers during deserialization.
	 * 
	 * A parser that encounters an object structure when expecting a UUID will
	 * return a Map; this transform takes that map and converts it to the
	 * {@code Model<Widget>} it acquires during construction from the injected
	 * Models. It leverages the WidgetDao injected thanks to JsonResolver to store
	 * the nested objects, and feeds the UUID back into the normalized model for the
	 * parent widget.
	 *
	 */
	public static class WidgetCollapser extends TypeParserTransform<Map, UUID> {
		Model<Widget> widgetModel;
		WidgetDao dao;

		public WidgetCollapser(WidgetDao dao, Models models) {
			this.widgetModel = models.get(Widget.class);
			this.dao = dao;
		}

		@Override
		public UUID transform(Map in) {
			Widget w = widgetModel.convert(in);
			dao.set(w.id(), w);
			return w.id();
		}

	}
}
