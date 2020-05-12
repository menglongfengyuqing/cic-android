package com.ztmg.cicmorgan.investment.entity;

import java.io.Serializable;

/**
 * 股东信息entity
 * Created by dell on 2018/5/22.
 */

public class ShareholderInformationEntity implements Serializable{

    private String shareholdersType;
    private String shareholdersCertType;
    private String shareholdersName;

    public String getShareholdersType() {
        return shareholdersType;
    }

    public void setShareholdersType(String shareholdersType) {
        this.shareholdersType = shareholdersType;
    }

    public String getShareholdersCertType() {
        return shareholdersCertType;
    }

    public void setShareholdersCertType(String shareholdersCertType) {
        this.shareholdersCertType = shareholdersCertType;
    }

    public String getShareholdersName() {
        return shareholdersName;
    }

    public void setShareholdersName(String shareholdersName) {
        this.shareholdersName = shareholdersName;
    }
}
