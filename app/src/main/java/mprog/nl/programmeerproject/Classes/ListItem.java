package mprog.nl.programmeerproject.Classes;

/**
 * Basic ListItem class that holds the username and his id in order for the flow of
 * the activity to keep functioning.
 */

public class ListItem {

    String userName;
    String data;

    public ListItem(String userName, String data) {
        super();
        this.userName = userName;
        this.data = data;
    }

    public ListItem() {

    }

    public String getUserName() {
        return userName;
    }

    public String getData() {
        return data;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setData(String messageUser) {
        this.data = data;
    }

}
