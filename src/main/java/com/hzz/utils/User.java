package com.hzz.utils;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/3/7
 */
public class User {

    private Long id;
    private String name;

    @Override
    public String toString() {
        return "user{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
