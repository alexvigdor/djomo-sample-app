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

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Our demo domain model; notice the {@code List<UUID>} relatedWidgets. The
 * WidgetService offers some special endpoints to convert those UUIDs to other
 * Widgets loaded from or stored to a WidgetDao.
 * 
 * @author Alex Vigdor
 *
 */
public record Widget(@NotNull UUID id, @NotNull String name, @Positive double width, @Positive double height,
		@Positive double depth, @DecimalMin("0.1") @DecimalMax("100.0") double weight, List<UUID> relatedWidgets) {
	public double getVolume() {
		return width * height * depth;
	}
}
