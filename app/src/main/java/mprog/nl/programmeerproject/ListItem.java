package mprog.nl.programmeerproject;

/**
 * Created by Rick on 1/18/2017.
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getData() {
        return data;
    }

    public void setData(String messageUser) {
        this.data = data;
    }

}
