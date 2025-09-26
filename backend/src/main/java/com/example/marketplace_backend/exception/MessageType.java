package com.example.marketplace_backend.exception;

import lombok.Getter;

@Getter
public enum MessageType {

    No_Record_Exist("1004","kayıt bulunamadı"),
    Token_Expired("1005","token süresi dolmuş"),
    UserName_Not_Found("1006","username bulunamadı"),
    Username_Or_Password_Invalid("1007","kullanıcı adı veya şifre hatalı"),
    Refresh_Token_Not_Found("1008","refresh token bulunamadı"),
    Refresh_Token_Expired("1009","refresh token süresi dolmuş"),
    General_Exception("9999","genel bir hata oluştu"),
    Authentication_Error("1010","kimlik doğrulama hatası");

    private String code;

    private String message;

    MessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
