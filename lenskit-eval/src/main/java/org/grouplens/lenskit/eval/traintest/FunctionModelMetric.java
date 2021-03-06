/*
 * LensKit, an open source recommender systems toolkit.
 * Copyright 2010-2014 LensKit Contributors.  See CONTRIBUTORS.md.
 * Work on LensKit has been funded by the National Science Foundation under
 * grants IIS 05-34939, 08-08692, 08-12148, and 10-17697.
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
package org.grouplens.lenskit.eval.traintest;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.grouplens.lenskit.Recommender;
import org.grouplens.lenskit.eval.Attributed;
import org.grouplens.lenskit.eval.data.traintest.TTDataSet;
import org.grouplens.lenskit.eval.metrics.Metric;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Model metric backed by an arbitrary function.
 *
 * @author <a href="http://www.grouplens.org">GroupLens Research</a>
 * @since 1.1
 */
public class FunctionModelMetric implements Metric<List<Object>> {
    private final List<String> columnHeaders;
    private final Function<Recommender, List<Object>> function;

    public FunctionModelMetric(List<String> columns, Function<Recommender, List<Object>> func) {
        columnHeaders = ImmutableList.copyOf(columns);
        function = func;
    }

    @Override
    public List<String> getColumnLabels() {
        return columnHeaders;
    }

    @Override
    public List<String> getUserColumnLabels() {
        return Collections.emptyList();
    }

    @Override
    public List<Object> createContext(Attributed algorithm, TTDataSet dataSet, Recommender recommender) {
        return function.apply(recommender);
    }

    @Nonnull
    @Override
    public List<Object> measureUser(TestUser user, List<Object> context) {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public List<Object> getResults(List<Object> context) {
        if (context == null) {
            return Lists.transform(columnHeaders, Functions.constant(null));
        } else {
            return context;
        }
    }

    @Override
    public void close() {}
}
