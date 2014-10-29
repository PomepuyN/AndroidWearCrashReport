package fr.nicolaspomepuy.androidwearcrashreport.mobile;

/**
 * Created by nicolas on 17/08/14.
 */
public class CrashInfo {

    private Throwable throwable;
    private String fingerprint;
    private String model;
    private String manufacturer;
    private String product;
    private final int versionCode;
    private final String versionName;


    private CrashInfo(Builder builder) {
        this.throwable = builder.throwable;
        this.fingerprint = builder.fingerprint;
        this.model = builder.model;
        this.manufacturer = builder.manufacturer;
        this.product = builder.product;
        this.versionCode = builder.versionCode;
        this.versionName = builder.versionName;
    }

    public static class Builder {
        private Throwable throwable;
        private String fingerprint;
        private String model;
        private String manufacturer;
        private String product;
        private String versionName;
        private int versionCode;

        public Builder(Throwable throwable) {
            this.throwable = throwable;
        }

        public Builder fingerprint(String fingerprint) {
            this.fingerprint = fingerprint;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder manufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        public Builder product(String product) {
            this.product = product;
            return this;
        }

        public Builder versionCode(int version) {
            this.versionCode = version;
            return this;
        }

        public Builder versionName(String version) {
            this.versionName = version;
            return this;
        }

        public CrashInfo build() {
            return new CrashInfo(this);
        }


    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getModel() {
        return model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getProduct() {
        return product;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    @Override
    public String toString() {
        return manufacturer + " - " + model + "(" + versionCode + ")";
    }
}
