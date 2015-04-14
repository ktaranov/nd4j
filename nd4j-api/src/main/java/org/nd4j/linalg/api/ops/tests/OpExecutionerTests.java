/*
 * Copyright 2015 Skymind,Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.nd4j.linalg.api.ops.tests;

import org.junit.Test;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.FloatBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.exception.IllegalOpException;
import org.nd4j.linalg.api.ops.executioner.OpExecutioner;
import org.nd4j.linalg.api.ops.impl.accum.*;
import org.nd4j.linalg.api.ops.impl.accum.distances.EuclideanDistance;
import org.nd4j.linalg.api.ops.impl.scalar.ScalarAdd;
import org.nd4j.linalg.api.ops.impl.scalar.ScalarMax;
import org.nd4j.linalg.api.ops.impl.scalar.comparison.ScalarGreaterThan;
import org.nd4j.linalg.api.ops.impl.scalar.comparison.ScalarLessThan;
import org.nd4j.linalg.api.ops.impl.transforms.*;
import org.nd4j.linalg.api.ops.impl.transforms.arithmetic.AddOp;
import org.nd4j.linalg.api.ops.impl.transforms.arithmetic.MulOp;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by agibsonccc on 2/22/15.
 */
public abstract class OpExecutionerTests {

    @Test
    public void testCosineSimilarity() {
        INDArray vec1 = Nd4j.create(new float[]{1, 2, 3, 4});
        INDArray vec2 = Nd4j.create(new float[]{1, 2, 3, 4});
        double sim = Transforms.cosineSim(vec1, vec2);
        assertEquals(1, sim, 1e-1);

    }

    @Test
    public void testEuclideanDistance() {
        INDArray arr = Nd4j.create(new double[]{55,55});
        INDArray arr2 = Nd4j.create(new double[]{60,60});
        double result = Nd4j.getExecutioner().execAndReturn(new EuclideanDistance(arr,arr2)).currentResult().doubleValue();
        assertEquals(7.0710678118654755,result,1e-1);

    }

    @Test
    public void testScalarMaxOp() {
        INDArray scalarMax = Nd4j.linspace(1, 6, 6).negi();
        INDArray postMax = Nd4j.ones(6);
        Nd4j.getExecutioner().exec(new ScalarMax(scalarMax, 1));
        assertEquals(scalarMax, postMax);
    }

    @Test
    public void testSetRange() {
        INDArray linspace = Nd4j.linspace(1, 4, 4);
        Nd4j.getExecutioner().exec(new SetRange(linspace, 0, 1));
        for (int i = 0; i < linspace.length(); i++) {
            double val = linspace.getDouble(i);
            assertTrue(val >= 0 && val <= 1);
        }

        INDArray linspace2 = Nd4j.linspace(1, 4, 4);
        Nd4j.getExecutioner().exec(new SetRange(linspace2, 2, 4));
        for (int i = 0; i < linspace2.length(); i++) {
            double val = linspace2.getDouble(i);
            assertTrue(val >= 2 && val <= 4);
        }


    }

    @Test
    public void testNormMax() {
        INDArray arr = Nd4j.create(new float[]{1, 2, 3, 4});
        double normMax = Nd4j.getExecutioner().execAndReturn(new NormMax(arr)).currentResult().doubleValue();
        assertEquals(10, normMax, 1e-1);

    }


    @Test
    public void testNorm2() {
        INDArray arr = Nd4j.create(new float[]{1, 2, 3, 4});
        double norm2 = Nd4j.getExecutioner().execAndReturn(new Norm2(arr)).currentResult().doubleValue();
        assertEquals(5.4772255750516612, norm2, 1e-1);

    }

    @Test
    public void testAdd() {
        OpExecutioner opExecutioner = Nd4j.getExecutioner();
        INDArray x = Nd4j.ones(5);
        INDArray xDup = x.dup();
        INDArray solution = Nd4j.valueArrayOf(5, 2.0);
        opExecutioner.exec(new AddOp(x, xDup, x));
        assertEquals(solution, x);

    }

    @Test
    public void testMul() {
        OpExecutioner opExecutioner = Nd4j.getExecutioner();
        INDArray x = Nd4j.ones(5);
        INDArray xDup = x.dup();
        INDArray solution = Nd4j.valueArrayOf(5, 1.0);
        opExecutioner.exec(new MulOp(x, xDup, x));
        assertEquals(solution, x);

    }


    @Test
    public void testExecutioner() throws IllegalOpException {
        OpExecutioner opExecutioner = Nd4j.getExecutioner();
        INDArray x = Nd4j.ones(5);
        INDArray xDup = x.dup();
        INDArray solution = Nd4j.valueArrayOf(5, 2.0);
        opExecutioner.exec(new AddOp(x, xDup, x));
        assertEquals(solution, x);
        Sum acc = new Sum(x.dup());
        opExecutioner.exec(acc);
        assertEquals(10.0, acc.currentResult().doubleValue(), 1e-1);
        Prod prod = new Prod(x.dup());
        opExecutioner.exec(prod);
        assertEquals(32.0, prod.currentResult().doubleValue(), 1e-1);
    }


    @Test
    public void testMaxMin() {
        OpExecutioner opExecutioner = Nd4j.getExecutioner();
        INDArray x = Nd4j.linspace(1, 5, 5);
        Max max = new Max(x);
        opExecutioner.exec(max);
        assertEquals(5, max.currentResult().doubleValue(), 1e-1);
        Min min = new Min(x);
        assertEquals(1, min.currentResult().doubleValue(), 1e-1);
    }

    @Test
    public void testProd() {
        INDArray linspace = Nd4j.linspace(1, 6, 6);
        Prod prod = new Prod(linspace);
        double prod2 = Nd4j.getExecutioner().execAndReturn(prod).currentResult().doubleValue();
        assertEquals(720, prod2, 1e-1);

    }

    @Test
    public void testSum() {
        INDArray linspace = Nd4j.linspace(1, 6, 6);
        Sum sum = new Sum(linspace);
        double sum2 = Nd4j.getExecutioner().execAndReturn(sum).currentResult().doubleValue();
        assertEquals(21, sum2, 1e-1);


    }


    @Test
    public void testDescriptiveStatsDouble() {
        Nd4j.dtype = DataBuffer.DOUBLE;
        OpExecutioner opExecutioner = Nd4j.getExecutioner();
        INDArray x = Nd4j.linspace(1, 5, 5);

        Mean mean = new Mean(x);
        opExecutioner.exec(mean);
        assertEquals(3.0, mean.currentResult().doubleValue(), 1e-1);

        Variance variance = new Variance(x.dup(), true);
        opExecutioner.exec(variance);
        assertEquals(2.5, variance.currentResult().doubleValue(), 1e-1);

    }


    @Test
    public void testDescriptiveStats() {
        OpExecutioner opExecutioner = Nd4j.getExecutioner();
        INDArray x = Nd4j.linspace(1, 5, 5);

        Mean mean = new Mean(x);
        opExecutioner.exec(mean);
        assertEquals(3.0, mean.currentResult().doubleValue(), 1e-1);

        Variance variance = new Variance(x.dup(), true);
        opExecutioner.exec(variance);
        assertEquals(2.5, variance.currentResult().doubleValue(), 1e-1);

    }

    @Test
    public void testRowSoftmax() {
        OpExecutioner opExecutioner = Nd4j.getExecutioner();
        INDArray arr = Nd4j.linspace(1, 6, 6);
        SoftMax softMax = new SoftMax(arr);
        opExecutioner.exec(softMax);
        assertEquals(1.0, softMax.z().sum(Integer.MAX_VALUE).getDouble(0), 1e-1);


    }

    @Test
    public void testPow() {
        INDArray oneThroughSix = Nd4j.linspace(1, 6, 6);
        Pow pow = new Pow(oneThroughSix, 2);
        Nd4j.getExecutioner().exec(pow);
        INDArray answer = Nd4j.create(new float[]{1, 4, 9, 16, 25, 36});
        assertEquals(answer, pow.z());
    }


    @Test
    public void testComparisonOps() {
        INDArray linspace = Nd4j.linspace(1, 6, 6);
        INDArray ones = Nd4j.ones(6);
        INDArray zeros = Nd4j.zeros(6);
        assertEquals(ones, Nd4j.getExecutioner().execAndReturn(new ScalarGreaterThan(linspace, 0)));
        assertEquals(zeros, Nd4j.getExecutioner().execAndReturn(new ScalarGreaterThan(linspace, 7)));
        assertEquals(zeros, Nd4j.getExecutioner().execAndReturn(new ScalarLessThan(linspace, 0)));
        assertEquals(ones, Nd4j.getExecutioner().execAndReturn(new ScalarLessThan(linspace, 7)));

    }

    @Test
    public void testScalarArithmetic() {
        INDArray linspace = Nd4j.linspace(1, 6, 6);
        INDArray plusOne = Nd4j.linspace(2, 7, 6);
        Nd4j.getExecutioner().exec(new ScalarAdd(linspace, 1));
        assertEquals(plusOne, linspace);
    }


    @Test
    public void testDimensionMax() {
        INDArray linspace = Nd4j.linspace(1, 6, 6).reshape(2, 3);
        int axis = 0;
        INDArray row = linspace.slice(axis);
        Max max = new Max(row);
        double max2 = Nd4j.getExecutioner().execAndReturn(max).currentResult().doubleValue();
        assertEquals(5.0, max2, 1e-1);

        Min min = new Min(row);
        double min2 = Nd4j.getExecutioner().execAndReturn(min).currentResult().doubleValue();
        assertEquals(1.0, min2, 1e-1);
    }


    @Test
    public void testStridedLog() {
        OpExecutioner opExecutioner = Nd4j.getExecutioner();
        INDArray arr = Nd4j.linspace(1, 6, 6).reshape(2, 3);
        INDArray slice = arr.slice(0);
        Log exp = new Log(slice);
        opExecutioner.exec(exp);
        assertEquals(Nd4j.create(new FloatBuffer(new float[]{0.f, 1.09861229f, 1.60943791f})), slice);
    }

    @Test
    public void testStridedExp() {
        OpExecutioner opExecutioner = Nd4j.getExecutioner();
        INDArray arr = Nd4j.linspace(1, 6, 6).reshape(2, 3);
        INDArray slice = arr.slice(0);
        Exp exp = new Exp(slice);
        opExecutioner.exec(exp);
        assertEquals(Nd4j.create(new FloatBuffer(new float[]{2.7182817459106445f, 20.08553695678711f, 148.4131622314453f})), slice);
    }

    @Test
    public void testSoftMax() {
        OpExecutioner opExecutioner = Nd4j.getExecutioner();
        INDArray arr = Nd4j.linspace(1, 6, 6);
        SoftMax softMax = new SoftMax(arr);
        opExecutioner.exec(softMax);
        assertEquals(1.0, softMax.z().sum(Integer.MAX_VALUE).getDouble(0), 1e-1);
    }


    @Test
    public void testDimensionSoftMax() {
        INDArray linspace = Nd4j.linspace(1, 6, 6).reshape(2, 3);
        SoftMax max = new SoftMax(linspace);
        Nd4j.getExecutioner().exec(max, 1);
        assertEquals(linspace.getRow(0).sum(Integer.MAX_VALUE).getDouble(0), 1.0, 1e-1);

    }


}
