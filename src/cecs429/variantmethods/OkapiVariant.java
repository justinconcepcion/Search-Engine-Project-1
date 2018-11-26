package cecs429.variantmethods;

/**
 * Okapi variant can be used to calculate WQT, WDT and LD.
 */
public class OkapiVariant implements VariantMethodsInterface {

    private int mDocLength;
    private double mDocLengthAve;

    public OkapiVariant() {

    }

    public OkapiVariant(int docLength, double docLengthAve) {
        mDocLength = docLength;
        mDocLengthAve = docLengthAve;
    }

    @Override
    public float getWQT(int dft, int N) {

        float numerator = (float) N - dft + 0.5f;
        float denominator = dft + 0.5f;

        return Math.max(0.1f, (float) Math.log(numerator / denominator));
    }

    @Override
    public float getWDT(int tftd, int docId) {
        float numerator = 2.2f * tftd;
        double d = 0.75 * (double) (mDocLength / mDocLengthAve);
        float denominator = 1.2f * (0.25f + (0.75f * (float) d) + tftd);
        return numerator / denominator;
    }

    @Override
    public double getLD(String path, int docId) {

        return 1.0;
    }

}
