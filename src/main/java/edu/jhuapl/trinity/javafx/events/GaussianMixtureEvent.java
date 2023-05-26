package edu.jhuapl.trinity.javafx.events;

/*-
 * #%L
 * trinity-1.0.0-SNAPSHOT
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

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

/**
 * @author Sean Phillips
 */
public class GaussianMixtureEvent extends Event {

    public Object object;
    public static final EventType<GaussianMixtureEvent> NEW_GAUSSIAN_COLLECTION = new EventType(ANY, "NEW_GAUSSIAN_COLLECTION");
    public static final EventType<GaussianMixtureEvent> NEW_GAUSSIAN_MIXTURE = new EventType(ANY, "NEW_GAUSSIAN_MIXTURE");
    public static final EventType<GaussianMixtureEvent> LOCATE_GAUSSIAN_MIXTURE = new EventType(ANY, "LOCATE_GAUSSIAN_MIXTURE");

    public GaussianMixtureEvent(EventType<? extends Event> arg0) {
        super(arg0);
    }

    public GaussianMixtureEvent(EventType<? extends Event> arg0, Object arg1) {
        this(arg0);
        object = arg1;
    }

    public GaussianMixtureEvent(Object arg0, EventTarget arg1, EventType<? extends Event> arg2) {
        super(arg0, arg1, arg2);
        object = arg0;
    }
}
