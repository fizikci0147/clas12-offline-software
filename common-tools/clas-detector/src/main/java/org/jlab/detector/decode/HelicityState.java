package org.jlab.detector.decode;

import org.jlab.jnp.hipo4.data.SchemaFactory;
import org.jlab.jnp.hipo4.data.Bank;

/**
 *  See:
 *  common-tools/clas-detector/doc
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
    private long timestamp=UDF;
    private int  event=UDF;
    private int  run=UDF;

    private byte getFadcState(short ped) {
        if (ped == HALFADC)
            return UDF;
        else
            return ped>HALFADC ? POSITIVE : NEGATIVE;
    }

    public HelicityState(){}

    public HelicityState(Bank adcBank) {
        for (int ii=0; ii<adcBank.getRows(); ii++) {
            if (adcBank.getInt("sector",ii) != SECTOR) continue;
            if (adcBank.getInt("layer",ii) != LAYER) continue;
            switch (adcBank.getInt("component",ii)) {
                case HELICITY_COMPONENT:
                    this.helicity = this.getFadcState(adcBank.getShort("ped",ii));
                    break;
                case SYNC_COMPONENT:
                    this.sync = this.getFadcState(adcBank.getShort("ped",ii));
                    break;
                case QUARTET_COMPONENT:
                    this.quartet = this.getFadcState(adcBank.getShort("ped",ii));
                    break;
                default:
                    break;
            }
        }
    }

    public double getSecondsDelta(HelicityState other) {
        return (this.timestamp-other.timestamp)*4e-9;
    }
    public int getEventDelta(HelicityState other) {
        return this.event-other.event;
    }

    @Override
    public String toString() {
        return String.format("%+d/%+d/%+d",this.helicity,this.sync,this.quartet);
    }

    public Bank makeFlipBank(SchemaFactory schemaFactory) {
        Bank bank=new Bank(schemaFactory.getSchema("HEL::flip"),1);
        bank.putLong("timestamp", 0, this.timestamp);
        bank.putInt("event", 0, this.event);
        bank.putInt("run", 0, this.run);
        bank.putByte("helicity", 0, this.helicity);
        bank.putByte("sync", 0, this.sync);
        bank.putByte("quartet", 0, this.quartet);
        return bank;
    }

    public final boolean isValid() {
        return this.helicity!=UDF &&
               this.sync!=UDF &&
               this.quartet!=UDF;
    }

    public boolean equals(HelicityState other) {
        if (this.helicity != other.helicity) return false;
        if (this.sync != other.sync) return false;
        return this.quartet == other.quartet;
    }

    public void setTimestamp(long timestamp) { this.timestamp=timestamp; }
    public void setEvent(int event) { this.event=event; }
    public void setRun(int run) { this.run=run; }
    public byte getHelicity() { return this.helicity; }
    public byte getSync() { return this.sync; }
    public byte getQuartet() { return this.quartet; }
    public long getTimestamp() { return timestamp; }
    public int  getEvent() { return event; }

}
