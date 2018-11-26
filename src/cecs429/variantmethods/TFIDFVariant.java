package cecs429.variantmethods;

import cecs429.index.DiskPositionalIndex;

/**
 * TFIDF variant can be used to calculate WQT, WDT and LD.
 */
public class TFIDFVariant implements VariantMethodsInterface {

    @Override
    public float getWQT(int dft, int N) {

        float f = (float) N / dft;
        return (float) Math.log(f);
    }

    @Override
    public float getWDT(int tftd, int docId) {

        return tftd;
    }

    @Override
    public double getLD(String path, int docId) {

        return new DiskPositionalIndex(path).getLd(docId);
    }
}
