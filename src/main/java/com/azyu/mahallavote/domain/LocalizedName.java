package com.azyu.mahallavote.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalizedName implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uzLat;
    private String en;
    private String ru;
    private String uzCrl;
    private String krLat;

    public LocalizedName() {}

    public String getByLangKey(String langKey) {
        return switch (langKey) {
            case "uzLat" -> uzLat;
            case "en" -> en;
            case "ru" -> ru;
            case "uzCrl" -> uzCrl;
            case "krLat" -> krLat;
            default -> uzLat;
        };
    }

    public String getUzLat() {
        return uzLat;
    }

    public void setUzLat(String uzLat) {
        this.uzLat = uzLat;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getUzCrl() {
        return uzCrl;
    }

    public void setUzCrl(String uzCrl) {
        this.uzCrl = uzCrl;
    }

    public String getKrLat() {
        return krLat;
    }

    public void setKrLat(String krLat) {
        this.krLat = krLat;
    }
}
