/*
 * BSD 3-Clause License
 * Copyright (c) 2017, Leland McInnes, 2019 Tag.bio (Java port).
 * See UMAPLicense.txt.
 */
package edu.jhuapl.trinity.utils.umap;

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
 * Container for a hyperplane.
 *
 * @author Leland McInnes (Python)
 * @author Sean A. Irvine
 * @author Richard Littin
 */
class Hyperplane {

    //private final int[] mInds;
    private final float[] mData;
    private final int[] mShape;

    Hyperplane(final int[] inds, final float[] data) {
        //mInds = inds;
        mData = data;
        mShape = inds == null ? new int[]{data.length} : new int[]{inds.length, 2};
    }

    Hyperplane(final float[] data) {
        this(null, data);
    }

    public float[] data() {
        return mData;
    }

    public int[] shape() {
        return mShape;
    }
}
