package cecs429.variantmethods;

import cecs429.index.DiskPositionalIndex;

/**
 * Wacky variant can be used to calculate WQT, WDT and LD.
 */
public class WackyVariant implements VariantMethodsInterface {

    private double mAveTftd;

    public WackyVariant() {
    }

    public WackyVariant(double aveTftd) {
        mAveTftd = aveTftd;
    }

    @Override
    public float getWQT(int dft, int N) {

        float wqt = (float) Math.log((float) (N - dft) / dft);

        return Math.max(0.1f, wqt);
    }

    @Override
    public float getWDT(int tftd, int docId) {

        float numerator = 1.0f + (float) Math.log(tftd);

        float denominator = 1.0f + (float) Math.log(mAveTftd);

        return numerator / denominator;
    }


    @Override
    public double getLD(String path, int docId) {

        return Math.sqrt(new DiskPositionalIndex(path).getDocByteSize(docId));
    }

}
