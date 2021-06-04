package com.example.easypass;

public class DocumentUser {
    String Passport_doc;
    String Id_doc;
    String Birthdate_doc;
    String Police_certificate;



    public DocumentUser(String passport_doc, String id_doc, String birthdate_doc, String police_certificate) {
        Passport_doc = passport_doc;
        Id_doc = id_doc;
        Birthdate_doc = birthdate_doc;
        Police_certificate = police_certificate;
    }
    public DocumentUser() {

    }

    public String getPassport_doc() {
        return Passport_doc;
    }

    public void setPassport_doc(String passport_doc) {
        Passport_doc = passport_doc;
    }

    public String getId_doc() {
        return Id_doc;
    }

    public void setId_doc(String id_doc) {
        Id_doc = id_doc;
    }

    public String getBirthdate_doc() {
        return Birthdate_doc;
    }

    public void setBirthdate_doc(String birthdate_doc) {
        Birthdate_doc = birthdate_doc;
    }

    public String getPolice_certificate() {
        return Police_certificate;
    }

    public void setPolice_certificate(String police_certificate) {
        Police_certificate = police_certificate;
    }
}
