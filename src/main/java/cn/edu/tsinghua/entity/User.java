package cn.edu.tsinghua.entity;

import lombok.Data;

/**
 * Created on 2020-11-30.
 * Description:
 *
 * @author iznauy
 */
@Data
public class User {

    private long userId;

    private String username;

    private String password;

    private String address;

}
