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

import java.util.List;
import java.util.UUID;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.bigcloud.djomo.rs.JsonBodyReader;
import com.bigcloud.djomo.rs.JsonBodyWriter;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;

public class TestWidgets extends JerseyTestNg.ContainerPerClassTest {
	Widget legs = new Widget(UUID.randomUUID(), "Legs", 4.0, 20.0, 4.0, 5.5, List.of());
	Widget top = new Widget(UUID.randomUUID(), "Top", 60.0, 2.0, 40.0, 41.0, List.of());
	Widget table = new Widget(UUID.randomUUID(), "Table", 60.0, 22.0, 40.0, 63.0, List.of(legs.id(), top.id()));
	ExpandedWidget expandedTable = new ExpandedWidget(table.id(), "Table", 60.0, 22.0, 40.0, 63.0, table.getVolume(),
			List.of(legs, top));

	@Override
	protected Application configure() {
		return new SampleApplication();
	}

	@Override
	protected void configureClient(ClientConfig config) {
		config.register(JsonBodyReader.class);
		config.register(JsonBodyWriter.class);
	}

	@Test
	public void testWidget() {
		Widget resp = target("/widgets").request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(table, MediaType.APPLICATION_JSON), Widget.class);
		Assert.assertEquals(table, resp);
		resp = target("/widgets/" + table.id()).request(MediaType.APPLICATION_JSON).get(Widget.class);
		Assert.assertEquals(table, resp);
	}

	@Test
	public void testWidgetExpandCollapse() {
		Widget resp = target("/widgets/collapse").request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(expandedTable, MediaType.APPLICATION_JSON), Widget.class);
		Assert.assertEquals(table, resp);
		ExpandedWidget rt = target("/widgets/" + table.id() + "/expand").request(MediaType.APPLICATION_JSON)
				.get(ExpandedWidget.class);
		Assert.assertEquals(expandedTable, rt);
	}

	@Test(expectedExceptions = BadRequestException.class)
	public void testInvalidWidget() {
		Widget wrong = new Widget(UUID.randomUUID(), "Legs", -4.0, 20.0, 4.0, 5.5, List.of());
		target("/widgets").request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(wrong, MediaType.APPLICATION_JSON), Widget.class);
	}
}
