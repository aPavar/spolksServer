package com.company.KitOfHeaders;

import com.company.Header.Header;
import com.company.SpecialData.SpecialData;

/**
 * Created by apava on 25.09.2016.
 */
public class KitOfHeaders {
    int typeHeader;



    Header header;
    SpecialData specialData;

    public KitOfHeaders(Header header) {
        typeHeader=0;
        this.header = header;
    }

    public KitOfHeaders(){

    }

    public int getTypeHeader() {
        return typeHeader;
    }

    public void setTypeHeader(int typeHeader) {
        this.typeHeader = typeHeader;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public SpecialData getSpecialData() {
        return specialData;
    }

    public void setSpecialData(SpecialData specialData) {
        this.specialData = specialData;
    }

}
