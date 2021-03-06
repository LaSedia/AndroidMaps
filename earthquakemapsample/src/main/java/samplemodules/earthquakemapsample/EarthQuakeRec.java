package samplemodules.earthquakemapsample;

/**
 * Created by fbald on 05/11/2016.
 */

public class EarthQuakeRec {
    private double lat, lng, magnitude;

    protected EarthQuakeRec(double lat, double lng, double magnitude) {
        super();
        this.lat = lat;
        this.lng = lng;
        this.magnitude = magnitude;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double getMagnitude() {
        return magnitude;
    }
}
