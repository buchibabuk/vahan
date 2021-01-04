package nic.vahan.form.dobj;

import java.io.Serializable;

public class TmConfigurationOwnerIdentificationDobj implements Serializable {

    private String state_cd;
    private String dl_required;
    private boolean dl_validation_required;
    private String pan_card_mandatory;

    /**
     * @return the state_cd
     */
    public String getState_cd() {
        return state_cd;
    }

    /**
     * @param state_cd the state_cd to set
     */
    public void setState_cd(String state_cd) {
        this.state_cd = state_cd;
    }

    /**
     * @return the dl_required
     */
    public String isDl_required() {
        return dl_required;
    }

    /**
     * @param dl_required the dl_required to set
     */
    public void setDl_required(String dl_required) {
        this.dl_required = dl_required;
    }

    /**
     * @return the dl_validation_required
     */
    public boolean isDl_validation_required() {
        return dl_validation_required;
    }

    /**
     * @param dl_validation_required the dl_validation_required to set
     */
    public void setDl_validation_required(boolean dl_validation_required) {
        this.dl_validation_required = dl_validation_required;
    }

    public String getPan_card_mandatory() {
        return pan_card_mandatory;
    }

    public void setPan_card_mandatory(String pan_card_mandatory) {
        this.pan_card_mandatory = pan_card_mandatory;
    }
}
