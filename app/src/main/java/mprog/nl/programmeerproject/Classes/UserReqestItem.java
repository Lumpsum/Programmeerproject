package mprog.nl.programmeerproject.Classes;

/**
 * UserRequestItem class that gives the data of the specific user that wants to chat with you.
 */
public class UserReqestItem {

    String userName;
    String data;
    String age;
    String gender;
    String description;

    public UserReqestItem(String userName, String data, String age, String gender, String description) {
        super();
        this.userName = userName;
        this.data = data;
        this.age = age;
        this.gender = gender;
        this.description = description;
    }

    public UserReqestItem() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getData() {
        return data;
    }

    public void setData(String messageUser) {
        this.data = data;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getDescription() {
        return description;
    }

}
