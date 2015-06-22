package me.justup.upme.api_rpc.response_object.sub_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import me.justup.upme.api_rpc.utils.Constants;

public class InputOutputData implements Serializable {

    @SerializedName(Constants.LAST_NAME)
    private String LastName;

    @SerializedName(Constants.FIRST_NAME)
    private String FirstName;

    @SerializedName(Constants.PATRONYMIC)
    private String Patronymic;

    @SerializedName(Constants.SEX)
    private String sex;

    @SerializedName(Constants.AGE)
    private int age;

    @SerializedName(Constants.EMAIL)
    private String Email;

    @SerializedName(Constants.AMOUNT)
    private int amount;

    public String getLastName() {
        return LastName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getPatronymic() {
        return Patronymic;
    }

    public String getSex() {
        return sex;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return Email;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "InputOutputData{" +
                "LastName='" + LastName + '\'' +
                ", FirstName='" + FirstName + '\'' +
                ", Patronymic='" + Patronymic + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", Email='" + Email + '\'' +
                ", amount=" + amount +
                '}';
    }
}
