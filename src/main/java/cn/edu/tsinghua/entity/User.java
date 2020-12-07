package cn.edu.tsinghua.entity;

import lombok.*;

import java.util.Objects;

/**
 * Created on 2020-11-30.
 * Description:
 *
 * @author iznauy
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private long userId;

    private String username;

    private String password;

    private String address;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s", userId, username, password, address);
    }

    public static User fromString(String source) {
        String[] fields = source.split(",");
        assert fields.length == 4;
        return new User(Long.valueOf(fields[0]), fields[1], fields[2], fields[3]);
    }

}
