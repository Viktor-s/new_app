package me.justup.upme.entity;

/*
Формат ответа - успешно {
        "status": "ok",
        "file_hash": "3fe213eab39aaa81a3e6b317637c9b50"
    }

Формат ответа - ошибка {
        "status": "fail",
        "reason": "FILE_IS_ABSENT"
    }
*/
public class SendFileToCloudResponse {
    public boolean status = false;
    public String file_hash = "";
    public String reason = "";

}
