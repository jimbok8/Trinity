/*
 * BSD 3-Clause License
 * Copyright (c) 2017, Leland McInnes, 2019 Tag.bio (Java port).
 * See UMAPLicense.txt.
 */
package edu.jhuapl.trinity.utils.umap.metric;

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

/**
 * Special indicator that the metric has been precomputed.
 *
 * @author Sean A. Irvine
 */
public final class PrecomputedMetric extends Metric {

    /**
     * Special indicator that the metric has been precomputed.
     */
    public static final PrecomputedMetric SINGLETON = new PrecomputedMetric();

    private PrecomputedMetric() {
        super(false);
    }

    @Override
    public float distance(final float[] x, final float[] y) {
        throw new IllegalStateException("Attempt to computed distance when distances precomputed");
    }
}
