/*-
 * #%L
 * trinity
 * %%
 * Copyright (C) 2021 - 2023 The Johns Hopkins University Applied Physics Laboratory LLC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
module edu.jhuapl.trinity {
    requires java.prefs;
    requires java.logging;
    requires java.datatransfer;
    requires java.desktop;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.fxyz3d.core;
    requires eu.hansolo.fx.charts;
    requires lit.litfx.core;
    requires lit.litfx.controls;
    requires quickhull3d;
    requires commons.math3;
    requires jeromq;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires webcam.capture;
    requires jdk.crypto.ec;
    opens edu.jhuapl.trinity.javafx.components.radial to lit.litfx.controls;
    opens edu.jhuapl.trinity to webcam.capture;
    opens edu.jhuapl.trinity.data to javafx.base;
    opens edu.jhuapl.trinity.data.terrain to javafx.base;
    opens edu.jhuapl.trinity.javafx.controllers to javafx.fxml;
    opens edu.jhuapl.trinity.javafx.javafx3d.particle to java.base;
    exports edu.jhuapl.trinity;
    exports edu.jhuapl.trinity.data;
    exports edu.jhuapl.trinity.data.messages;
    exports edu.jhuapl.trinity.data.files;
    exports edu.jhuapl.trinity.data.terrain;
}
