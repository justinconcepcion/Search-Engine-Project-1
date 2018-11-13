package cecs429;

import cecs429.index.Posting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface VariantMethodsInterface {

    float getWQT(int dft, int N);

    float getWDT(int tftd, int docId);

    float getLD(int docId);
}

class DefaultVariant implements VariantMethodsInterface {

    @Override
    public float getWQT(int dft, int N) {

        float f = (float) N / dft;
        return (float) Math.log(1.0f + f);
    }

    @Override
    public float getWDT(int tftd, int docId) {

        return 1.0f + ((float) Math.log(tftd));
    }

    @Override
    public float getLD(int docId) {
        return 0;
    }
}