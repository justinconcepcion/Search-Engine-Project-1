package cecs429.variantmethods;

import cecs429.index.DiskPositionalIndex;

/**
 * Default variant can be used to calculate WQT, WDT and LD.
 */
public class DefaultVariant implements VariantMethodsInterface {

    @Override
    public float getWQT(int dft, int N) {

        float f = (float) N / dft;
        return (float) Math.log(1.0f + f);
    }

    @Override
    public float getWDT(int tftd, int docId) {

        return 1 + ((float) Math.log(tftd));
    }

    @Override
    public double getLD(String path, int docId) {

        return new DiskPositionalIndex(path).getLd(docId);
    }
}
