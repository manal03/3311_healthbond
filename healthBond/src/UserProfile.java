//class - blueprint variables
public class UserProfile {
    private int weight;
    private int height;
    private String sex;
    private String dob;
    private String name;
    //constructor
    public UserProfile(String dob,int weight, int height, String sex, String name){
        this.weight = weight;
        this.height = height;
        this.sex = sex;
        this.dob = dob;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}

