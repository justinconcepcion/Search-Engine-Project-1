package cecs429;

import cecs429.index.DiskPositionalIndex;
import cecs429.index.Posting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface VariantMethodsInterface {

    float getWQT(int dft, int N);

    float getWDT(int tftd, int docId);

    double getLD(int docId);
}

class DefaultVariant implements VariantMethodsInterface {

    private String mPath;

    public DefaultVariant(String path) {
        this.mPath = path;
    }

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
    public double getLD(int docId) {

        return new DiskPositionalIndex(mPath).getLd(docId);
    }
}

class TFIDFVariant implements VariantMethodsInterface {

    private String mPath;

    public TFIDFVariant(String path) {
        this.mPath = path;
    }

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
    public double getLD(int docId) {

        return new DiskPositionalIndex(mPath).getLd(docId);
    }
}


class OkapiVariant implements VariantMethodsInterface {

    private String mPath;

    public OkapiVariant(String path) {
        this.mPath = path;
    }

    @Override
    public float getWQT(int dft, int N) {

        float numerator = (float) N - dft + 0.5f;
        float denominator = dft + 0.5f;

        return Math.max(0.1f, (float) Math.log(numerator / denominator));
    }

    @Override
    public float getWDT(int tftd, int docId) {
        DiskPositionalIndex diskPositionalIndex = new DiskPositionalIndex(mPath);
        float numerator = 2.2f * tftd;

        double docLength = diskPositionalIndex.getDocLenD(docId);
        double docsLengthAverage = diskPositionalIndex.getAvgLenOfCorpus();

        double d = 0.75 * (docLength / docsLengthAverage);
        float denominator = 1.2f * (0.25f + (0.75f * (float) d) + tftd);
        return numerator / denominator;
    }

    @Override
    public double getLD(int docId) {

        return 1.0;
    }

}

class WackyVariant implements VariantMethodsInterface {

    private String mPath;

    public WackyVariant(String path) {
        this.mPath = path;
    }

    @Override
    public float getWQT(int dft, int N) {

        float wqt = (float) Math.log((float) (N - dft) / dft);

        return Math.max(0.1f, wqt);
    }

    @Override
    public float getWDT(int tftd, int docId) {

        float numerator = 1.0f + (float) Math.log(tftd);

        // TODO: get from file
        double aveTftd = new DiskPositionalIndex(mPath).getAvgTftd(docId);
        float denominator = 1.0f + (float) Math.log(aveTftd);

        return numerator / denominator;
    }

    @Override
    public double getLD(int docId) {

        return Math.sqrt(new DiskPositionalIndex(mPath).getDocByteSize(docId));
    }

}
