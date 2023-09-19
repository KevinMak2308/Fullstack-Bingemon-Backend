package delta.fullstackbingemonbackend.model;

public class User {
    private Integer id;

    public User(Integer id) {
        this.id = id;
    }

    public int getUserId() {
        return id;
    }

    public void setUserId(Integer id) {
        this.id = id;
    }
}
