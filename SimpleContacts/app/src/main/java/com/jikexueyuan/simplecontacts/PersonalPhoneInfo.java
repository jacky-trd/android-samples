package com.jikexueyuan.simplecontacts;

/**.
 * 该类封装了通讯录中每一条记录的姓名和电话号码
 */
public class PersonalPhoneInfo {
    private String phoneName;
    private String phoneNumber;

    public PersonalPhoneInfo(String phoneName, String phoneNumber) {
        setPhoneName(phoneName);
        setPhoneNumber(phoneNumber);
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}