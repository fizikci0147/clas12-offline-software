package org.jlab.detector.decode;

import org.jlab.jnp.hipo4.data.Bank;

/**
 *  See:
 *  common-tools/doc
 *  https://logbooks.jlab.org/entry/3531353
 *
 * @author baltzell
 */
public class HelicityState {

    private static final byte UDF=0;
    private static final byte POSITIVE=1;
    private static final byte NEGATIVE=-1;

    // FIXME:  these should go in CCDB
    private static final short HALFADC=2000;
    private static final byte SECTOR=1;
    private static final byte LAYER=1;
    private static final byte HELICITY_COMPONENT=1;
    private static final byte SYNC_COMPONENT=2;
    private static final byte QUARTET_COMPONENT=3;

    private byte helicity=UDF;
    private byte sync=UDF;
    private byte quartet=UDF;

    private byte getState(short ped) {
        if (ped == HALFADC)
            return UDF;
        else
            return ped>HALFADC ? POSITIVE : NEGATIVE;
    }

    public HelicityState(Bank adcBank) {
        for (int ii=0; ii<adcBank.getRows(); ii++) {
            if (adcBank.getInt("sector",ii) != SECTOR) continue;
            if (adcBank.getInt("layer",ii) != LAYER) continue;
            switch (adcBank.getInt("component",ii)) {
                case HELICITY_COMPONENT:
                    this.helicity = this.getState(adcBank.getShort("ped",ii));
                    break;
                case SYNC_COMPONENT:
                    this.sync = this.getState(adcBank.getShort("ped",ii));
                    break;
                case QUARTET_COMPONENT:
                    this.quartet = this.getState(adcBank.getShort("ped",ii));
                    break;
                default:
                    break;
            }
        }
    }

    public boolean equals(HelicityState other) {
        if (this.sync != other.sync) return false;
        if (this.helicity != other.helicity) return false;
        return this.quartet == other.quartet;
    }

    public boolean isValid() {
        return this.helicity!=UDF && this.sync!=UDF && this.quartet!=UDF;
    }

    public byte getHelicity() { return this.helicity; }
    public byte getSync() { return this.sync; }
    public byte getQuartet() { return this.quartet; }

}
