package au.edu.unsw.infs3634.unswlearning;

public class UserAccountSettings {
    private String username;
    private String name;
    private int count_level;
    private int count_point;
    private String avatar;
    private String password;

    //Constructor
    public UserAccountSettings() {

    }

    public UserAccountSettings(String username, String name, int count_level, int count_point, String avatar, String password) {
        this.username = username;
        this.name = name;
        this.count_level = count_level;
        this.count_point = count_point;
        this.avatar = avatar;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount_level() {
        return count_level;
    }

    public void setCount_level(int count_level) {
        this.count_level = count_level;
    }

    public int getCount_point() {
        return count_point;
    }

    public void setCount_point(int count_point) {
        this.count_point = count_point;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", count_level=" + count_level +
                ", count_point=" + count_point +
                ", avatar='" + avatar + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
