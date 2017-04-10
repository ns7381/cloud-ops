import lombok.ToString;

/**
 * Created by Nathan on 2017/4/10.
 */
@ToString
public class User {
    String firstName;
    String secondName;
    int age;

    private User(UserBuilder userBuilder) {
        this.firstName = userBuilder.firstName;
        this.secondName = userBuilder.secondName;
        this.age = userBuilder.age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static class UserBuilder {
        String firstName;
        String secondName;
        int age;

        public UserBuilder(String firstName, String secondName) {
            this.firstName = firstName;
            this.secondName = secondName;
        }

        public UserBuilder age(int age) {
            this.age = age;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }


    public static UserBuilder builder(String firstName, String secondName) {
        return new UserBuilder(firstName, secondName);
    }
    public static void main(String[] args) {
        System.out.printf(User.builder("nathan","test").age(12).build().toString());
    }
}
