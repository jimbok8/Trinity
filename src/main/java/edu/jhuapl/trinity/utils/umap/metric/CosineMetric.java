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
 * Cosine distance.
 *
 * @author Sean A. Irvine
 */
public final class CosineMetric extends Metric {

    /**
     * Cosine distance.
     */
    public static final CosineMetric SINGLETON = new CosineMetric();

    private CosineMetric() {
        super(true);
    }

    @Override
    public float distance(final float[] x, final float[] y) {
        double result = 0.0;
        double normX = 0.0;
        double normY = 0.0;
        for (int i = 0; i < x.length; ++i) {
            result += x[i] * y[i];
            normX += x[i] * x[i];
            normY += y[i] * y[i];
        }
        if (normX == 0.0 && normY == 0.0) {
            return 0;
        } else if (normX == 0.0 || normY == 0.0) {
            return 1;
        } else {
            return (float) (1 - (result / Math.sqrt(normX * normY)));
        }
    }
}
