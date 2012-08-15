/*
 * LensKit, an open source recommender systems toolkit.
 * Copyright 2010-2012 Regents of the University of Minnesota and contributors
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.grouplens.lenskit.knn.item;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import org.grouplens.lenskit.vectors.SparseVector;

/**
 * Model build strategy that avoids computing similarities between items with
 * disjoint rating sets.
 * @author Michael Ekstrand <ekstrand@cs.umn.edu>
 *
 */
class SparseModelBuildStrategy implements
        ItemItemModelBuildStrategy {
    private final ItemSimilarity similarityFunction;

    SparseModelBuildStrategy(ItemSimilarity sim) {
        Preconditions.checkArgument(sim.isSparse(), "similarity function is not sparse");
        similarityFunction = sim;
    }

    @Override
    public void buildMatrix(ItemItemBuildContext context,
                            SimilarityMatrixAccumulator accum) {
        final LongSortedSet items = context.getItems();
        final boolean symmetric = similarityFunction.isSymmetric();

        LongSet candidates = new LongOpenHashSet();
        LongIterator iit = items.iterator();
        while (iit.hasNext()) {
            final long i = iit.nextLong();
            final SparseVector v = context.itemVector(i);
            final LongIterator uiter = v.keySet().iterator();
            while (uiter.hasNext()) {
                final long user = uiter.nextLong();
                LongSortedSet uitems = context.userItems(user);
                if (symmetric) {
                    uitems = uitems.headSet(i);
                }
                candidates.addAll(uitems);
            }

            final LongIterator iter = candidates.iterator();
            while (iter.hasNext()) {
                final long j = iter.nextLong();

                if (i == j) {
                    continue;
                }

                final double sim =
                        similarityFunction.similarity(j, context.itemVector(j),
                                                      i, v);
                accum.put(i, j, sim);
                if (symmetric) {
                    accum.put(j, i, sim);
                }
            }
            candidates.clear();
        }
    }

    @Override
    public boolean needsUserItemSets() {
        return true;
    }

}