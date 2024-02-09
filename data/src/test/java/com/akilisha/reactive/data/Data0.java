// Source code is decompiled from a .class file using FernFlower decompiler.
package com.akilisha.reactive.data;

public class Data0 extends Data {
    private Observer obs;

    @Override
    public String getName() {
        String var1 = super.getName();
        if (this.obs != null) {
            this.obs.get(this, "name", var1);
        }

        return var1;
    }

    @Override
    public void setName(String var1) {
        String var2 = super.getName();
        if (this.obs != null) {
            this.obs.set(this, "name", var2, var1);
        }

        super.setName(var1);
    }

    @Override
    public boolean isVerified() {
        Object var1 = super.isVerified();
        if (this.obs != null) {
            this.obs.get(this, "verified", var1);
        }

        return (boolean) var1;
    }

    @Override
    public void setVerified(boolean var1) {
        Object var2 = super.isVerified();
        if (this.obs != null) {
            this.obs.set(this, "verified", var2, var1);
        }

        super.setVerified((boolean) var1);
    }

    public void setObs(Observer var1) {
        this.obs = var1;
    }
}
