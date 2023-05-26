/*
 * BSD 3-Clause License
 * Copyright (c) 2017, Leland McInnes, 2019 Tag.bio (Java port).
 * See UMAPLicense.txt.
 */
package edu.jhuapl.trinity.utils.umap;

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

import edu.jhuapl.trinity.utils.umap.metric.Metric;

import java.util.List;
import java.util.Random;

/**
 * Nearest neighbor descent for a specified distance metric.
 *
 * @author Leland McInnes (Python)
 * @author Sean A. Irvine
 * @author Richard Littin
 */
class NearestNeighborDescent {

    final Metric mMetric;
    boolean mVerbose;

    /**
     * Construct a nearest neighbor descent object for the given metric.
     *
     * @param metric distance function
     */
    NearestNeighborDescent(final Metric metric) {
        mMetric = metric;
    }

    void setVerbose(boolean flag) {
        mVerbose = flag;
    }

    Heap descent(final Matrix data, final int nNeighbors, final Random random, final int maxCandidates, final boolean rpTreeInit, final int nIters, final List<FlatTree> forest) {
        return descent(data, nNeighbors, random, maxCandidates, rpTreeInit, nIters, forest, 0.001F, 0.5F);
    }

    Heap descent(final Matrix data, final int nNeighbors, final Random random, final int maxCandidates, final boolean rpTreeInit, final int nIters, final List<FlatTree> forest, final float delta, final float rho) {
        final int nVertices = data.rows();
        final Heap currentGraph = new Heap(data.rows(), nNeighbors);
        for (int i = 0; i < data.rows(); ++i) {
            final float[] iRow = data.row(i);
            for (final int index : Utils.rejectionSample(nNeighbors, data.rows(), random)) {
                final float d = mMetric.distance(iRow, data.row(index));
                currentGraph.push(i, d, index, true);
                currentGraph.push(index, d, i, true);
            }
        }
        UmapProgress.update();

        if (rpTreeInit) {
            for (final FlatTree tree : forest) {
                for (final int[] leaf : tree.getIndices()) {
                    for (int i = 0; i < leaf.length; ++i) {
                        final float[] iRow = data.row(leaf[i]);
                        for (int j = i + 1; j < leaf.length; ++j) {
                            final float d = mMetric.distance(iRow, data.row(leaf[j]));
                            currentGraph.push(leaf[i], d, leaf[j], true);
                            currentGraph.push(leaf[j], d, leaf[i], true);
                        }
                    }
                }
            }
        }
        UmapProgress.update();

        final boolean[] rejectStatus = new boolean[maxCandidates];
        for (int n = 0; n < nIters; ++n) {
            if (mVerbose) {
                Utils.message("NearestNeighborDescent: " + (n + 1) + " / " + nIters);
            }

            final Heap candidateNeighbors = currentGraph.buildCandidates(nVertices, nNeighbors, maxCandidates, random);

            int c = 0;
            for (int i = 0; i < nVertices; ++i) {
                for (int j = 0; j < maxCandidates; ++j) {
                    rejectStatus[j] = random.nextFloat() < rho;
                }

                for (int j = 0; j < maxCandidates; ++j) {
                    final int p = candidateNeighbors.index(i, j);
                    if (p < 0) {
                        continue;
                    }
                    for (int k = 0; k <= j; ++k) {
                        final int q = candidateNeighbors.index(i, k);
                        if (q < 0 || (rejectStatus[j] && rejectStatus[k]) || (!candidateNeighbors.isNew(i, j) && !candidateNeighbors.isNew(i, k))) {
                            continue;
                        }

                        final float d = mMetric.distance(data.row(p), data.row(q));
                        if (currentGraph.push(p, d, q, true)) {
                            ++c;
                        }
                        if (currentGraph.push(q, d, p, true)) {
                            ++c;
                        }
                    }
                }
            }

            if (c <= delta * nNeighbors * data.rows()) {
                UmapProgress.update(nIters - n);
                break;
            }
            UmapProgress.update();
        }
        return currentGraph.deheapSort();
    }


    static Heap initialiseSearch(final List<FlatTree> forest, final Matrix data, final Matrix queryPoints, final int nNeighbors, final NearestNeighborSearch nn, final Random random) {
        final Heap results = new Heap(queryPoints.rows(), nNeighbors);
        nn.randomInit(nNeighbors, data, queryPoints, results, random);
        if (forest != null) {
            for (final FlatTree tree : forest) {
                nn.treeInit(tree, data, queryPoints, results, random);
            }
        }
        return results;
    }
}
