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


    private CrashInfo (Builder builder) {
        this.throwable = builder.throwable;
        this.fingerprint = builder.fingerprint;
        this.model = builder.model;
        this.manufacturer = builder.manufacturer;
        this.product = builder.product;
    }

    public static class Builder {
        private Throwable throwable;
        private String fingerprint;
        private String model;
        private String manufacturer;
        private String product;

        public  Builder(Throwable throwable) {
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
}
